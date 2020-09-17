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

package org.dromara.athena.core.transformer;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import lombok.SneakyThrows;
import org.dromara.athena.core.config.AgentConfig;
import org.dromara.athena.core.config.Debugger;
import org.dromara.athena.core.transformer.visitor.MetricsClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * The type Metrics class transformer.
 *
 * @author xiaoyu
 */
public class MetricsClassTransformer implements ClassFileTransformer {
    
    private final AgentConfig agentConfig;
    
    private final Debugger debugger;
    
    /**
     * Instantiates a new Metrics class transformer.
     *
     * @param agentConfig the agent config
     */
    public MetricsClassTransformer(final AgentConfig agentConfig) {
        this.agentConfig = agentConfig;
        this.debugger = agentConfig.getDebugger();
    }
    
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        try {
            if (agentConfig.hasMetric(className)) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ASMClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS, loader);
                ClassVisitor cv = new MetricsClassVisitor(cw, agentConfig);
                cr.accept(cv, EXPAND_FRAMES);
                output(cw, className);
                return cw.toByteArray();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    
        return classfileBuffer;
    }
    
    @SneakyThrows
    private void output(final ClassWriter cw, final String className) {
        if (debugger.isDebug()) {
            String path = getPath() + "/" + className.substring(className.lastIndexOf("/") + 1) + ".class";
            System.out.println("output path is " + path);
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(cw.toByteArray());
            }
        }
    }
    
    private String getPath() {
        String path;
        if (null == debugger.getOutPath() || "".equalsIgnoreCase(debugger.getOutPath())) {
            path = System.getProperty("user.dir");
        } else {
            path = debugger.getOutPath();
        }
        return path;
    }
    
}
