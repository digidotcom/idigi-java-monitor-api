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
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idigi.api.monitor.MonitorClientContext;
import com.idigi.api.monitor.netty.ReconnectHandler.ChannelChangeListener;

class NettyMonitorClientContext implements MonitorClientContext,
  ChannelChangeListener {

  private final Logger logger = LoggerFactory
      .getLogger(NettyMonitorClientContext.class);

  protected final ClientBootstrap bootstrap;

  protected Channel channel;

  private InetSocketAddress address;

  private boolean stopped = false;

  public NettyMonitorClientContext(ClientBootstrap bootstrap,
    InetSocketAddress address) {
    this.address = address;
    this.bootstrap = bootstrap;

    bootstrap.setOption("channelChangeListener", this);
  }

  @Override
  public synchronized void start() {
    logger.debug("Starting monitor client.");
    if (this.channel == null) {
      ChannelFuture f = this.bootstrap.connect(this.address);
      this.channel = f.getChannel();
    } else {
      throw new IllegalStateException("The monitor has already been started.");
    }
  }

  @Override
  public synchronized void stop() {
    logger.debug("Stopping monitor client.");
    if (this.channel != null) {
      this.stopped = true;
      closeChannel(this.channel);
    } else {
      throw new IllegalStateException("The monitor is not running.");
    }
  }

  protected void releaseResources() {
    // Release external resources from Netty. Since one of these external
    // resources includes the thread pool in which this method is running,
    // it is necessary to shutdown the resources in a new thread.
    new Thread(new Runnable() {
      @Override
      public void run() {
        logger.debug("Releasing external resources.");
        bootstrap.releaseExternalResources();
      }
    }).start();
  }

  @Override
  public synchronized void channelChanged(Channel channel) {
    if (this.stopped) {
      closeChannel(channel);
    } else {
      this.channel = channel;
    }
  }

  private void closeChannel(Channel channel2) {
    channel.getCloseFuture().addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        releaseResources();
      }
    });
    channel.close();
  }

}
