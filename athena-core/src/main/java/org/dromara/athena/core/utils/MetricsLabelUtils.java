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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.objectweb.asm.Type;

/**
 * The type Metrics label utils.
 *
 * @author xiaoyu
 */
public class MetricsLabelUtils {
    
    private static final String REGEX = ":";
    
    private static final String PARAM = ".";
    
    private static final String REGEX_PATTERN = "\\$([0-9]+|this)([a-zA-Z.]+)*";
    
    /**
     * Get label names string [ ].
     *
     * @param labels the labels
     * @return the string [ ]
     */
    public static String[] getLabelNames(final List<String> labels) {
        return labels.stream().map(label -> label.split(REGEX)[0]).toArray(String[]::new);
    }
    
    /**
     * Check labels boolean.
     *
     * @param argTypes the arg types
     * @param labels   the labels
     * @return the boolean
     */
    public static boolean checkLabels(final Type[] argTypes, final List<String> labels) {
        return getLabelValues(labels).stream().allMatch(labelValue -> checkLabel(argTypes, labelValue));
    }
    
    /**
     * Gets label values.
     *
     * @param labels the labels
     * @return the label values
     */
    public static List<String> getLabelValues(final List<String> labels) {
        return labels.stream().map(label -> label.split(REGEX)[1]).collect(Collectors.toList());
    }
    
    /**
     * Has bean param boolean.
     *
     * @param labelValue the label value
     * @return the boolean
     */
    public static boolean hasBeanParam(final String labelValue) {
        return labelValue.contains(PARAM);
    }
    
    /**
     * Gets label var index.
     *
     * @param value the value
     * @return the label var index
     */
    public static String getLabelVarIndex(final String value) {
        return value.substring(value.indexOf(PARAM) + 1);
    }
    
    private static boolean checkLabel(final Type[] argTypes, final String labelValue) {
        if (labelValue.startsWith("$")) {
            if (!Pattern.matches(REGEX_PATTERN, labelValue)) {
                return false;
            }
            int index = getLabelValueIndex(labelValue);
            if (index >= argTypes.length) {
                return false;
            }
            Type argType = argTypes[index];
            return argType.getSort() != Type.ARRAY;
        }
        return true;
    }
    
    /**
     * Gets label value index.
     *
     * @param labelValue the label value
     * @return the label value index
     */
    public static int getLabelValueIndex(final String labelValue) {
        if (hasBeanParam(labelValue)) {
            return Integer.parseInt(labelValue.substring(1, labelValue.indexOf(PARAM)));
        }
        return Integer.parseInt(labelValue.substring(1));
    }
}
