iDigi Monitor API Library for Java
==================================

This project contains clients for the iDigi monitor API. The iDigi monitor API
allows clients to receive asynchronous notification of various events in iDigi.
More information about the iDigi monitor service may be found on the [iDigi
Website](http://www.idigi.com). Log in to your account and check out the iDigi Web Services
Programming Guide.

This source has been contributed by [Digi International](http://www.digi.com).

Requirements
------------

* [Java SDK](http://www.java.com)
* [Gradle Build Tool](http://www.gradle.com)
* [iDigi Account](http://www.idigi.com)
* An iDigi device - this includes any of Digi's gateway products or another
  iDigi connector like [ConnectPort for PC](http://www.github.com/digidotcom/cp4pc).

Usage
-----

Command Line (from project directory):

    $ gradle jar
    
    $ java -jar netty/build/libs/netty.jar <host> <username> <password> <monitorId>

NOTE: This assumes that a monitor has been created using the iDigi Monitor API
(see below).

Example
-------

This example shows how to integrate the iDigi Monitor API Library into a Java
app.

First, create a monitor using the Monitor web service.

    $ cat > request.xml
    <Monitor>
      <monTopic>DeviceCore</monTopic>
      <monTransportType>tcp</monTransportType>
      <monFormatType>json</monFormatType>
      <monBatchSize>1</monBatchSize>
      <monCompression>gzip</monCompression>
      <monBatchDuration>0</monBatchDuration>
    </Monitor>
    $ curl -XPOST -d @request.xml https://user:pass@developer.idigi.com/ws/Monitor
    <?xml version="1.0" encoding="ISO-8859-1"?>
    <result>
       <location>Monitor/1234</location>
    </result>

Next, use the client to process messages received from that monitor.

    int monitorId = 1234;
    MonitorClientFactory factory = ...; // see implementations
    PublishMessageListener listener = new PublishMessageListener {
      @Override
      public void messageReceived(Message message) {
        Scanner scanner = new Scanner(message.getPayload());
        while(scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
        }
      }
    }
    factory.buildClient(monitorId, listener).start();


Implementations
---------------

### Netty

The Netty implementation of the iDigi Monitor API provides two
`MonitorClientFactory` implementations:

* `NettyMonitorClientFactory`
* `SecureNettyMonitorClientFactory`

#### NettyMonitorClientFactory

`NettyMonitorClientFactory` sends data (including authentication) over the wire
without encryption. Therefore, this implementation should not be used for
production environments. Production environments should use the
`SecureNettyMonitorClientFactory`.

    NettyMonitorClientFactory factory =
        new NettyMonitorClientFactory('developer.idigi.com', 'user', 'pass');

#### SecureNettyMonitorClientFactory

`SecureNettyMonitorClientFactory` sends and receives data over an SSL TCP
socket.

    SecureNettyMonitorClientFactory factory =
        new SecureNettyMonitorClientFactory('developer.idigi.com', 'user', 'pass');

The constructor above uses a permissive SSL context, so any valid certificate
is accepted from the server.  This is still vulnerable to man-in-the-middle
attacks.  For added security, a program may specify it's own SSL context for
validating certificates.

    SSLContext context = ...; // some custom SSLContext implementation
    SecureNettyMonitorClientFactory factory =
        new SecureNettyMonitorClientFactory('developer.idigi.com', 'user', 'pass', sslContext);


License
-------

This software is open-source software. Copyright Digi International, 2012.

This Source Code is subject to the terms of the Apache License v. 2.0. If a
copy of the Apache License v2.0 was not distributed with this file, you can
obtain one at http://www.apache.org/licenses.

