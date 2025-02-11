FROM eclipse-temurin:21-jre-alpine

VOLUME /tmp

# Переносим приложение в Docker-контейнер
COPY target/*.jar app.jar

# Переносим скрипт запуска в Docker-контейнер
COPY docker/entrypoint.sh /sh/entrypoint.sh

USER root
RUN chmod +x /sh/*.sh
#USER non-root


