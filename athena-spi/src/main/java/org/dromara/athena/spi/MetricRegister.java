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

/**
 * The interface Metric register.
 *
 * @author xiaoyu
 */
public interface MetricRegister {
    
    /**
     * Register gauge.
     *
     * @param name       the name
     * @param labelNames the label names
     * @param doc        the doc
     */
    void registerGauge(String name, String[] labelNames, String doc);
    
    /**
     * Register counter.
     *
     * @param name       the name
     * @param labelNames the label names
     * @param doc        the doc
     */
    void registerCounter(String name, String[] labelNames, String doc);
    
    /**
     * Register timer.
     *
     * @param name       the name
     * @param labelNames the label names
     * @param doc        the doc
     */
    void registerTimer(String name, String[] labelNames, String doc);
    
    /**
     * Record count.
     *
     * @param name        the name
     * @param labelValues the label values
     */
    void recordCount(String name, String[] labelValues);
    
    /**
     * Record count.
     *
     * @param name        the name
     * @param labelValues the label values
     * @param count       the count
     */
    void recordCount(String name, String[] labelValues, long count);
    
    /**
     * Record gauge inc.
     *
     * @param name        the name
     * @param labelValues the label values
     */
    void recordGaugeInc(String name, String[] labelValues);
    
    /**
     * Record gauge dec.
     *
     * @param name        the name
     * @param labelValues the label values
     */
    void recordGaugeDec(String name, String[] labelValues);
    
    /**
     * Record time.
     *
     * @param name        the name
     * @param labelValues the label values
     * @param duration    the duration
     */
    void recordTime(String name, String[] labelValues, long duration);
    
    /**
     * Start endpoint.
     */
    void startEndpoint();

}
