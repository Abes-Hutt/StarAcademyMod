package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class AttributeReference<T> {

    private final Object owner;
    private final int order;
    private AttributeModifier<T> modifier;
    private final AttributePath path;
    private boolean removed;

    public AttributeReference(Object owner, int order, AttributePath path) {
        this.owner = owner;
        this.order = order;
        this.path = path;
        this.removed = false;
    }

    public Object getOwner() {
        return this.owner;
    }

    public int getOrder() {
        return this.order;
    }

    public AttributePath getPath() {
        return this.path;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public AttributeModifier<T> get() {
        return this.modifier;
    }

    public AttributeReference<T> set(AttributeModifier<T> modifier) {
        this.modifier = modifier;
        return this;
    }

    public void dispose() {
        this.removed = true;
    }

    public static class Adapter<T> implements IAdapter<AttributeReference<T>, NbtCompound, JsonObject, TypeSupplierAdapter<AttributeModifier<T>>> {
        @Override
        public Optional<JsonObject> writeJson(AttributeReference<T> value, TypeSupplierAdapter<AttributeModifier<T>> context) {
            return Optional.of(new JsonObject()).map(json -> {
                return json;
            });
        }

        @Override
        public Optional<AttributeReference<T>> readJson(JsonObject json, TypeSupplierAdapter<AttributeModifier<T>> context) {
            return Optional.empty();
        }
    }

}
