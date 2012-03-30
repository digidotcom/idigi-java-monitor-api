iDigi Push Monitor API
======================

This project contains clients for the iDigi monitor service.  The iDigi monitor
service allows clients to receive asynchronous notification of various events
in iDigi.  More information about the iDigi monitor service may be found in the
[iDigi Web Services Programming
Guide](http://ftp1.digi.com/support/documentation/90002008_D.pdf).

Example
-------

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
    $ curl -v -XPOST -d @request.xml https://user:pass@developer.idigi.com/ws/Monitor
    <?xml version="1.0" encoding="ISO-8859-1"?>
    <result>
       <location>Monitor/1234</location>
    </result>

Next, use the iDigi Monitor client to process messages received from that monitor.

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

The Netty implementation of the iDigi Push Monitor API provides two
MonitorClientFactory implementations:

* NettyMonitorClientFactory
* SecureNettyMonitorClientFactory

#### NettyMonitorClientFactory

NettyMonitorClientFactory sends data (including authentication) over the wire
without encryption. Therefore, this implementation should not be used for
production environments. Production environments should use the
`SecureNettyMonitorClientFactory`.

    NettyMonitorClientFactory factory =
        new NettyMonitorClientFactory('developer.idigi.com', 'user', 'pass');

#### SecureNettyMonitorClientFactory

SecureNettyMonitorClientFactory sends and receives data over an SSL TCP
socket.

    SecureNettyMonitorClientFactory factory =
        new SecureNettyMonitorClientFactory(
            'developer.idigi.com', 'user', 'pass');

The constructor above uses a permissive SSL context, so any valid certificate
is accepted from the server.  This is still vulnerable to man-in-the-middle
attacks.  For added security, a program may specify it's own SSL context for
validating certificated.

    SSLContext context = ...; // some custom SSLContext implementation
    SecureNettyMonitorClientFactory factory =
        new SecureNettyMonitorClientFactory(
            'developer.idigi.com', 'user', 'pass', sslContext);

