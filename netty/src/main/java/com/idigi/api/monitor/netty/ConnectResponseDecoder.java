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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectResponseDecoder extends FrameDecoder {

  private static final int LENGTH = 10;

  private static final Logger logger = LoggerFactory
      .getLogger(ConnectResponseDecoder.class);

  @Override
  protected Object decode(ChannelHandlerContext ctx, Channel ch,
      ChannelBuffer buf) {
    logger.trace("Decoding connect response.");
    if (buf.readableBytes() < LENGTH)
      return null;
    short messageType = buf.readShort();
    if (messageType != MessageType.CONNECT_RESPONSE_ID) {
      throw new RuntimeException("Unexpected message type: " + messageType);
    }
    buf.readInt(); // message size (unused)
    return new ConnectResponse(buf.readShort(), buf.readShort());
  }
}
