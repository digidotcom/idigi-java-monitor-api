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

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Test;

import com.idigi.api.monitor.Message.Format;

public class PublishMessageDecoderTest {

  @Test
  public void testDecodePublishMessage() throws Exception {
    ChannelBuffer buf =
        ChannelBufferResource.getResourceAsChannelBuffer(getClass(),
            "publish-message.bin");
    DecoderEmbedder<MessageDataBlock> embedder =
        new DecoderEmbedder<MessageDataBlock>(new MessageDataBlockDecoder());
    embedder.offer(buf);
    MessageDataBlock dataBlock = embedder.poll();

    assertEquals((short) 0x000, dataBlock.getId());
    assertEquals(Format.JSON, dataBlock.getMessage().getFormat());

    InputStream in = dataBlock.getMessage().getPayload();
    int size = 0;
    int payloadSize = 0;
    byte[] buffer = new byte[1024];
    while ((size = in.read(buffer)) != -1)
      payloadSize += size;

    assertEquals(508, payloadSize);
  }
}
