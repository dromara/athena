package org.dromara.athena.core.transformer.listener;

import org.dromara.athena.core.config.Metric;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * The type Gauge listener.
 *
 * @author xiaoyu
 */
public class GaugeListener extends AbstractListener {

    private static final String INC_METHOD = "gaugeInc";

    private static final String SIGNATURE = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(String[].class));
    
    private final Metric metric;
    
    /**
     * Instantiates a new Gauge listener.
     *
     * @param metric   the metric
     * @param aa       the aa
     * @param argTypes the arg types
     * @param access   the access
     */
    public GaugeListener(final Metric metric, final AdviceAdapter aa, final Type[] argTypes, final int access) {
        super(aa, argTypes, access);
        this.metric = metric;
    }

    @Override
    public void listenerOnMethodEnter() {
        Label startFinally = new Label();
        aa.visitLabel(startFinally);
        //injectNameAndLabelToStack(metric);
        aa.visitMethodInsn(INVOKESTATIC, METRICS_REPORTER_CLASSNAME, INC_METHOD, SIGNATURE, false);
    }
    
}
