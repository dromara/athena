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

import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dromara.athena.core.config.Metric;
import org.dromara.athena.core.reporter.MetricsReporter;
import org.dromara.athena.core.utils.MetricsLabelUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;


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
    
    protected void injectLabel(final Metric metric) {
        int nameVar = aa.newLocal(Type.getType(String.class));
        aa.visitLdcInsn(metric.getName());
        aa.visitVarInsn(ASTORE, nameVar);
        List<String> labelValues = MetricsLabelUtils.getLabelValues(metric.getLabels());
        if (CollectionUtils.isNotEmpty(labelValues)) {
            int labelVar = injectLabelValues(labelValues);
            aa.visitVarInsn(ALOAD, nameVar);
            aa.visitVarInsn(ALOAD, labelVar);
        } else {
            aa.visitVarInsn(ALOAD, nameVar);
            aa.visitInsn(ACONST_NULL);
        }
    }
    
    protected int injectLabelValues(final List<String> labelValues) {
        aa.visitInsn(labelValues.size() + 3);
        aa.visitTypeInsn(ANEWARRAY, Type.getInternalName(String.class));
        for (int i = 0; i < labelValues.size(); i++) {
            aa.visitInsn(DUP);
            aa.visitInsn(i + 3);
            injectLabelValue(labelValues.get(i));
        }
        int labelVar = aa.newLocal(Type.getType(String[].class));
        aa.visitVarInsn(ASTORE, labelVar);
        return labelVar;
    }
    
    private void injectLabelValue(final String labelValue) {
        if (MetricsLabelUtils.hasBeanParam(labelValue)) {
            aa.visitLdcInsn(MetricsLabelUtils.getLabelVarIndex(labelValue));
            aa.visitMethodInsn(INVOKESTATIC, Type.getInternalName(PropertyUtils.class), "getNestedProperty",
                    Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class), Type.getType(String.class)), false);
        }
        aa.visitMethodInsn(INVOKESTATIC, Type.getInternalName(String.class), "valueOf",
                Type.getMethodDescriptor(Type.getType(String.class), Type.getType(Object.class)), false);
        aa.visitInsn(AASTORE);
    }
    
}
