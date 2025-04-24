FROM golang:1.24-alpine

WORKDIR /app

RUN go install github.com/minio/mc@latest

COPY ./configure_minio.sh ./
RUN ["chmod", "+x", "./configure_minio.sh"]

ENTRYPOINT ["/app/configure_minio.sh"]
