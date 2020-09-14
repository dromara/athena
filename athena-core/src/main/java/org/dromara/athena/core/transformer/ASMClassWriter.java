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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * We need to override the getCommonSuperClass method of ClassWriter as the default implementation (don't know why)
 * triggers loading of classes used in the common super class resolution. This causes linkage and circular loading
 * issues and has been a real nightmare to sort out.. There wasn't anywhere on the AMS documentation or warnings
 * that I found this..
 *
 * The solution is taken from
 * https://github.com/naver/pinpoint/blob/master/profiler/src/main/java/com/navercorp/pinpoint/profiler/instrument/ASMClassWriter.java
 */

public final class ASMClassWriter extends ClassWriter {

    private static final String OBJECT_CLASS_INTERNAL_NAME = "java/lang/Object";

    private ClassLoader classLoader;

    public ASMClassWriter(final int flags, final ClassLoader classLoader) {
        super(flags);
        this.classLoader = classLoader;
    }

    @Override
    protected String getCommonSuperClass(final String classInternalName1, final String classInternalName2) {
        return get(classInternalName1, classInternalName2);
    }
    
    private String get(final String classInternalName1, final String classInternalName2) {
        if (classInternalName1 == null || classInternalName1.equals(OBJECT_CLASS_INTERNAL_NAME)
                || classInternalName2 == null || classInternalName2.equals(OBJECT_CLASS_INTERNAL_NAME)) {
            // object is the root of the class hierarchy.
            return OBJECT_CLASS_INTERNAL_NAME;
        }

        if (classInternalName1.equals(classInternalName2)) {
            // two equal.
            return classInternalName1;
        }
        final ClassReader classReader1 = getClassReader(classInternalName1);
        if (classReader1 == null) {
            return OBJECT_CLASS_INTERNAL_NAME;
        }
        final ClassReader classReader2 = getClassReader(classInternalName2);
        if (classReader2 == null) {
            return OBJECT_CLASS_INTERNAL_NAME;
        }
        // interface.
        if (isInterface(classReader1)) {
            // <interface, class> or <interface, interface>
            return getCommonInterface(classReader1, classReader2);
        }
        // interface.
        if (isInterface(classReader2)) {
            // <class, interface>
            return getCommonInterface(classReader2, classReader1);
        }
        return getCommonClass(classReader1, classReader2);
    }

    private boolean isInterface(final ClassReader classReader) {
        return (classReader.getAccess() & Opcodes.ACC_INTERFACE) != 0;
    }
    
    private String getCommonInterface(final ClassReader classReader1, final ClassReader classReader2) {
        final Set<String> interfaceHierarchy = new HashSet<>();
        traversalInterfaceHierarchy(interfaceHierarchy, classReader1);
        if (isInterface(classReader2)) {
            if (interfaceHierarchy.contains(classReader2.getClassName())) {
                return classReader2.getClassName();
            }
        }
        final String interfaceInternalName = getImplementedInterface(interfaceHierarchy, classReader2);
        if (interfaceInternalName != null) {
            return interfaceInternalName;
        }
        return OBJECT_CLASS_INTERNAL_NAME;
    }

    private void traversalInterfaceHierarchy(final Set<String> interfaceHierarchy, final ClassReader classReader) {
        if (classReader != null && interfaceHierarchy.add(classReader.getClassName())) {
            for (String interfaceInternalName : classReader.getInterfaces()) {
                traversalInterfaceHierarchy(interfaceHierarchy, getClassReader(interfaceInternalName));
            }
        }
    }

    private String getImplementedInterface(final Set<String> interfaceHierarchy, final ClassReader classReader) {
        ClassReader cr = classReader;
        while (cr != null) {
            final String[] interfaceInternalNames = cr.getInterfaces();
            for (String name : interfaceInternalNames) {
                if (name != null && interfaceHierarchy.contains(name)) {
                    return name;
                }
            }
            for (String name : interfaceInternalNames) {
                final String interfaceInternalName = getImplementedInterface(interfaceHierarchy, getClassReader(name));
                if (interfaceInternalName != null) {
                    return interfaceInternalName;
                }
            }
            final String superClassInternalName = cr.getSuperName();
            if (superClassInternalName == null || superClassInternalName.equals(OBJECT_CLASS_INTERNAL_NAME)) {
                break;
            }
            cr = getClassReader(superClassInternalName);
        }
        return null;
    }

    private String getCommonClass(final ClassReader classReader1, final ClassReader classReader2) {
        final Set<String> classHierarchy = new HashSet<>();
        classHierarchy.add(classReader1.getClassName());
        classHierarchy.add(classReader2.getClassName());
        String superClassInternalName1 = classReader1.getSuperName();
        if (!classHierarchy.add(superClassInternalName1)) {
            // find common super class.
            return superClassInternalName1;
        }
        String superClassInternalName2 = classReader2.getSuperName();
        if (!classHierarchy.add(superClassInternalName2)) {
            // find common super class.
            return superClassInternalName2;
        }
        while (superClassInternalName1 != null || superClassInternalName2 != null) {
            if (superClassInternalName1 != null) {
                superClassInternalName1 = getSuperClassInternalName(superClassInternalName1);
                if (superClassInternalName1 != null) {
                    if (!classHierarchy.add(superClassInternalName1)) {
                        return superClassInternalName1;
                    }
                }
            }
            if (superClassInternalName2 != null) {
                superClassInternalName2 = getSuperClassInternalName(superClassInternalName2);
                if (superClassInternalName2 != null) {
                    if (!classHierarchy.add(superClassInternalName2)) {
                        return superClassInternalName2;
                    }
                }
            }
        }
        return OBJECT_CLASS_INTERNAL_NAME;
    }
    
    private String getSuperClassInternalName(final String classInternalName) {
        final ClassReader classReader = getClassReader(classInternalName);
        if (classReader == null) {
            return null;
        }
        return classReader.getSuperName();
    }

    private ClassReader getClassReader(final String classInternalName) {
        if (classInternalName == null || classLoader == null) {
            return null;
        }
        try (InputStream in = classLoader.getResourceAsStream(classInternalName + ".class")) {
            if (in != null) {
                return new ClassReader(in);
            }
        } catch (IOException ignored) {
            // not found class.
        }
        return null;
    }
}