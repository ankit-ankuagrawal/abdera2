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
package org.apache.abdera2.common.templates;

import org.apache.abdera2.common.templates.Context;

/**
 * Abstract base for custom Context implementations
 */
public abstract class AbstractContext implements Context {

  private static final long serialVersionUID = -2511167897930514261L;
  private final boolean iri;
  protected AbstractContext(boolean iri) {
    this.iri = iri;
  }
  public boolean isIri() {
      return iri;
  }
}
