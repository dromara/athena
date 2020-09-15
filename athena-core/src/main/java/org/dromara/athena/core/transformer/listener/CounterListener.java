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

import org.dromara.athena.core.config.Metric;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * The type Counter listener.
 *
 * @author xiaoyu
 */
public class CounterListener extends AbstractListener {
    
    private static final String METHOD = "counterInc";
    
    private static final String SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(String[].class));
    
    private final Metric metric;
    
    /**
     * Instantiates a new Counter listener.
     *
     * @param metric    the metric
     * @param aa        the aa
     * @param argTypes  the arg types
     * @param access    the access
     */
    public CounterListener(final Metric metric, final AdviceAdapter aa, final Type[] argTypes, final int access) {
        super(aa, argTypes, access);
        this.metric = metric;
    }
    
    @Override
    public void listenerOnMethodEnter() {
        injectLabel(metric);
        aa.visitMethodInsn(INVOKESTATIC, METRICS_REPORTER_CLASSNAME, METHOD, SIGNATURE, false);
    }
    
}
