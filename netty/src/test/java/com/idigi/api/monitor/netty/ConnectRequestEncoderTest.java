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

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;
import static org.junit.Assert.*;

import com.idigi.api.monitor.netty.ConnectRequest;

public class ConnectRequestEncoderTest {

  public ConnectRequestEncoderTest() {
  }

  @Test
  public void testBuildPayload() {
    ConnectRequest req = new ConnectRequest("pepsi", "cola", 10);
    ChannelBuffer buf = ConnectRequestEncoder.encode(req);

    assertEquals(1, buf.readShort()); // message type
    assertEquals(19, buf.readInt()); // payload size
    assertEquals(1, buf.readShort()); // protocol version
    assertEquals(5, buf.readShort()); // username size
    assertEquals("pepsi", readString(buf, 5)); // username
    assertEquals(4, buf.readShort()); // password size
    assertEquals("cola", readString(buf, 4)); // password
    assertEquals(10, buf.readInt()); // monitor id
  }

  private String readString(ChannelBuffer buf, int length) {
    byte[] bytes = new byte[length];
    buf.readBytes(bytes);
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }
}
