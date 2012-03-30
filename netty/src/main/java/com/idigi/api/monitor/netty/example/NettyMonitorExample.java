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
package com.idigi.api.monitor.netty.example;

import java.io.IOException;
import java.util.Scanner;

import com.idigi.api.monitor.MonitorClientContext;
import com.idigi.api.monitor.MonitorClientFactory;
import com.idigi.api.monitor.Message;
import com.idigi.api.monitor.MessageListener;
import com.idigi.api.monitor.netty.SecureNettyMonitorClientFactory;

public class NettyMonitorExample {

  /**
   * Creates an iDigi monitor client and prints message received to stdout.
   */
  public static void main(String[] args) throws IOException {

    // Print usage if the wrong number of arguments are supplied.
    if (args.length != 4) {
      System.out.println("usage: " + NettyMonitorExample.class.getName()
          + " <host> <username> <password> <monitorId>");
      return;
    }

    // Parse args.
    String host = args[0];
    String username = args[1];
    String password = args[2];
    int monitorId = Integer.parseInt(args[3]);

    // Create a new MonitorClientFactory.
    MonitorClientFactory service =
        new SecureNettyMonitorClientFactory(host, username, password);

    // Create a message listener to receive messages from the monitor client.
    MessageListener listener = new MessageListener() {
      @Override
      public void messageReceived(Message message) {
        // Every time a message is received, print the message payload to
        // stdout.
        Scanner isScanner = new Scanner(message.getPayload());
        while (isScanner.hasNextLine()) {
          System.out.println(isScanner.nextLine());
        }
      }
    };

    // Create a new client context to manage the monitor client lifecycle.
    MonitorClientContext context = service.buildClient(monitorId, listener);

    // Start listening for messages from the monitor.
    context.start();
    
    // Wait for the user to quit this example.
    System.out.println("Press [enter] to quit.");
    System.in.read();

    // Stop listening for messages from the monitor.
    context.stop();
  }
}
