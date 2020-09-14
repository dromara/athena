/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.athena.core.transformer.listener;

import org.dromara.athena.core.reporter.MetricsReporter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;


/**
 * The type Abstract listener.
 *
 * @author xiaoyu
 */
public abstract class AbstractListener implements Listener {
    
    public static final String METRICS_REPORTER_CLASSNAME = Type.getInternalName(MetricsReporter.class);
    
    /**
     * The Advice adapter.
     */
    protected final AdviceAdapter aa;
    
    /**
     * The Arg types.
     */
    protected final Type[] argTypes;
    
    /**
     * The Access.
     */
    protected final int access;
    
    /**
     * Instantiates a new Abstract listener.
     *
     * @param aa        the AdviceAdapter
     * @param argTypes  the arg types
     * @param access    the access
     */
    public AbstractListener(final AdviceAdapter aa, final Type[] argTypes, final int access) {
        this.aa = aa;
        this.argTypes = argTypes;
        this.access = access;
    }
}
