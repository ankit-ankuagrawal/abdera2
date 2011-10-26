/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.protocol;

import java.util.Map;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.DefaultImplementation;
import org.apache.abdera2.common.protocol.servlet.async.ProcessorQueue;
import org.apache.abdera2.common.protocol.servlet.async.TaskExecutor;
import org.apache.abdera2.common.pusher.ChannelManager;

@DefaultImplementation("org.apache.abdera2.protocol.server.AtompubServiceManager")
public interface ServiceManager {
  
  public abstract <P extends Provider>P newProvider(
    Map<String, Object> properties);

  public abstract ProcessorQueue newProcessorQueue(
    Map<String, Object> properties);

  public abstract TaskExecutor newTaskExecutor(
    Map<String, Object> properties);

  public abstract ChannelManager newChannelManager(
      Map<String, Object> properties);

  public static class Factory {
    
    public static ServiceManager getInstance() {
      return getInstance(
        "org.apache.abdera2.protocol.server.AtompubServiceManager");
    }
  
    public static ServiceManager getInstance(String impl) {
      return Discover.locate(ServiceManager.class, impl);
    }
    
  }
}