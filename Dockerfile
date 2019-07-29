FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080

WORKDIR /home/oerebclient

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /home/oerebclient/app/lib
COPY ${DEPENDENCY}/META-INF /home/oerebclient/app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /home/oerebclient/app
RUN chown -R 1001:0 /home/oerebclient && \
    chmod -R g=u /home/oerebclient

USER 1001

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-cp","/home/oerebclient/app:/home/oerebclient/app/lib/*","ch.so.agi.oereb.wicketclient.OerebWicketClientApplication"]
