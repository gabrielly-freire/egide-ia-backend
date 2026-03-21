FROM ubuntu:latest
LABEL authors="gabrielly"

ENTRYPOINT ["top", "-b"]