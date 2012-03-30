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

import java.io.InputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idigi.api.monitor.Message;

class MessageDataBlockDecoder extends
  ReplayingDecoder<MessageDataBlockDecoder.State> {

  private static final Logger logger = LoggerFactory
      .getLogger(MessageDataBlockDecoder.class);

  private MessageDataBlockBuilder builder;

  private int payloadSize;

  private boolean isCompressed;

  public enum State {
    MESSAGE_TYPE, MESSAGE_SIZE, DATA_BLOCK_ID, COUNT, COMPRESSION, FORMAT, PAYLOAD_SIZE, PAYLOAD;
  }

  public MessageDataBlockDecoder() {
    this.reset();
  }

  private void reset() {
    this.setState(State.MESSAGE_TYPE);
    this.builder = new MessageDataBlockBuilder();
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, Channel ch,
      ChannelBuffer buf, State state) throws Exception {
    logger.debug("Decoding PublishMesssage");
    switch (state) {
    case MESSAGE_TYPE:
      short messageType = buf.readShort();
      if (messageType != MessageType.PUBLISH_MESSAGE_ID) {
        throw new RuntimeException("Unexcepted messageType:" + messageType);
      }
      checkpoint(State.MESSAGE_TYPE);
    case MESSAGE_SIZE:
      buf.readInt(); // (unused)
      checkpoint(State.MESSAGE_SIZE);
    case DATA_BLOCK_ID:
      this.builder.setDataBlockId(buf.readShort());
      checkpoint(State.COUNT);
    case COUNT:
      buf.readShort(); // Count unused
      checkpoint(State.COMPRESSION);
    case COMPRESSION:
      this.isCompressed = buf.readByte() == 1;
      checkpoint(State.FORMAT);
    case FORMAT:
      this.builder.setFormat(formatFromByte(buf.readByte()));
      checkpoint(State.PAYLOAD_SIZE);
    case PAYLOAD_SIZE:
      this.payloadSize = buf.readInt();
      checkpoint(State.PAYLOAD);
    case PAYLOAD:
      ChannelBuffer payload = buf.readBytes(payloadSize);
      if (isCompressed) {
        payload = decompress(payload);
      }
      InputStream is = new ChannelBufferInputStream(payload);
      this.builder.setPayload(is);
      try {
        return this.builder.build();
      } finally {
        // This decoder is stateful and is reused to decode other messages. The
        // decoder must be reset before it can be used again.
        this.reset();
      }
    default:
      throw new Exception("Unknown state: " + state);
    }
  }

  private ChannelBuffer decompress(ChannelBuffer slice) {
    logger.debug("Decompressing GZIP data.");

    ZlibDecoder zlibDecoder = new ZlibDecoder(ZlibWrapper.ZLIB_OR_NONE);
    DecoderEmbedder<ChannelBuffer> decoder =
        new DecoderEmbedder<ChannelBuffer>(zlibDecoder);
    decoder.offer(slice);
    return decoder.poll();
  }

  private Message.Format formatFromByte(byte value) throws Exception {
    switch (value) {
    case 0x00:
      return Message.Format.XML;
    case 0x01:
      return Message.Format.JSON;
    default:
      throw new IllegalArgumentException("Unknown message format: " + value);
    }
  }
}
