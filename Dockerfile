# ---------- JAVA BUILD ----------
FROM gradle:9.4.0-jdk25 AS java-builder
WORKDIR /project

# Cache Gradle dependencies
COPY build.gradle settings.gradle gradle /project/
RUN gradle build -x test || true

# Copy rest of source and build
COPY . .
RUN gradle clean build


# ---------- YT-CIPHER PREP ----------
FROM denoland/deno:alpine AS yt-cipher-builder
WORKDIR /yt-cipher

RUN apk add --no-cache git && \
    git clone https://github.com/kikkia/yt-cipher.git . && \
    git clone https://github.com/yt-dlp/ejs.git && \
    cd ejs && \
    git checkout cd4e87f52e87ab6d8b318fd3a817adda6fafa8dc

RUN deno run --allow-read --allow-write ./scripts/patch-ejs.ts


# ---------- FINAL RUNTIME ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ffmpeg \
        python3 \
        python3-venv \
        python3-pip \
        tini && \
    rm -rf /var/lib/apt/lists/*

# Copy Deno binary from builder
COPY --from=denoland/deno:alpine /bin/deno /usr/local/bin/deno

# Create Python Virtual Environment
RUN python3 -m venv /app/python-api/venv

# CACHE OPTIMIZATION: Copy ONLY requirements.txt first
# This ensures pip install only runs when requirements.txt changes!
COPY python-api/requirements.txt /app/python-api/requirements.txt

# Install Python requirements using pip cache
RUN --mount=type=cache,target=/root/.cache/pip \
    /app/python-api/venv/bin/pip install --upgrade pip setuptools wheel && \
    /app/python-api/venv/bin/pip install --prefer-binary -r /app/python-api/requirements.txt

# Copy Java artifact
COPY --from=java-builder /project/build/libs/*.jar /app/app.jar

# Copy yt-cipher prepared source
COPY --from=yt-cipher-builder /yt-cipher /app/yt-cipher

# Copy Python application source code (AFTER pip install)
COPY python-api /app/python-api

ENV OVERRIDE_PLAYER_VARIANT=IAS

EXPOSE 5000
EXPOSE 8001

ENTRYPOINT ["/usr/bin/tini", "--"]

CMD ["sh", "-c", "\
java -jar /app/app.jar & \
/app/python-api/venv/bin/uvicorn python-api.app:app --host 0.0.0.0 --port 5000 & \
deno run --allow-net --allow-read --allow-write --allow-env /app/yt-cipher/server.ts \
"]