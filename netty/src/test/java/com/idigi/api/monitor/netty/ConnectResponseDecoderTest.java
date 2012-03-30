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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Test;

public class ConnectResponseDecoderTest {

  @Test
  public void testDecodeConnectResponse() throws Exception {
    ChannelBuffer buf =
        ChannelBufferResource.getResourceAsChannelBuffer(getClass(),
            "connect-response.bin");
    DecoderEmbedder<ConnectResponse> embedder =
        new DecoderEmbedder<ConnectResponse>(new ConnectResponseDecoder());
    embedder.offer(buf);
    ConnectResponse msg = embedder.poll();

    assertEquals((short) 0x0001, msg.protocolVersion);
    assertEquals((short) 200, msg.statusCode);
  }

}
