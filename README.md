# Socket Broker

[![build](https://github.com/exceptionfactory/socketbroker/actions/workflows/build.yml/badge.svg)](https://github.com/exceptionfactory/socketbroker/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/exceptionfactory/socketbroker/branch/main/graph/badge.svg?token=XM99GMCJGT)](https://codecov.io/gh/exceptionfactory/socketbroker)
[![vulnerabilities](https://snyk.io/test/github/exceptionfactory/socketbroker/badge.svg)](https://snyk.io/test/github/exceptionfactory/socketbroker)
[![javadoc](https://javadoc.io/badge2/com.exceptionfactory.socketbroker/socketbroker/javadoc.svg)](https://javadoc.io/doc/com.exceptionfactory.socketbroker/socketbroker)
[![maven-central](https://img.shields.io/maven-central/v/com.exceptionfactory.socketbroker/socketbroker)](https://search.maven.org/artifact/com.exceptionfactory.socketbroker/socketbroker)

Java Socket library supporting SOCKS and HTTP proxy servers with authentication 

# Build Requirements

- Java 17
- Maven 3.9

# Runtime Requirements

- Java 8

# Versioning

Socket Broker follows the [Semantic Versioning Specification 2.0.0](https://semver.org/).

The public API consists of interfaces and classes in the following Java packages:

- com.exceptionfactory.socketbroker
- com.exceptionfactory.socketbroker.configuration

# Standards

Socket Broker supports client capabilities for several protocols defined in referenced standards.

## SOCKS

- [RFC 1928: SOCKS Protocol Version 5](https://tools.ietf.org/html/rfc1928)
- [RFC 1929: Username/Password Authentication for SOCKS V5](https://tools.ietf.org/html/rfc1929)

## HTTP

- [RFC 7231: Hypertext Transfer Protocol (HTTP/1.1): Semantics and Content](https://tools.ietf.org/html/rfc7231)
- [RFC 7235: Hypertext Transfer Protocol (HTTP/1.1): Authentication](https://tools.ietf.org/html/rfc7235)
- [RFC 7617: The 'Basic' HTTP Authentication Scheme](https://tools.ietf.org/html/rfc7617)

# Building

Run the following Maven command to build the library:

```
./mvnw clean install
```

# Integrating

The `BrokeredSocketFactory` class provides the primary point of integration for external applications.

## Configuration

The `BrokerConfiguration` interface and `StandardBrokerConfiguration` class provide the proxy protocol, proxy server
address, and optional authentication credentials necessary for creating a connection through a proxy server.

### Proxy Type

The `ProxyType` enumeration defines supported protocols including `SOCKS5` and `HTTP_CONNECT`.

### Proxy Socket Address

The proxy server address must be defined using an instance
of [java.net.InetSocketAddress](https://docs.oracle.com/javase/8/docs/api/java/net/InetSocketAddress.html) that includes
the server address and port number.

### Authentication Credentials

Access to proxy servers that require authentication involves configuring an instance of `AuthenticationCredentials`.
The `StandardUsernamePasswordAuthenticationCredentials` class supports defining a username string and a password
character array.

### Socket Factory

The `BrokeredSocketFactory` class requires an instance of `BrokerConfiguration` as well as
a [javax.net.SocketFactory](https://docs.oracle.com/javase/8/docs/api/javax/net/SocketFactory.html). The `SocketFactory`
provides the opportunity to supply standard settings for the connection to the configured proxy server. The
`SocketFactory.getDefault()` method is sufficient for standard integrations.

# Alternatives

Several alternative implementations are available depending on usage requirements.

## Java Socket Proxy

The [java.net.Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html) class can be configured using an
instance of [java.net.Proxy](https://docs.oracle.com/javase/8/docs/api/java/net/Proxy.html) for connections to SOCKS or
HTTP proxies that do not require authentication.

The standard [java.net.Authenticator](https://docs.oracle.com/javase/8/docs/api/java/net/Authenticator.html) class
supports configurable proxy authentication through the static `setDefault()` method. Standard `Socket` implementations
invoke password request methods on a configured `Authenticator` to return instances of
[java.net.PasswordAuthentication](https://docs.oracle.com/javase/8/docs/api/java/net/PasswordAuthentication.html). This
integration approach is sufficient for applications that allow defining an instance of the `Authenticator` class for the
Java Virtual Machine.

## Netty

The [Netty](https://netty.io) project provides component modules that support both SOCKS 4 and SOCKS 5 as well as HTTP.
Netty does not provide a direct implementation of `java.net.Socket` supporting proxy connections, but applications using
Netty can integrate support for proxy access using available components.

## SocksLib

The [SocksLib](https://github.com/fengyouchao/sockslib) library provides client and server socket implementations for
the SOCKS 5 protocol. SocksLib supports both TCP and UDP client connections.

# Licensing

Socket Broker is released under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
