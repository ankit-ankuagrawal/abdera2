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
package org.apache.abdera2.activities.protocol.managed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.CollectionInfo;
import org.apache.abdera2.common.protocol.WorkspaceInfo;

public class ManagedWorkspace implements WorkspaceInfo {

    private final ManagedProvider provider;

    private String title = "Abdera - Activities";

    public ManagedWorkspace(ManagedProvider provider) {
      this.provider = provider;
    }

    public Collection<CollectionInfo> getCollections(RequestContext request) {
      CollectionAdapterManager cam = provider.getCollectionAdapterManager(request);
      List<CollectionInfo> collections = new ArrayList<CollectionInfo>();
      try {
        Map<String, FeedConfiguration> map = cam.listAdapters();
        for (FeedConfiguration config : map.values())
          collections.add(config);
      } catch (Throwable e) {
        throw ExceptionHelper.propogate(e);
      }
      return collections;
    }
    
    public String getTitle(RequestContext request) {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
}
