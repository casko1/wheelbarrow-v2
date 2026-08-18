# ---------- JAVA BUILD ----------
FROM gradle:9.4.0-jdk25 AS java-builder
WORKDIR /project

COPY build.gradle settings.gradle gradle /project/
RUN gradle build -x test || true

COPY . .
RUN gradle clean build


# ---------- YT-CIPHER PREP ----------
FROM denoland/deno:alpine AS yt-cipher-builder
WORKDIR /yt-cipher

RUN apk add --no-cache git

RUN git clone https://github.com/kikkia/yt-cipher.git .
RUN git clone https://github.com/yt-dlp/ejs.git && \
    cd ejs && \
    git checkout cd4e87f52e87ab6d8b318fd3a817adda6fafa8dc

RUN deno run --allow-read --allow-write ./scripts/patch-ejs.ts


# ---------- PYTHON BUILDER (Python 3.12 for binary wheels) ----------
FROM python:3.12-slim AS python-builder
WORKDIR /app/python-api

COPY python-api/requirements.txt .

# Installs instantly using pre-compiled wheels from PyPI
RUN python -m venv /app/python-api/venv && \
    /app/python-api/venv/bin/pip install --upgrade pip && \
    /app/python-api/venv/bin/pip install --no-cache-dir -r requirements.txt


# ---------- FINAL RUNTIME ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Install runtime dependencies including python 3.12
RUN apt-get update && \
    apt-get install -y ffmpeg python3.12 python3.12-venv tini && \
    rm -rf /var/lib/apt/lists/*

# Copy Deno binary
COPY --from=denoland/deno:alpine /bin/deno /usr/local/bin/deno

# Java artifact
COPY --from=java-builder /project/build/libs/*.jar /app/app.jar

# YT-Cipher source
COPY --from=yt-cipher-builder /yt-cipher /app/yt-cipher

# Copy virtual environment directly from Python builder stage
COPY --from=python-builder /app/python-api/venv /app/python-api/venv
COPY python-api /app/python-api

ENV OVERRIDE_PLAYER_VARIANT=IAS

EXPOSE 5000
EXPOSE 8001

ENTRYPOINT ["/usr/bin/tini","--"]

CMD ["sh", "-c", "\
java -jar /app/app.jar & \
/app/python-api/venv/bin/uvicorn python-api.app:app --host 0.0.0.0 --port 5000 & \
deno run --allow-net --allow-read --allow-write --allow-env /app/yt-cipher/server.ts \
"]