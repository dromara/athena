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

package org.dromara.athena.spi;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The enum Metrics provider.
 */
public enum MetricsProvider {
    
    /**
     * Instance metrics provider.
     */
    INSTANCE;
    
    private static MetricRegisterFactory metricRegisterFactory;
    
    private Map<String, Object> jvmConfig;
    
    static {
        metricRegisterFactory = ServiceLoader.load(MetricRegisterFactory.class).iterator().next();
    }
    
    /**
     * Register jvm config.
     *
     * @param jvmConfig the jvm config
     */
    public void registerJvmConfig(final Map<String, Object> jvmConfig) {
        this.jvmConfig = jvmConfig;
    }
    
    /**
     * Create metric system metric register.
     *
     * @return the metric register
     */
    public MetricRegister newInstance() {
        return metricRegisterFactory.newInstance(jvmConfig);
    }
    
}
