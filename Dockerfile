FROM gradle:8.7-jdk21 AS builder
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN gradle clean build

FROM amazoncorretto:21 AS runtime
RUN mkdir /app
WORKDIR /app

COPY --from=builder /project/build/libs/*.jar /app/app.jar
COPY python-api /app/python-api
RUN yum -y update && \
    yum install -y ffmpeg && \
    yum install -y python3 python3-venv && \
    python3 -m venv /app/python-api/venv && \
    /app/python-api/venv/bin/pip install --upgrade pip && \
    /app/python-api/venv/bin/pip install -r /app/python-api/requirements.txt

EXPOSE 5000
CMD ["sh", "-c", "java -jar app.jar & /app/python-api/venv/bin/uvicorn python-api.app:app --port 5000"]