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

package org.dromara.athena.prometheus;

import java.util.Map;
import org.dromara.athena.spi.MetricRegister;

/**
 * The type Prometheus metrics register.
 *
 * @author xiaoyu
 */
public class PrometheusMetricsRegister implements MetricRegister {
    
    private final Map<String, Object> configuration;
    
    public PrometheusMetricsRegister(final Map<String, Object> configuration) {
        this.configuration = configuration;
    }
    
    public void startEndpoint() {
    
    }
    
    public void registerGauge(final String name, final String[] labelNames, final String doc) {
    
    }
    
    public void registerCounter(final String name, final String[] labelNames, final String doc) {
    
    }
    
    public void registerTimer(final String name, final String[] labelNames, final String doc) {
    
    }
    
    public void recordCount(final String name, final String[] labelValues) {
    
    }
    
    public void recordCount(final String name, final String[] labelValues, final long count) {
    
    }
    
    public void recordGaugeInc(final String name, final String[] labelValues) {
    
    }
    
    public void recordGaugeDec(final String name, final String[] labelValues) {
    
    }
    
    public void recordTime(final String name, final String[] labelValues, final long duration) {
    
    }
    
}
