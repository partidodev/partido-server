# Partido Server
![CI](https://github.com/jens-wagner/partido-server/workflows/CI/badge.svg)

## Enabling SSL

Not used by Partido on production but useful in other cases.
Our server does not need SSL because it runs on localhost only and is being proxied through a V-Host provided by an Apache server. This V-Host is already SSL encrypted.

https://medium.com/@JavaArchitect/configure-ssl-certificate-with-spring-boot-b707a6055f3
