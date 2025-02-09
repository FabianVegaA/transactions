from openjdk:21-jdk-bullseye as build

env APP_HOME=/root/dev/app
run mkdir -p $APP_HOME/src/main/java
workdir $APP_HOME

copy build.gradle.kts gradlew gradlew.bat settings.gradle.kts $APP_HOME
copy gradle $APP_HOME/gradle

run set -ex \
    && apt-get update \
    && apt-get install findutils -y \
    && ./gradlew build -x test --continue

copy . .

run set -ex \
    && ./gradlew build

from openjdk:21-jdk-bullseye

workdir /root/

copy --from=build /root/dev/app/build/libs/demo-0.0.1-SNAPSHOT.jar .

expose 8080

cmd ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
