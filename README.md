# Partido Server

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/partidodev/partido-server/CI?style=flat-square)
![Github All Contributors](https://img.shields.io/github/all-contributors/partidodev/partido-server/main?style=flat-square)

This is the backend server application of the Partido platform written in Java with the Spring Framework.

Partido itself is a platform independent App for sharing group expenses.

## How to contribute to the project

You can contribute in various ways - even if you're not a software developer. You can translate Partido to your language, report bugs, request features or code. Please check the [contribution guidelines](https://github.com/partidodev/partido-server/blob/main/CONTRIBUTING.md) to start.

## How to run the server

In the project root folder, run:

```
mvn spring-boot:run
```

or just press the IDE's run button.

The server will listen on Port 8080 per default (defined in `./src/main/resources/application.properties`).

## API

The server does not allow normal access via web browser, except for verifying new user accounts. All operations must be done with a REST client. Normally this would be the [Partido Client App](https://github.com/partidodev/partido-client) but for testing you can use any REST tool like [Insomnia](https://github.com/Kong/insomnia), Postman, etc.

_OpenAPI Spec of the server is on the To Do List..._

## Database

The server needs an already existing PostgreSQL database.

The defaults defined in `./src/main/resources/application.properties` are:

```
## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url=jdbc:postgresql://localhost:5432/partido-server
spring.datasource.username=postgres
spring.datasource.password=postgres
```

You can override these properties with custom start parameters.

## Mail sending on local machine

Per default, the server tries to send emails to localhost on Port 25 without any credentials.
If you don't have a (fake) Mailserver installed, the console will output some specific errors, you can ignore. If you want to get those emails, you can use a tool like [FakeSMTP](http://nilhcem.com/FakeSMTP/).

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://www.fosforito.de"><img src="https://avatars3.githubusercontent.com/u/5000255?v=4" width="100px;" alt=""/><br /><sub><b>Jens Wagner</b></sub></a><br /><a href="https://github.com/jenslw/partido-server/commits?author=jenslw" title="Code">💻</a> <a href="#translation-jenslw" title="Translation">🌍</a> <a href="#infra-jenslw" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="#maintenance-jenslw" title="Maintenance">🚧</a> <a href="https://github.com/jenslw/partido-server/commits?author=jenslw" title="Tests">⚠️</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
