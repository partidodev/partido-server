# Partido Server
[![Build Status](https://ci.fosforito.net/api/badges/Fosforito/partido-server/status.svg)](https://ci.fosforito.net/Fosforito/partido-server)

## Enabling SSL

Not used by Partido on production but useful in other cases.
Our server does not need SSL because it runs on localhost only and is being proxied through a V-Host provided by an Apache server. This V-Host is already SSL encrypted.

https://medium.com/@JavaArchitect/configure-ssl-certificate-with-spring-boot-b707a6055f3