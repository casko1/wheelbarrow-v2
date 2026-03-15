# ---------- JAVA BUILD ----------
FROM gradle:8.7-jdk21 AS java-builder
WORKDIR /project

# cache Gradle dependencies
COPY build.gradle settings.gradle gradle /project/
RUN gradle build -x test || true

# copy the rest of the source
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


# ---------- FINAL RUNTIME ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y ffmpeg python3 python3-venv tini && \
    rm -rf /var/lib/apt/lists/*

# copy deno binary
COPY --from=denoland/deno:alpine /usr/bin/deno /usr/local/bin/deno

# java artifact
COPY --from=java-builder /project/build/libs/*.jar /app/app.jar

# yt-cipher prepared source
COPY --from=yt-cipher-builder /yt-cipher /app/yt-cipher

# python api
COPY python-api /app/python-api

# python environment
RUN python3 -m venv /app/python-api/venv && \
    /app/python-api/venv/bin/pip install --upgrade pip && \
    /app/python-api/venv/bin/pip install -r /app/python-api/requirements.txt

ENV OVERRIDE_PLAYER_VARIANT=IAS

EXPOSE 5000
EXPOSE 8001

ENTRYPOINT ["/usr/bin/tini","--"]

CMD ["sh", "-c", "\
java -jar /app/app.jar & \
/app/python-api/venv/bin/uvicorn python-api.app:app --host 0.0.0.0 --port 5000 & \
deno run --allow-net --allow-read --allow-env /app/yt-cipher/main.ts \
"]