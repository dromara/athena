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
    
    @Override
    public void registerGauge(final String name, final String[] labelNames, final String doc) {
    
    }
    
    @Override
    public void registerCounter(final String name, final String[] labelNames, final String doc) {
    
    }
    
    @Override
    public void registerHistogram(final String name, final String[] labelNames, final String doc) {
    
    }
    
    @Override
    public void counterInc(final String name, final String[] labelValues) {
    
    }
    
    @Override
    public void counterInc(final String name, final String[] labelValues, final long count) {
    
    }
    
    @Override
    public void gaugeInc(final String name, final String[] labelValues) {
    
    }
    
    @Override
    public void gaugeDec(final String name, final String[] labelValues) {
    
    }
    
    @Override
    public void recordTime(final String name, final String[] labelValues, final long duration) {
    
    }
    
}
