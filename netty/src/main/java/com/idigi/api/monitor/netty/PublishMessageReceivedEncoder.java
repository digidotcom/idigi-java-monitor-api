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
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishMessageReceivedEncoder extends OneToOneEncoder {
  
  private static final Logger logger = LoggerFactory
      .getLogger(ConnectResponseDecoder.class);

  private static final short PAYLOAD_LENGTH = 4;

  private static final short HEADER_LENGTH = 6;

  @Override
  protected Object encode(ChannelHandlerContext ctx, Channel ch, Object msg)
      throws Exception {
    if (msg instanceof PublishMessageReceived) {
      return encode((PublishMessageReceived) msg);
    }
    return msg;
  }

  public static ChannelBuffer encode(PublishMessageReceived msg) {
    logger.trace("Encoding: {}", msg);
    ChannelBuffer buf =
        ChannelBuffers.directBuffer(PAYLOAD_LENGTH + HEADER_LENGTH);
    buf.writeShort(MessageType.PUBLISH_MESSAGE_RECEIVED_ID);
    buf.writeInt(PAYLOAD_LENGTH);
    buf.writeShort(msg.dataBlockId);
    buf.writeShort(msg.status);
    return buf;
  }
  
}
