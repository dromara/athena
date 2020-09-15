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

package org.dromara.athena.core;

import java.lang.instrument.Instrumentation;
import org.dromara.athena.core.config.AgentConfig;
import org.dromara.athena.core.config.AgentParser;
import org.dromara.athena.core.transformer.MetricsClassTransformer;
import org.dromara.athena.core.utils.YamlUtils;
import org.dromara.athena.spi.MetricsProvider;

/**
 * The type Athena agent.
 *
 * @author xiaoyu
 */
public class AthenaAgent {
    
    /**
     * Premain.
     *
     * @param args            the args
     * @param instrumentation the instrumentation
     */
    public static void premain(final String args, final Instrumentation instrumentation) {
        AgentParser parser = new AgentParser(args);
        AgentConfig agentConfig = YamlUtils.createAgentConfig(parser.getConfigPath());
        MetricsProvider.INSTANCE.registerConfigMap(agentConfig.getConfigMap());
        instrumentation.addTransformer(new MetricsClassTransformer(agentConfig), instrumentation.isRetransformClassesSupported());
    }
}
