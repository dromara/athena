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

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.athena.prometheus.collector.BuildInfoCollector;
import org.dromara.athena.spi.MetricRegister;

/**
 * The type Prometheus metrics register.
 *
 * @author xiaoyu
 */
public class PrometheusMetricsRegister implements MetricRegister {
    
    private static final int DEFAULT_HTTP_PORT = 9090;
    
    private static final Map<String, Counter> COUNTER_MAP = new ConcurrentHashMap<>();
    
    private static final Map<String, Gauge> GAUGE_MAP = new ConcurrentHashMap<>();
    
    private static final Map<String, Histogram> HISTOGRAM_MAP = new ConcurrentHashMap<>();
    
    private final Map<String, Object> configMap;
    
    /**
     * Instantiates a new Prometheus metrics register.
     *
     * @param configMap the config map
     */
    public PrometheusMetricsRegister(final Map<String, Object> configMap) {
        this.configMap = configMap;
        registerJvm(configMap);
        startServer();
    }
    
    @Override
    public void registerCounter(final String name, final String[] labelNames, final String doc) {
        Counter.Builder builder = Counter.build().name(name).help(doc);
        if (labelNames != null) {
            builder.labelNames(labelNames);
        }
        COUNTER_MAP.put(name, builder.register());
    }
    
    @Override
    public void registerGauge(final String name, final String[] labelNames, final String doc) {
        if (!GAUGE_MAP.containsKey(name)) {
            Gauge.Builder builder = Gauge.build().name(name).help(doc);
            if (labelNames != null) {
                builder.labelNames(labelNames);
            }
            GAUGE_MAP.put(name, builder.register());
        }
    }
    
    @Override
    public void registerHistogram(final String name, final String[] labelNames, final String doc) {
        Histogram.Builder builder = Histogram.build().name(name).help(doc);
        if (labelNames != null) {
            builder.labelNames(labelNames);
        }
        HISTOGRAM_MAP.put(name, builder.register());
    }
    
    @Override
    public void counterInc(final String name, final String[] labelValues) {
        Counter counter = COUNTER_MAP.get(name);
        if (labelValues != null) {
            counter.labels(labelValues).inc();
        } else {
            counter.inc();
        }
    }
    
    @Override
    public void counterInc(final String name, final String[] labelValues, final long count) {
        Counter counter = COUNTER_MAP.get(name);
        if (labelValues != null) {
            counter.labels(labelValues).inc(count);
        } else {
            counter.inc(count);
        }
    }
    
    @Override
    public void gaugeInc(final String name, final String[] labelValues) {
        Gauge gauge = GAUGE_MAP.get(name);
        if (labelValues != null) {
            gauge.labels(labelValues).inc();
        } else {
            gauge.inc();
        }
    }
    
    @Override
    public void gaugeDec(final String name, final String[] labelValues) {
        Gauge gauge = GAUGE_MAP.get(name);
        if (labelValues != null) {
            gauge.labels(labelValues).dec();
        } else {
            gauge.dec();
        }
    }
    
    @Override
    public void recordTime(final String name, final String[] labelValues, final long duration) {
        Histogram histogram = HISTOGRAM_MAP.get(name);
        if (labelValues != null) {
            histogram.labels(labelValues).observe(duration);
        } else {
            histogram.observe(duration);
        }
    }
    
    /**
     * Start server.
     */
    public void startServer() {
        int port = DEFAULT_HTTP_PORT;
        if (configMap.containsKey("port")) {
            port = (Integer) configMap.get("port");
        }
        String host = (String) configMap.get("host");
        InetSocketAddress inetSocketAddress;
        if ("".equals(host) || null == host) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(host, port);
        }
        try {
            new HTTPServer(inetSocketAddress, CollectorRegistry.defaultRegistry, true);
            System.out.println("you start prometheus metrics http server  host is : " + inetSocketAddress.getPort() + " port is : " + inetSocketAddress.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void registerJvm(final Map<String, Object> configMap) {
        if (!configMap.isEmpty()) {
            boolean enabled = Boolean.parseBoolean(configMap.get("jvmEnabled").toString());
            if (enabled) {
                new BuildInfoCollector().register();
                DefaultExports.initialize();
            }
        }
    }
}
