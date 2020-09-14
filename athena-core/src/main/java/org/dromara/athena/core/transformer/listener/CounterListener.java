package org.dromara.athena.core.transformer.listener;

import org.dromara.athena.core.config.Metric;
import org.dromara.athena.core.enums.MetricType;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * The type Counter listener.
 *
 * @author xiaoyu
 */
public class CounterListener extends AbstractListener {
    
    private static final String METHOD = "counterInc";
    
    private static final String SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(String[].class));
    
    private final Metric metric;
    
    /**
     * Instantiates a new Counter listener.
     *
     * @param metric    the metric
     * @param aa        the aa
     * @param argTypes  the arg types
     * @param access    the access
     */
    public CounterListener(Metric metric, AdviceAdapter aa, Type[] argTypes, int access) {
        super(aa, argTypes, access);
        this.metric = metric;
    }
    
    @Override
    public void listenerOnMethodEnter() {
        aa.visitMethodInsn(INVOKESTATIC, METRICS_REPORTER_CLASSNAME, METHOD, SIGNATURE, false);
    }
    
}
