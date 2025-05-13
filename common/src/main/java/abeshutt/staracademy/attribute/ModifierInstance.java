package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.attribute.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtElement;
import org.w3c.dom.Attr;

import java.util.Optional;

public class ModifierInstance<T> {

    private final int order;
    private Attribute<T> modifier;
    private final AttributePath<T> path;
    private boolean removed;

    protected ModifierInstance(int order, Attribute<T> modifier, AttributePath<T> path, boolean removed) {
        this.order = order;
        this.modifier = modifier;
        this.path = path;
        this.removed = removed;
    }

    public static <T> ModifierInstance<T> empty(int order, AttributePath<T> path) {
        return new ModifierInstance<>(order, null, path, false);
    }

    public static <T> ModifierInstance<T> empty(int order) {
        return new ModifierInstance<>(order, null, AttributePath.empty(), false);
    }

    public static <T> ModifierInstance<T> empty() {
        return new ModifierInstance<>(0, null, AttributePath.empty(), false);
    }

    public static <T> ModifierInstance<T> of(int order, Attribute<T> modifier, AttributePath<T> path) {
        return new ModifierInstance<>(order, modifier, path, false);
    }

    public static <T> ModifierInstance<T> of(int order, Attribute<T> modifier) {
        return new ModifierInstance<>(order, modifier, AttributePath.empty(), false);
    }

    public static <T> ModifierInstance<T> of(Attribute<T> modifier) {
        return new ModifierInstance<>(0, modifier, AttributePath.empty(), false);
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

    public ModifierInstance<T> set(Attribute<T> modifier) {
        this.modifier = modifier;
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
    }

    public static class Adapter<T> implements ISimpleAdapter<ModifierInstance<T>, NbtElement, JsonElement> {
        private final IAdapter<Attribute<T>, NbtElement, JsonElement, ?> modifierAdapter;

        protected Adapter(IAdapter<Attribute<T>, ?, ?, ?> modifierAdapter) {
            this.modifierAdapter = (IAdapter)modifierAdapter;
        }

        @Override
        public Optional<JsonElement> writeJson(ModifierInstance<T> value) {
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
        public Optional<ModifierInstance<T>> readJson(JsonElement json) {
            if(json instanceof JsonObject object) {
                return Optional.of(new ModifierInstance<T>(
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
