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

public class ConnectRequestEncoder extends OneToOneEncoder {

  private static final Logger logger = LoggerFactory
      .getLogger(ConnectRequestEncoder.class);

  private static final short VERSION = 1;

  private static final int BASE_PAYLOAD_LENGTH = 10;

  private static final int HEADER_LENGTH = 6;

  @Override
  protected Object encode(ChannelHandlerContext ctx, Channel ch, Object msg)
      throws Exception {
    if (msg instanceof ConnectRequest) {
      return encode((ConnectRequest) msg);
    }
    return msg;
  }

  public static ChannelBuffer encode(ConnectRequest msg) {
    logger.trace("Encoding: {}", msg);
    byte[] user = msg.username.getBytes();
    byte[] pass = msg.password.getBytes();
    int payloadLength = BASE_PAYLOAD_LENGTH + user.length + pass.length;

    ChannelBuffer buf =
        ChannelBuffers.directBuffer(payloadLength + HEADER_LENGTH);
    buf.writeShort(MessageType.CONNECT_REQUEST_ID);
    buf.writeInt(payloadLength);
    buf.writeShort(VERSION);
    buf.writeShort((short) user.length);
    buf.writeBytes(user);
    buf.writeShort((short) pass.length);
    buf.writeBytes(pass);
    buf.writeInt(msg.monitorId);
    return buf;
  }
  
}
