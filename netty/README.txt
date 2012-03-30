iDigi Push Monitor Netty Implementation
=======================================

This project contains an implementation of the iDigi Push Monitor API using the
Netty network application library. There are two service implementations in this
module:

 * `com.idigi.monitor.netty.NettyMonitorService` 
 * `com.idigi.monitor.netty.SecureNettyMonitorService`

NettyMonitorService
-------------------

The `NettyMonitorService` is an insecure implementation of the monitor service
API. All data exchanged by this service (including credentials) is sent
unencrypted over the wire. It is not recommeded to use this service in
production environments.

### Example: Create a netty monitor service

    MonitorService service = 
      new NettyMonitorService('developer.idigi.com', 'username', 'password'); 
    PublishMessageListener listener = new PublishMessageListener {
      {@literal @Override}
      public void publishMessageReceived(PublishMessage message) {
        Scanner scanner = new Scanner(message.getPayload());
        while(scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
        }
      }
    }
    MonitorClientContext client = service.buildClient(1234, listener);
    client.start();

It is also possible to create a service from a `NettyMonitorContext` POJO. This 
is useful if your application uses a dependency injection framework to determine
the service context.

### Example: Create a netty monitor service from a context

    Authentication auth = new Authentication('user', 'pass');
    NettyMonitorContext context = new NettyMonitorContext('developer.idigi.com', auth);
    MonitorService service = new NettyMonitorService(context);
    PublishMessageListener listener = new PublishMessageListener {
      {@literal @Override}
      public void publishMessageReceived(PublishMessage message) {
        Scanner scanner = new Scanner(message.getPayload());
        while(scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
        }
      }
    }
    MonitorClientContext client = service.buildClient(1234, listener);
    client.start();

SecureNettyMonitorService
-------------------------

The `SecureNettyMonitorService` is a secure implementation of the monitor
service API. All data exchanged by this service is send over SSL.

### Example: Create a secure netty monitor service

    MonitorService service = 
      new NettyMonitorService('developer.idigi.com', 'username', 'password'); 
    PublishMessageListener listener = new PublishMessageListener {
      {@literal @Override}
      public void publishMessageReceived(PublishMessage message) {
        Scanner scanner = new Scanner(message.getPayload());
        while(scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
        }
      }
    }
    MonitorClientContext client = service.buildClient(1234, listener);
    client.start();

By default, the `SecureNettyMonitorService` accepts all certificates which
makes it vulnerable to man-in-the-middle attacks. This can be avoided by
providing a custom `SSLContext` object via a `SecureNettyMonitorContext`.

### Example: Using a custom SSL context

    SSLContext sslContext = ...; // some custom SSLContext
    Authentication auth = new Authentication('user', 'pass');
    SecureNettyMonitorContext context = 
      new SecureNettyMonitorContext('developer.idigi.com', auth, sslContext);
    MonitorService service = new SecureNettyMonitorService(context); 
    PublishMessageListener listener = new PublishMessageListener {
      {@literal @Override}
      public void publishMessageReceived(PublishMessage message) {
        Scanner scanner = new Scanner(message.getPayload());
        while(scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
        }
      }
    }
    MonitorClientContext client = service.buildClient(1234, listener);
    client.start();
