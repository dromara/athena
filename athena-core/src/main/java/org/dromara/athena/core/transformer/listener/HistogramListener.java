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
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.LSUB;

/**
 * The type Histogram listener.
 *
 * @author xiaoyu
 */
public class HistogramListener extends AbstractListener {

    private static final String METHOD = "recordTime";
    
    private static final String SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE,Type.getType(String.class), Type.getType(String[].class), Type.LONG_TYPE);
    
    private final Metric metric;
    
    private int startTime;
    
    private Label startFinally;
    
    /**
     * Instantiates a new Histogram listener.
     *
     * @param metric   the metric
     * @param aa       the aa
     * @param argTypes the arg types
     * @param access   the access
     */
    public HistogramListener(final Metric metric, final AdviceAdapter aa, final Type[] argTypes, final int access) {
        super(aa, argTypes, access);
        this.metric = metric;
    }

    @Override
    public void listenerOnMethodEnter() {
        startFinally = new Label();
        startTime = aa.newLocal(Type.LONG_TYPE);
        aa.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        aa.visitVarInsn(LSTORE, startTime);
        aa.visitLabel(startFinally);
    }

    @Override
    public void listenerOnVisitMaxs(final int maxStack, final int maxLocals) {
        Label endFinally = new Label();
        aa.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
        aa.visitLabel(endFinally);
        onFinally();
        aa.visitInsn(ATHROW);
    }

    @Override
    public void listenerOnMethodExit(final int opcode) {
        if (opcode != ATHROW) {
            onFinally();
        }
    }

    private void onFinally() {
        injectLabel(metric);
        aa.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        aa.visitVarInsn(LLOAD, startTime);
        aa.visitInsn(LSUB);
        aa.visitMethodInsn(INVOKESTATIC, METRICS_REPORTER_CLASSNAME, METHOD, SIGNATURE, false);
    }
}
