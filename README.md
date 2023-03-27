# TodoService

Yet another todo service. Yes, you can find two other implementations of this
task [here](https://github.com/abondar24/TodoList)
and [here](https://github.com/abondar24/ServerlessAI/tree/master/TodoList)

## Technical stack

- JDK 17
- Gradlew 7.2
- Spring Boot 2.6.3
- Junit 5 (included in spring boot)
- Mockito (included in spring boot)
- H2 (included in spring boot)
- Project Lombok 6.3.0
- Flyway 8.4.2
- Docker 19

## Build,test and run

### Build

- Build the app using gradle wrapper

```
./gradlew clean build
```

- Build native image for GraalVM
```
./gradlew clean nativeCompile
```

- Build docker image

```
./gradlew bootBuildImage
```

### Test

```
./gradlew clean test
```

For native tests
```
./gradlew clean nativeTest
```

### Run

- Run the app using gradlew

```
./gradlew bootRun -PprofileName
```

- Run the app via jar

```
java -jar -Dspring.profiles.active=profileName build/libs/TodoService-1.0-SNAPSHOT.jar
```
- Run native image via gradle
```
./gradlew nativeRun
```

- Run native image in cmd
```
./build/native/nativeCompile/todoservice
```

- Run a docker image

```
`docker run -d --name todo  -p 8080:8080 abondar/todoservice`
```
Note: if no profile is set, directories for logs should be created 

## Profiles

- default 
- dev
- elk 
- docker

## Access

- DB console: http://localhost:8080/h2
- OpenAPI specification: http://localhost:8080/docs
- Swagger UI: http://localhost:8080/swagger
- Actuator: http://localhost:8080/actuator

Note: check thoroughly the api documentation before using the service.

## Assumptions

- We can't edit description of past due items
- We can change status of past due item only to done
- Done item can be moved to not done state, but their complete time is reset
- Done items are not updated by automatic past due update
- Done items can't have their description modified

## Logging

Local ELK stack was setup using this repo https://github.com/deviantony/docker-elk

Log data is sent via filebeat
