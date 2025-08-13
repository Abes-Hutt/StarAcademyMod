package abeshutt.staracademy.card;

import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.any;

public class CardModifierEntry implements ISerializable<NbtCompound, JsonObject> {

    private AttributePath<?> path;
    private int order;
    private Attribute<?> attribute;

    public CardModifierEntry() {

    }

    public CardModifierEntry(AttributePath<?> path, int order, Attribute<?> attribute) {
        this.path = path;
        this.order = order;
        this.attribute = attribute;
    }

    public AttributePath<?> getPath() {
        return this.path;
    }

    public int getOrder() {
        return this.order;
    }

    public Attribute<?> getAttribute() {
        return this.attribute;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.ATTRIBUTE_PATH.writeJson(this.path).ifPresent(tag -> {
                json.add("path", tag);
            });

            Adapters.INT.writeJson(this.order).ifPresent(tag -> {
                json.add("order", tag);
            });

            Adapters.ATTRIBUTE.writeJson(this.attribute, any()).ifPresent(tag -> {
                json.add("attribute", tag);
            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.path = Adapters.ATTRIBUTE_PATH.readJson(json.get("path")).orElseThrow();
        this.order = Adapters.INT.readJson(json.get("order")).orElseThrow();
        this.attribute = Adapters.ATTRIBUTE.readJson(json.get("attribute"), any()).orElseThrow();
    }

}
