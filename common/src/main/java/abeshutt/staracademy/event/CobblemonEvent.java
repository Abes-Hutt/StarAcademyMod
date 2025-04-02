package abeshutt.staracademy.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.reactive.Observable;
import com.cobblemon.mod.common.api.reactive.SimpleObservable;
import kotlin.Unit;

import java.util.function.Consumer;

public class CobblemonEvent<EVENT> extends Event<CobblemonEvent.Invoker<EVENT>, CobblemonEvent.Listener<EVENT>> {

    private final Observable<EVENT> source;

    protected CobblemonEvent(Observable<EVENT> source) {
        this.source = source;

        for(Priority priority : Priority.values()) {
            this.source.subscribe(priority, event -> {
                for(CobblemonEvent.Listener<EVENT> listener : this.orderedListeners) {
                    if(listener.getPriority() == priority) {
                        listener.getCallback().accept(event);
                    }
                }

                return Unit.INSTANCE;
            });
        }
    }

    public static <EVENT> CobblemonEvent<EVENT> of(Observable<EVENT> source) {
        return new CobblemonEvent<>(source);
    }

    @Override
    public Invoker<EVENT> invoker() {
        return event -> {
            if(this.source instanceof SimpleObservable<EVENT> observable) {
                observable.emit(event);
            }
        };
    }

    public void register(Consumer<EVENT> callback) {
        this.register(null, callback, Priority.NORMAL, 0);
    }

    public void register(Object owner, Consumer<EVENT> callback) {
        this.register(owner, callback, Priority.NORMAL, 0);
    }

    public void register(Consumer<EVENT> callback, int order) {
        this.register(null, callback, Priority.NORMAL, order);
    }

    public void register(Consumer<EVENT> callback, Priority priority) {
        this.register(null, callback, priority, 0);
    }

    public void register(Object owner, Consumer<EVENT> callback, Priority priority) {
        this.register(owner, callback, priority, 0);
    }

    public void register(Consumer<EVENT> callback, Priority priority, int order) {
        this.register(null, callback, priority, order);
    }

    public void register(Object owner, Consumer<EVENT> callback, Priority priority, int order) {
        this.register(new Listener<>(owner, order, priority, callback));
    }

    @FunctionalInterface
    public interface Invoker<EVENT> {
        void emit(EVENT event);

        default void emit(EVENT... events) {
            for(EVENT event : events) {
                this.emit(event);
            }
        }
    }

    protected static class Listener<EVENT> extends Event.Listener {
        private final Priority priority;
        private final Consumer<EVENT> callback;

        public Listener(Object owner, int order, Priority priority, Consumer<EVENT> callback) {
            super(owner, order);
            this.priority = priority;
            this.callback = callback;
        }

        public Priority getPriority() {
            return this.priority;
        }

        public Consumer<EVENT> getCallback() {
            return this.callback;
        }

        @Override
        public int compareTo(Event.Listener listener) {
            if(listener instanceof Listener<?> other) {
                int value = Integer.compare(this.getPriority().ordinal(), other.getPriority().ordinal());

                if(value != 0) {
                    return value;
                }
            }

            return super.compareTo(listener);
        }
    }

}
