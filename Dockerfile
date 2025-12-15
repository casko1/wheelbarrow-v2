FROM amazoncorretto:21-alpine AS builder
RUN apk add --no-cache gradle
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN gradle clean build

FROM amazoncorretto:21-alpine AS runtime
RUN mkdir /app
WORKDIR /app

COPY --from=builder /project/build/libs/*.jar /app/app.jar
COPY python-api /app/python-api
RUN apk add --no-cache \
    ffmpeg \
    python3 \
    py3-pip && \
    python3 -m venv /app/python-api/venv && \
    /app/python-api/venv/bin/pip install --upgrade pip && \
    /app/python-api/venv/bin/pip install -r /app/python-api/requirements.txt

EXPOSE 5000
CMD ["sh", "-c", "java -jar app.jar & /app/python-api/venv/bin/uvicorn python-api.app:app --port 5000"]
