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

import com.idigi.api.monitor.Message;

class MessageDataBlockBuilder {

  private Short dataBlockId = -1;

  private Message.Format format;

  private InputStream payload;

  private boolean finalized = false;

  private final Message message = new Message() {
    @Override
    public InputStream getPayload() {
      return payload;
    }

    @Override
    public Format getFormat() {
      return format;
    }
  };

  private final MessageDataBlock dataBlock = new MessageDataBlock() {

    @Override
    public short getId() {
      return dataBlockId;
    }

    @Override
    public Message getMessage() {
      return message;
    }
  };

  public MessageDataBlock build() {
    this.validate();
    this.finalized = true;
    return dataBlock;
  }

  private void validate() {

    if (this.finalized)
      throw new IllegalStateException("PublishMessage has already been built");

    if (this.dataBlockId == -1)
      throw new IllegalStateException("Data block id must be set");

    if (this.format == null)
      throw new IllegalStateException("Format cannot be null");

    if (this.payload == null)
      throw new IllegalStateException("Payload cannot be null");

  }

  public void setDataBlockId(Short dataBlockId) {
    this.dataBlockId = dataBlockId;
  }

  public void setFormat(Message.Format format) {
    this.format = format;
  }

  public void setPayload(InputStream payload) {
    this.payload = payload;
  }
}
