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

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.idigi.api.monitor.MessageListener;

class MonitorPipelineFactory implements ChannelPipelineFactory {

  private final ConnectHandler connectHandler;

  private final ChannelHandler reconnectHandler;

  private final ChannelHandler listenerHandler;

  public MonitorPipelineFactory(Authentication auth, int monitorId,
    MessageListener listener, ClientBootstrap bootstrap) {
    this.connectHandler = new ConnectHandler(auth, monitorId);
    this.reconnectHandler = new ReconnectHandler(bootstrap);
    this.listenerHandler = new ListenerHandler(listener);
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    // TODO Refactor stateless handlers into singletons.
    // Jordan Focht <2011-04-30>
    ChannelPipeline pipeline = Channels.pipeline();
    pipeline.addLast("hexLogger", Logging.getLoggingHandler()); 
    pipeline.addLast("decoder", new ConnectResponseDecoder());
    pipeline.addLast("authHandler", connectHandler);
    pipeline.addLast("connectRequestEncoder", new ConnectRequestEncoder());
    pipeline.addLast("publishMessageResponseEncoder",
        new PublishMessageReceivedEncoder());
    pipeline.addLast("listenerHandler", listenerHandler);
    pipeline.addLast("responder", new PublishMessageResponder());
    pipeline.addLast("reconnectHandler", reconnectHandler);
    return pipeline;
  }

}
