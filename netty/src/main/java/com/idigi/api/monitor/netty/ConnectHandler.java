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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConnectHandler sends a {@link ConnectRequest} to iDigi when a connection is
 * established.
 */
class ConnectHandler extends SimpleChannelHandler {

  private final ConnectRequest request;

  private static final Logger logger = LoggerFactory
      .getLogger(ConnectHandler.class);

  public ConnectHandler(Authentication auth, int monitorId) {
    this.request = new ConnectRequest(auth.username, auth.password, monitorId);
  }

  @Override
  public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    logger.debug("Connecting to iDigi monitor #{}.", this.request.monitorId);
    e.getChannel().write(this.request);

  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
      throws Exception {
    if (event.getMessage() instanceof ConnectResponse) {
      logger.info("Connected to iDigi monitor #{}.", request.monitorId);

      // Set up the pipeline to receive publish messages
      ctx.getPipeline().replace("decoder", "decoder",
          new MessageDataBlockDecoder());
    }
    ctx.sendUpstream(event);
  }

}
