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

import org.apache.abdera2.common.protocol.servlet.async.DefaultProcessor;
import org.apache.abdera2.common.protocol.servlet.async.DefaultTaskExecutor;
import org.apache.abdera2.common.protocol.servlet.async.ProcessorQueue;
import org.apache.abdera2.common.protocol.servlet.async.TaskExecutor;
import org.apache.abdera2.common.pusher.ChannelManager;
import org.apache.abdera2.common.pusher.SimpleChannelManager;
import static org.apache.abdera2.common.misc.MoreFunctions.*;

/**
 * The ServiceManager is used by the AbderaServlet to bootstrap the server 
 * instance. There should be little to no reason why an end user would 
 * need to use this class directly.
 */
public abstract class AbstractServiceManager 
  implements ServiceManager {

    protected AbstractServiceManager() {}

    public ProcessorQueue newProcessorQueue(
      Map<String, Object> properties) {    
        return discoverInitializable(
          ProcessorQueue.class,
          DefaultProcessor.class)
            .apply(properties);
    }
    
    public TaskExecutor newTaskExecutor(
      Map<String, Object> properties) {  
        return discoverInitializable(
            TaskExecutor.class,
            DefaultTaskExecutor.class)
              .apply(properties);
    }
    
    public ChannelManager newChannelManager(
      Map<String, Object> properties) {
        return discoverInitializable(
            ChannelManager.class,
            SimpleChannelManager.class)
              .apply(properties);
    }
    
}
