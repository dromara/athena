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

package org.dromara.athena.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.objectweb.asm.Type;

/**
 * The type Agent config.
 *
 * @author xiaoyu
 */
@Getter
public class AgentConfig {
    
    private Set<String> imports;
    
    private Map<Klass, List<Metric>> metrics;
    
    private Map<String, Object> configMap;
    
    private Debugger debugger;
    
    /**
     * Instantiates a new Agent config.
     */
    public AgentConfig() {
        this(null, null, null, null);
    }
    
    /**
     * Instantiates a new Agent config.
     *
     * @param metrics   the metrics
     * @param imports   the imports
     * @param configMap the configMap
     * @param debugger  the debugger
     */
    @JsonCreator
    public AgentConfig(
            @JsonProperty("metrics") final Map<Klass, List<Metric>> metrics,
            @JsonProperty("imports") final Set<String> imports,
            @JsonProperty("configMap") final Map<String, Object> configMap,
            @JsonProperty("debugger") final Debugger debugger) {
        this.imports = Optional.ofNullable(imports).orElse(Collections.emptySet());
        this.metrics = Optional.ofNullable(metrics).map(entry -> mergeByImports(entry, imports)).orElse(Collections.emptyMap());
        this.configMap = Optional.ofNullable(configMap).orElse(Collections.emptyMap());
        this.debugger = Optional.ofNullable(debugger).orElse(new Debugger());
    }
    
    /**
     * Has metric boolean.
     *
     * @param className the class name
     * @return the boolean
     */
    public boolean hasMetric(final String className) {
        return metrics.keySet().stream().anyMatch(klass -> klass.getClassName().equalsIgnoreCase(className));
    }
    
    /**
     * Find by class name list.
     *
     * @param className the class name
     * @return the list
     */
    public List<Metric> findByClassName(final String className) {
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
        return metrics.keySet().stream().filter(klass -> klass.getClassName().equalsIgnoreCase(className))
               .flatMap(klass -> metrics.get(klass).stream()).collect(Collectors.toList());
    }
    
    /**
     * Find by key list.
     *
     * @param className  the class name
     * @param methodName the method name
     * @param descriptor the descriptor
     * @return the list
     */
    public List<Metric> findByKey(final String className, final String methodName, final String descriptor) {
        return metrics.getOrDefault(new Klass(className, methodName, descriptor), Collections.emptyList());
    }
    
    private Map<Klass, List<Metric>> mergeByImports(final Map<Klass, List<Metric>> metrics, final Set<String> imports) {
        Map<String, String> importsMap = toImportsMap(imports);
        Map<Klass, List<Metric>> processed = new HashMap<>();
        for (Map.Entry<Klass, List<Metric>> entry : metrics.entrySet()) {
            Klass key = entry.getKey();
            String className = importsMap.get(key.getClassName());
            if (className == null) {
                className = key.getClassName();
            }
            String descriptor = key.getDescriptor();
            Map<String, String> methodMap = toMethodDescriptorMap(descriptor);
            for (String name : methodMap.keySet()) {
                if (importsMap.containsKey(name)) {
                    descriptor = descriptor.replaceAll(className, importsMap.get(className));
                }
            }
            key = new Klass(className, key.getMethod(), descriptor);
            processed.put(key, entry.getValue());
        }
        return processed;
    }
    
    private Map<String, String> toMethodDescriptorMap(final String descriptor) {
        Type type = Type.getMethodType(descriptor);
        Set<String> classes = new HashSet<>();
        classes.add(type.getReturnType().getClassName());
        Type[] arguments = type.getArgumentTypes();
        if (arguments != null) {
            for (Type arg : arguments) {
                classes.add(arg.getClassName());
            }
        }
        return toImportsMap(classes);
    }
    
    private Map<String, String> toImportsMap(final Set<String> imports) {
        return imports.stream().collect(Collectors.toMap(e -> e.substring(e.lastIndexOf("/") + 1), e -> e));
    }
}
