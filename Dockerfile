FROM gradle:7.5.1-jdk17

RUN apt-get update && apt-get install -qq -y --no-install-recommends

ENV INSTALL_PATH /reactive-bingo

RUN mkdir $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY . .