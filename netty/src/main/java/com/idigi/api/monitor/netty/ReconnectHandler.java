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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectHandler extends SimpleChannelUpstreamHandler {

  final ClientBootstrap bootstrap;

  private static final Logger logger = LoggerFactory
      .getLogger(ReconnectHandler.class);

  public ReconnectHandler(ClientBootstrap bootstrap) {
    this.bootstrap = bootstrap;
  }

  @Override
  public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
    // Reconnect unless the channel was closed due to a request from
    // and upstream handler.
    logger.info("Reconnecting to: {}", getRemoteAddress());
    bootstrap.connect();
  }

  protected InetSocketAddress getRemoteAddress() {
    return (InetSocketAddress) bootstrap.getOption("remoteAddress");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    Throwable cause = e.getCause();
    if (cause instanceof ConnectException) {
      logger.error("Failed to connect: {}", cause.getMessage());
    }
    if (cause instanceof IOException) {
      // An unknown IOException occurred. Most likely, iDigi reset the
      // connection.
      logger.warn("Disconnecting due to IOException: {}", cause.getMessage());
    } else {
      logger.error("Another error occurred: {}", cause.getMessage());
      cause.printStackTrace();
    }
    ctx.getChannel().close();
  }
}
