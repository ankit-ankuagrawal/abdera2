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

import org.apache.abdera2.common.protocol.RequestContext.Scope;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


/**
 * The DefaultWorkspaceManager is used by the DefaultProvider
 */
public class DefaultWorkspaceManager 
  extends AbstractWorkspaceManager  {
    // URI reserved delimiter characters (gen-delims) from RFC 3986 section 2.2
    private static final String URI_GEN_DELIMS = ":/?#[]@";
    public CollectionAdapter getCollectionAdapter(RequestContext request) {
      String path = request.getContextPath() + request.getTargetPath();
      CollectionAdapter ca = 
        (CollectionAdapter)request.getAttribute(
          Scope.REQUEST, 
          AbstractWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE);
      if (ca != null)
          return ca;
      for (WorkspaceInfo wi : workspaces) {
        CollectionInfo ci = 
          Iterables.<CollectionInfo>find(
            wi.getCollections(request), 
            CI(request,path),null);
        if (ci != null)
          return (CollectionAdapter)ci;
      }
      return null;
    }
    
    private static final Predicate<CollectionInfo> CI(
      final RequestContext rc, 
      final String path) { 
      return new Predicate<CollectionInfo>() {
        public boolean apply(CollectionInfo input) {
          String href = input.getHref(rc);
          return (
            path.equals(href) || 
            (href != null && 
             path.startsWith(href) && 
             URI_GEN_DELIMS.contains(
              path.substring(href.length(), href.length() + 1))));
        }
      }; 
    }
}
