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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * The type Agent config.
 *
 * @author xiaoyu
 */
@Getter
public class AgentConfig {
    
    private Set<String> imports;
    
    private Map<Klass, List<Metric>> metrics;
    
    private Map<String, Object> system;
    
    /**
     * Instantiates a new Agent config.
     */
    public AgentConfig() {
        this(null, null, null);
    }
    
    /**
     * Instantiates a new Agent config.
     *
     * @param metrics the metrics
     * @param imports the imports
     * @param system  the system
     */
    @JsonCreator
    public AgentConfig(
            @JsonProperty("metrics") Map<Klass, List<Metric>> metrics,
            @JsonProperty("imports") Set<String> imports,
            @JsonProperty("system") Map<String, Object> system) {
        this.imports = Optional.ofNullable(imports).orElse(Collections.emptySet());
        this.metrics = Optional.ofNullable(metrics).map(entry -> mergeByImports(entry, imports)).orElse(Collections.emptyMap());
        this.system = Optional.ofNullable(system).orElse(Collections.emptyMap());
    }
    
    public boolean hasMetric(final String className) {
        return metrics.keySet().stream().anyMatch(klass -> klass.getClassName().equalsIgnoreCase(className));
    }
    
    public List<Metric> findByClassName(final String className) {
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
       return metrics.keySet().stream().filter(klass -> klass.getClassName().equalsIgnoreCase(className))
                .flatMap(klass -> metrics.get(klass).stream()).collect(Collectors.toList());
    }
    
    public List<Metric> findByKey(final String className, final String methodName, final String descriptor) {
        return metrics.getOrDefault(new Klass(className, methodName, descriptor), Collections.emptyList());
    }
    
    private Map<Klass, List<Metric>> mergeByImports(final Map<Klass, List<Metric>> metrics, final Set<String> imports) {
        return null;
    }
}
