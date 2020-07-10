FROM java:openjdk-8u111-alpine

RUN mkdir /app

WORKDIR /app

COPY target/wxshop-order-0.0.1-SNAPSHOT.jar /app

EXPOSE 8081

CMD [ "java", "-jar", "wxshop-order-0.0.1-SNAPSHOT.jar" ]
