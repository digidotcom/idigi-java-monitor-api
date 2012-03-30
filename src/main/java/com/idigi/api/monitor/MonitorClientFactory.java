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
 * MonitorClientFactory builds iDigi monitor clients.
 *
 * @see MonitorClientContext 
 * @see MessageListener
 */
public interface MonitorClientFactory {

  /**
   * Builds a client to receive push messages from iDigi.
   *
   * @param  monitorId  the id of an iDigi monitor
   * @param  listener   a listener for messages received from the iDigi monitor
   * @return            a context for starting and stopping the client
   */
  public MonitorClientContext buildClient(int monitorId, MessageListener listener);

}

