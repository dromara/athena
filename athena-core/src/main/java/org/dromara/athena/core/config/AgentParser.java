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

import java.util.Arrays;
import java.util.Optional;

/**
 * The type Agent parser.
 */
public class AgentParser {
    
    private static final String CONFIG = "config";
    
    private final String[] agentArgs;
    
    /**
     * Instantiates a new Agent parser.
     *
     * @param args the args
     */
    public AgentParser(final String args) {
        agentArgs = Optional.ofNullable(args).map(arg -> arg.split(",")).orElse(new String[]{});
    }
    
    /**
     * Gets config path.
     *
     * @return the config path
     */
    public String getConfigPath() {
        if (agentArgs.length == 0) {
            return "/sharding-sphere.yaml";
        }
        return Arrays.stream(agentArgs)
                .filter(arg -> arg.startsWith(CONFIG))
                .map(arg -> arg.replace(CONFIG + ":", "")).findFirst().orElse(null);
    }
}
