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
import org.dromara.athena.core.config.AgentConfig;
import org.dromara.athena.core.config.Metric;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * The type Metrics class visitor.
 *
 * @author xiaoyu
 */
public class MetricsClassVisitor extends ClassVisitor {

    private boolean isInterface;
    
    private String className;
    
    private AgentConfig agentConfig;
    
    /**
     * Instantiates a new Metrics class visitor.
     *
     * @param cv          the cv
     * @param agentConfig the agent config
     */
    public MetricsClassVisitor(final ClassVisitor cv, final AgentConfig agentConfig) {
        super(ASM5, cv);
        this.agentConfig = agentConfig;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.isInterface = 0 != (access & ACC_INTERFACE);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        boolean isSyntheticMethod = 0 != (access & ACC_SYNTHETIC);
        if (!isInterface && !isSyntheticMethod && mv != null) {
            List<Metric> metadata = agentConfig.findByKey(className, name, desc);
            mv = new MetricsAdapter(mv, className, access, name, desc, metadata);
            mv = new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
