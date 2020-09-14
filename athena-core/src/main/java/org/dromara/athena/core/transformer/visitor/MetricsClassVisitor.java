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

import java.util.List;
import java.util.Objects;
import org.dromara.athena.core.config.AgentConfig;
import org.dromara.athena.core.config.Metric;
import org.dromara.athena.core.utils.FieldUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 *
 * @author xiaoyu
 */
public class MetricsClassVisitor extends ClassVisitor {

    private boolean isInterface;
    
    private String className;
    
    private boolean visitedStaticBlock = false;
    
    private AgentConfig agentConfig;
    
    private List<Metric> classMetrics;

    public MetricsClassVisitor(final ClassVisitor cv, final AgentConfig agentConfig) {
        super(ASM5, cv);
        this.agentConfig = agentConfig;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.isInterface = (access & ACC_INTERFACE) != 0;
        this.classMetrics = agentConfig.findByClassName(className);
        for (Metric metric : classMetrics) {
            super.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, FieldUtils.named(metric),
                    Type.getDescriptor(metric.getType().getType()), null, null).visitEnd();
        }
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        boolean isSyntheticMethod = (access & ACC_SYNTHETIC) != 0;
        boolean isStaticMethod = (access & ACC_STATIC) != 0;
        if (!isInterface && !isSyntheticMethod && mv != null) {
            List<Metric> metadata = agentConfig.findByKey(className, name, desc);
            mv = new MetricsAdapter(mv, className, access, name, desc, metadata);
            mv = new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
        }
        // initialize static fields
        if (Objects.equals(name, "<clinit>") && isStaticMethod && mv != null) {
            visitedStaticBlock = true;
            //mv = new StaticInitializerMethodVisitor(mv, classMetrics, className, access, name, desc);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!visitedStaticBlock) {
            MethodVisitor mv = super.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            //mv = new StaticInitializerMethodVisitor(mv, classMetrics, className, ACC_STATIC, "<clinit>", "()V");
            mv.visitCode();
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
