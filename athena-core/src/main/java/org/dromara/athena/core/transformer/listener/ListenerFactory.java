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

package org.dromara.athena.core.transformer.listener;

import java.util.List;
import java.util.stream.Collectors;
import org.dromara.athena.core.config.Metric;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * The type Listener factory.
 */
public class ListenerFactory {
    
    /**
     * New listeners list.
     *
     * @param metrics       the metrics
     * @param adviceAdapter the advice adapter
     * @param argTypes      the arg types
     * @param access        the access
     * @return the list
     */
    public static List<Listener> newListeners(final List<Metric> metrics, final AdviceAdapter adviceAdapter, final Type[] argTypes, final int access) {
        return metrics.stream().map(metric -> newListener(metric, adviceAdapter, argTypes, access)).collect(Collectors.toList());
    }
    
    private static Listener newListener(final Metric metric, final AdviceAdapter adviceAdapter, final Type[] argTypes, final int access) {
        switch (metric.getType()) {
            case COUNTER:
                return new CounterListener(metric, adviceAdapter, argTypes, access);
            case GAUGE:
                return new GaugeListener(metric, adviceAdapter, argTypes, access);
            case HISTOGRAM:
                return new HistogramListener(metric, adviceAdapter, argTypes, access);
            default:
                throw new IllegalStateException("we not support metric type: " + metric.getType());
        }
    }
}
