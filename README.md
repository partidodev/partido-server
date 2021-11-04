# Partido Server

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/partidodev/partido-server/CI?style=flat-square)

This is the backend server application of the Partido platform written in Java with the Spring Framework.

Partido itself is a platform independent App for sharing group expenses.

## How to contribute to the project

You can contribute in various ways - even if you're not a software developer. You can translate Partido to your language, report bugs, request features or code. Please check the [contribution guidelines](https://github.com/partidodev/partido-server/blob/main/CONTRIBUTING.md) to start.

## How to run the server

Go into the project root folder and run

```
mvn spring-boot:run
```
to start the spring application, or just press the IDE's run button.

The server will listen on Port 8080 per default (defined in `./src/main/resources/application.properties`).

## API

The server does not allow normal access via web browser, except for verifying new user accounts. All operations must be done with a REST client. Normally this would be the [Partido Client App](https://github.com/partidodev/partido-client) but for testing you can use any REST tool like [Insomnia](https://github.com/Kong/insomnia), Postman, etc.

_OpenAPI Spec of the server is on the To Do List..._

## Database

The server needs an already existing PostgreSQL database. 

If you want to run PostgreSQL with Docker, see the next sub-section.

The defaults defined in `./src/main/resources/application.properties` are:

```
## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url=jdbc:postgresql://localhost:5433/partido
spring.datasource.username=postgres
spring.datasource.password=postgres
```

You can override these properties with custom start parameters.

### Run PostgreSQL in a Docker container

To create a postgres container, you can go into the project root folder and run:

```
docker-compose -f src/main/postgres.yml up -d
```

## Mail sending on local machine

Per default, the server tries to send emails to localhost on Port 25 without any credentials.
If you don't have a (fake) Mailserver installed, the console will output some specific errors, you can ignore. If you want to get those emails, you can use a tool like [FakeSMTP](http://nilhcem.com/FakeSMTP/).
