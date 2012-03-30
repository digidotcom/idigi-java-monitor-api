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
package com.idigi.api.monitor;

/**
 * Receives push messages from an iDigi monitor.
 *
 * @see MonitorClientFactory
 */
public interface MessageListener {

  /**
   * Receives messages from an iDigi monitor.
   *
   * @param message a push message received from iDigi.
   *
   * @see Message
   */
  public void messageReceived(Message message);

}

