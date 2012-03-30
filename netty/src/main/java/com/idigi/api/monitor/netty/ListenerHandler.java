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
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idigi.api.monitor.MessageListener;

/**
 * ListenerHandler notifies a PublishMessageListener when a message is received
 * from the iDigi server.
 */
class ListenerHandler extends SimpleChannelHandler {

  private final MessageListener listener;

  private static final Logger logger = LoggerFactory
      .getLogger(ListenerHandler.class);

  public ListenerHandler(MessageListener listener) {
    this.listener = listener;
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    Object msg = e.getMessage();
    if (msg instanceof MessageDataBlock) {
      MessageDataBlock dataBlock = (MessageDataBlock) msg;
      logger.trace("Processing {} with {}.", dataBlock.getMessage(),
          this.listener);
      this.listener.messageReceived(dataBlock.getMessage());
    }
    ctx.sendUpstream(e);
  }
}
