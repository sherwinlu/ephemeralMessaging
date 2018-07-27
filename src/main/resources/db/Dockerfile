## MySQL 8.0

## Pull the mysql:latest image
FROM mysql:latest

MAINTAINER sherwin

ENV MYSQL_DATABASE=fitnesspaldb \
    MYSQL_ROOT_PASSWORD=abcd1234

COPY schema.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
