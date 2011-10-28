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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.http.EntityTag;
import org.joda.time.DateTime;

import com.google.common.base.Function;

/**
 * An abstract base Provider implementation that implements the 
 * WorkspaceManager interface. This is intended to be used by 
 * Provider's that do not wish to use a separate WorkspaceManager object.
 */
public abstract class AbstractWorkspaceProvider
  extends AbstractProvider 
  implements WorkspaceManager {

    protected Function<RequestContext,Target> targetResolver;
    protected TargetBuilder<?> targetBuilder;
    protected final Set<WorkspaceInfo> workspaces = 
      new LinkedHashSet<WorkspaceInfo>();

    protected WorkspaceManager getWorkspaceManager() {
      return this;
    }

    protected Function<RequestContext,Target> getTargetResolver(
      RequestContext request) {
      return targetResolver;
    }

    @SuppressWarnings("rawtypes")
    protected TargetBuilder getTargetBuilder(Request request) {
        return (TargetBuilder)targetBuilder;
    }

    protected void setTargetBuilder(TargetBuilder<?> targetBuilder) {
        this.targetBuilder = targetBuilder;
    }

    protected void setTargetResolver(Function<RequestContext,Target> targetResolver) {
        this.targetResolver = targetResolver;
    }

    public Collection<WorkspaceInfo> getWorkspaces(RequestContext request) {
        return workspaces;
    }

    public synchronized void setWorkspaces(Collection<WorkspaceInfo> workspaces) {
      workspaces.clear();
      workspaces.addAll(workspaces);
    }
    
    public synchronized void addWorkspace(WorkspaceInfo workspace) {
        workspaces.add(workspace);
    }

    // JIRA: https://issues.apache.org/jira/browse/ABDERA-255
    public EntityTag getEntityTag() {
      return null;
    }
    
    // JIRA: https://issues.apache.org/jira/browse/ABDERA-255
    public DateTime getLastModified() {
      return DateTimes.utcNow();
    }
}
