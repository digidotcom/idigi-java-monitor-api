/*
 * Copyright 2012 Digi International, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idigi.api.monitor.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;

import com.idigi.api.monitor.MessageListener;
import com.idigi.api.monitor.MonitorClientContext;
import com.idigi.api.monitor.MonitorClientFactory;

/**
 * NettyPushService is an iDigi monitor service built on the Netty NIO TCP
 * client.
 */
public class NettyMonitorClientFactory implements MonitorClientFactory {

  private final InetSocketAddress address;
  private final Authentication authentication;

  /**
   * Constructs a new instance. NettyMonitorClientFactory sends data
   * (including authentication) over the wire without encryption. Therefore,
   * this service should not be used for production environments. Production
   * environments should use the <code>SecureNettyMonitorService</code>.
   * 
   * The host name can either be a machine name, such as "java.sun.com", or a
   * textual representation of its IP address. The monitor service will build
   * clients that try to connect to the push service at this host on the default
   * insecure monitor port (defined in <code>Constants</code>).
   * 
   * @param hostname the iDigi host
   * @param username the iDigi user
   * @param password the iDigi password
   * 
   * @see java.net.InetAddress#getByName(String)
   * @see SecureNettyMonitorService
   */
  public NettyMonitorClientFactory(String hostname, String username,
    String password) {
    this.address =
        new InetSocketAddress(hostname, Constants.INSECURE_MONITOR_PORT);
    this.authentication = new Authentication(username, password);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MonitorClientContext buildClient(int monitorId,
      MessageListener listener) {
    ClientBootstrap bootstrap = MonitorClientBootstrap.newInstance(address);
    bootstrap.setPipelineFactory(new MonitorPipelineFactory(authentication,
        monitorId, listener, bootstrap));
    return new NettyMonitorClientContext(bootstrap, address);
  }
}
