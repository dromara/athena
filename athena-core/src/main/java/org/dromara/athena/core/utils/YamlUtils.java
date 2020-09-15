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

package org.dromara.athena.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import lombok.SneakyThrows;
import org.dromara.athena.core.config.AgentConfig;
import org.dromara.athena.core.config.Klass;

/**
 * The type Yaml utils.
 */
public class YamlUtils {
    
    /**
     * The constant YAML_MAPPER.
     */
    public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory()) {
        {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
            setVisibilityChecker(getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            registerModule(new KlassModule());
        }
    };
    
    /**
     * Create agent config agent config.
     *
     * @param filename the filename
     * @return the agent config
     */
    @SneakyThrows
    public static AgentConfig createAgentConfig(final String filename) {
        if (Objects.isNull(filename)) {
            return new AgentConfig();
        }
        try (InputStream inputStream = new FileInputStream(filename)) {
            return YAML_MAPPER.readValue(inputStream, AgentConfig.class);
        }
    }
    
    private static class KlassDeserializer extends KeyDeserializer {
        
        @Override
        public Object deserializeKey(final String key, final DeserializationContext ctxt) {
            String className = key.substring(0, key.lastIndexOf("."));
            String methodName = key.substring(key.lastIndexOf(".") + 1, key.indexOf("("));
            String descriptor = key.substring(key.indexOf("("));
            return new Klass(className, methodName, descriptor);
        }
    }
    
    private static class KlassModule extends SimpleModule {
    
        /**
         * Instantiates a new Klass module.
         */
        KlassModule() {
            addKeyDeserializer(Klass.class, new KlassDeserializer());
        }
    }
}
