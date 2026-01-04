package abeshutt.staracademy.live.api.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TypeRegistry<T> {

    private final String key;
    private final Map<String, Supplier<? extends T>> idToConstructor;
    private final Map<Class<? extends T>, String> typeToId;

    public TypeRegistry(String key) {
        this.key = key;
        this.idToConstructor = new HashMap<>();
        this.typeToId = new HashMap<>();
    }

    public String getKey() {
        return this.key;
    }

    public Optional<T> construct(String id) {
        return Optional.ofNullable(this.idToConstructor.get(id)).map(Supplier::get);
    }

    public <C extends T> Optional<String> getId(Class<C> type) {
        return Optional.ofNullable(this.typeToId.get(type));
    }

    public Optional<String> getId(T value) {
        return this.getId((Class<T>)value.getClass());
    }

    public <C extends T> TypeRegistry<T> register(String id, Class<C> type, Supplier<C> supplier) {
        this.idToConstructor.put(id, supplier);
        this.typeToId.put(type, id);
        return this;
    }

}
