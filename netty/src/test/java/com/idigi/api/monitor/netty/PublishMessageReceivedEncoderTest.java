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
import org.junit.Test;

public class PublishMessageReceivedEncoderTest {

  @Test
  public void testEncode() {
    PublishMessageReceived msg =
        new PublishMessageReceived((short)300, (short)200);
    ChannelBuffer buf = PublishMessageReceivedEncoder.encode(msg);

    assertEquals(4, buf.readShort()); // message type
    assertEquals(4, buf.readInt()); // payload length
    assertEquals(300, buf.readShort()); // data block id
    assertEquals(200, buf.readShort()); // status
  }

}
