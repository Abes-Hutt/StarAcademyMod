package abeshutt.staracademy.event;

import abeshutt.staracademy.util.MappingIterable;
import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public class CallbackEvent<CALLBACK> extends Event<CALLBACK, CallbackEvent.Listener<CALLBACK>> {

    protected final Class<CALLBACK> type;
    protected final Invoker<Listener<CALLBACK>, CALLBACK> invoker;

    protected CallbackEvent(Class<CALLBACK> type, Invoker<Listener<CALLBACK>, CALLBACK> invoker) {
        this.type = type;
        this.invoker = invoker;
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofComplex(Class<CALLBACK> type, Invoker<Listener<CALLBACK>, CALLBACK> invoker) {
        return new CallbackEvent<>(type, invoker);
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofComplex(Invoker<Listener<CALLBACK>, CALLBACK> invoker, CALLBACK... unused) {
        return CallbackEvent.ofComplex((Class<CALLBACK>)unused.getClass().getComponentType(), invoker);
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofSimple(Class<CALLBACK> type, Invoker<CALLBACK, CALLBACK> invoker) {
        return CallbackEvent.ofComplex(type, listeners -> {
            return invoker.invoke(new MappingIterable<>(listeners, Listener::getCallback));
        });
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofSimple(Invoker<CALLBACK, CALLBACK> invoker, CALLBACK... unused) {
        return CallbackEvent.ofSimple((Class<CALLBACK>)unused.getClass().getComponentType(), invoker);
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofVoid(Class<CALLBACK> type) {
        return CallbackEvent.ofSimple(type, listeners -> (CALLBACK)Proxy.newProxyInstance(
                    CallbackEvent.class.getClassLoader(), new Class[] { type },
                    new VoidInvocation<>(type, listeners)));
    }

    public static <CALLBACK> CallbackEvent<CALLBACK> ofVoid(CALLBACK... unused) {
        return CallbackEvent.ofVoid((Class<CALLBACK>)unused.getClass().getComponentType());
    }

    public Class<CALLBACK> getType() {
        return this.type;
    }

    @Override
    public CALLBACK invoker() {
        return this.invoker.invoke(this.orderedListeners);
    }

    public void register(CALLBACK callback) {
        this.register(null, callback, 0);
    }

    public void register(Object owner, CALLBACK callback) {
        this.register(owner, callback, 0);
    }

    public void register(CALLBACK callback, int order) {
        this.register(null, callback, order);
    }

    public void register(Object owner, CALLBACK callback, int order) {
        this.register(new Listener<>(owner, order, callback));
    }

    @FunctionalInterface
    public interface Invoker<INPUT, OUTPUT> {
        OUTPUT invoke(Iterable<INPUT> listeners);
    }

    public static class Listener<CALLBACK> extends Event.Listener {
        private final CALLBACK callback;

        public Listener(Object owner, int order, CALLBACK callback) {
            super(owner, order);
            this.callback = callback;
        }

        public CALLBACK getCallback() {
            return this.callback;
        }
    }

    protected static class VoidInvocation<CALLBACK> extends AbstractInvocationHandler {
        private final Class<CALLBACK> type;
        private final Iterable<CALLBACK> listeners;

        public VoidInvocation(Class<CALLBACK> type, Iterable<CALLBACK> listeners) {
            this.type = type;
            this.listeners = listeners;
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
            for(CALLBACK listener : this.listeners) {
                MethodHandles.lookup().unreflect(method).bindTo(listener)
                        .invokeWithArguments(args);
            }

            if(!this.type.isPrimitive()) {
                return null;
            }

            if(this.type == boolean.class) {
                return false;
            } else if(this.type == byte.class) {
                return (byte)0;
            } else if(this.type == short.class) {
                return (short)0;
            } else if(this.type == char.class) {
                return '\u0000';
            } else if(this.type == int.class) {
                return 0;
            } else if(this.type == long.class) {
                return 0L;
            } else if(this.type == float.class) {
                return 0.0F;
            } else if(this.type == double.class) {
                return 0.0D;
            }

            return null;
        }
    }

}
