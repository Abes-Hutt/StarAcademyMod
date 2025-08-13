package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModifierReference<T> {

    private final int order;
    private Attribute<T> modifier;
    private final AttributePath<T> path;
    private boolean removed;
    private final Map<Object, Runnable> changeListeners;

    protected ModifierReference(int order, Attribute<T> modifier, AttributePath<T> path, boolean removed) {
        this.order = order;
        this.modifier = modifier;
        this.path = path;
        this.removed = removed;
        this.changeListeners = new HashMap<>();
    }

    public void addChangeListener(Object owner, Runnable runnable) {
        this.changeListeners.put(owner, runnable);
    }

    public void removeChangeListener(Object owner) {
        this.changeListeners.remove(owner);
    }

    public static <T> ModifierReference<T> empty(int order, AttributePath<T> path) {
        return new ModifierReference<>(order, null, path, false);
    }

    public static <T> ModifierReference<T> empty(int order) {
        return new ModifierReference<>(order, null, AttributePath.empty(), false);
    }

    public static <T> ModifierReference<T> empty() {
        return new ModifierReference<>(0, null, AttributePath.empty(), false);
    }

    public static <T> ModifierReference<T> of(int order, Attribute<T> modifier, AttributePath<T> path) {
        return new ModifierReference<>(order, modifier, path, false);
    }

    public static <T> ModifierReference<T> of(int order, Attribute<T> modifier) {
        return new ModifierReference<>(order, modifier, AttributePath.empty(), false);
    }

    public static <T> ModifierReference<T> of(Attribute<T> modifier) {
        return new ModifierReference<>(0, modifier, AttributePath.empty(), false);
    }

    public static <T> Adapter<T> adapter(IAdapter<Attribute<T>, ?, ?, ?> modifierAdapter) {
        return new Adapter<>(modifierAdapter);
    }

    public int getOrder() {
        return this.order;
    }

    public Attribute<T> get() {
        return this.modifier;
    }

    public ModifierReference<T> set(Attribute<T> modifier) {
        this.modifier = modifier;
        this.changeListeners.values().forEach(Runnable::run);
        return this;
    }

    public AttributePath<T> getPath() {
        return this.path;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void dispose() {
        this.removed = true;
        this.changeListeners.values().forEach(Runnable::run);
    }

    public static class Adapter<T> implements ISimpleAdapter<ModifierReference<T>, NbtElement, JsonElement> {
        private final IAdapter<Attribute<T>, NbtElement, JsonElement, ?> modifierAdapter;

        protected Adapter(IAdapter<Attribute<T>, ?, ?, ?> modifierAdapter) {
            this.modifierAdapter = (IAdapter)modifierAdapter;
        }

        @Override
        public Optional<JsonElement> writeJson(ModifierReference<T> value) {
            if(value == null) {
                return Optional.empty();
            }

            return Optional.of(new JsonObject()).map(json -> {
                if(value.getOrder() != 0) {
                    Adapters.INT.writeJson(value.getOrder()).ifPresent(tag -> json.add("order", tag));
                }

                this.modifierAdapter.writeJson(value.get(), null).ifPresent(tag -> json.add("modifier", tag));
                Adapters.ATTRIBUTE_PATH.writeJson(value.getPath()).ifPresent(tag -> json.add("path", tag));

                if(value.isRemoved()) {
                    Adapters.BOOLEAN.writeJson(value.isRemoved()).ifPresent(tag -> json.add("removed", tag));
                }

                return json;
            });
        }

        @Override
        public Optional<ModifierReference<T>> readJson(JsonElement json) {
            if(json instanceof JsonObject object) {
                return Optional.of(new ModifierReference<T>(
                        Adapters.INT.readJson(object.get("order")).orElse(0),
                        this.modifierAdapter.readJson(object.get("modifier"), null).orElse(null),
                        Adapters.ATTRIBUTE_PATH.readJson(object.get("path")).orElse(AttributePath.empty()),
                        Adapters.BOOLEAN.readJson(object.get("removed")).orElse(false)
                ));
            }

            return Optional.empty();
        }
    }

}
