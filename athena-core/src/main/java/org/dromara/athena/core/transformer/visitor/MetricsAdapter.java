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

package org.dromara.athena.core.transformer.visitor;

import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.dromara.athena.core.config.Metric;
import org.dromara.athena.core.reporter.MetricsReporter;
import org.dromara.athena.core.transformer.listener.Listener;
import org.dromara.athena.core.transformer.listener.ListenerFactory;
import org.dromara.athena.core.utils.MetricsLabelUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * The type Metrics adapter.
 *
 * @author xiaoyu
 */
public class MetricsAdapter extends AdviceAdapter {
    
    private final List<Metric> metrics;
    
    private final Type[] argTypes;
    
    private final String className;
    
    private final String methodName;
    
    private final int access;
    
    private List<Listener> listeners;
    
    /**
     * Instantiates a new Metrics adapter.
     *
     * @param mv        the mv
     * @param className the class name
     * @param access    the access
     * @param name      the name
     * @param desc      the desc
     * @param metrics   the metric list
     */
    public MetricsAdapter(final MethodVisitor mv, final String className, final int access, final String name, final String desc, final List<Metric> metrics) {
        super(ASM5, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
        this.argTypes = Type.getArgumentTypes(desc);
        this.access = access;
        this.metrics = metrics;
    }
    
    @Override
    protected void onMethodEnter() {
        if (CollectionUtils.isEmpty(metrics)) {
            listeners = Collections.emptyList();
            return;
        }
        if (!checkLabels()) {
            throw new IllegalArgumentException("you class name :" + className  + " methodName :" + methodName + ", labels config error");
        }
        listeners = ListenerFactory.newListeners(metrics, this, argTypes, access);
        MetricsReporter.registerMetrics(metrics);
        listeners.forEach(Listener::listenerOnMethodEnter);
    }
    
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        listeners.forEach(listener -> listener.listenerOnVisitMaxs(maxStack, maxLocals));
        mv.visitMaxs(maxStack, maxLocals);
    }
    
    @Override
    protected void onMethodExit(int opcode) {
        listeners.forEach(listener -> listener.listenerOnMethodExit(opcode));
    }
    
    private boolean checkLabels() {
        return metrics.stream().filter(metric -> CollectionUtils.isNotEmpty(metric.getLabels()))
                .allMatch(metric -> MetricsLabelUtils.checkLabels(argTypes, metric.getLabels()));
    }
    
}
