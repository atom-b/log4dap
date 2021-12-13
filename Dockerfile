FROM gradle:6.9.1-jdk11-alpine as build

COPY --chown=gradle:gradle . /home/gradle/proj
WORKDIR /home/gradle/proj
RUN gradle build installDist --no-daemon

FROM openjdk:14-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY --from=build /home/gradle/proj/app/build/install/log4dap ./log4dap
EXPOSE 1337
ENTRYPOINT exec /bin/sh ./log4dap/bin/log4dap
