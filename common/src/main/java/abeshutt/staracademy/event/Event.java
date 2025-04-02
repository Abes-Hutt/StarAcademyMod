package abeshutt.staracademy.event;

import java.util.*;

public abstract class Event<INVOKER, LISTENER extends Event.Listener> {

    protected final Map<Object, List<LISTENER>> keyedListeners;
    protected final List<LISTENER> orderedListeners;

    protected Event() {
        this.keyedListeners = new HashMap<>();
        this.orderedListeners = new ArrayList<>();
    }

    public abstract INVOKER invoker();

    public void register(LISTENER listener) {
        List<LISTENER> keyed = this.keyedListeners.computeIfAbsent(listener.getOwner(),
                    key -> new ArrayList<>());
        keyed.add(listener);

        List<LISTENER> ordered = this.orderedListeners;
        int index = Collections.binarySearch(ordered, listener);

        if(index >= 0) {
            while(index < ordered.size() - 1 && ordered.get(index + 1).compareTo(listener) == 0) {
                index++;
            }

            index++;
        } else {
            index = -index - 1;
        }

        ordered.add(index, listener);
    }

    public void release(Object owner) {
        List<LISTENER> listeners = this.keyedListeners.remove(owner);
        if(listeners == null || listeners.isEmpty()) return;
        this.orderedListeners.removeAll(new HashSet<>(listeners));
    }

    public void clear() {
        this.keyedListeners.clear();
        this.orderedListeners.clear();
    }

    public static class Listener implements Comparable<Listener> {
        private final Object owner;
        private final int order;

        public Listener(Object owner, int order) {
            this.owner = owner;
            this.order = order;
        }

        public Object getOwner() {
            return this.owner;
        }

        public int getOrder() {
            return this.order;
        }

        @Override
        public int compareTo(Listener other) {
            return Integer.compare(this.order, other.getOrder());
        }
    }

}
