package abeshutt.staracademy.card;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class CardDisplayEntry implements ISerializable<NbtCompound, JsonObject> {

    private AttributePath<?> path;
    private String name;
    private int color;
    private AttributeStyle<?> style;

    public CardDisplayEntry() {
        this.path = AttributePath.empty();
        this.name = "unknown";
        this.color = 0xFFFFFF;
        this.style = null;
    }

    public CardDisplayEntry(AttributePath<?> path, String name, int color, AttributeStyle<?> style) {
        this.path = path;
        this.name = name;
        this.color = color;
        this.style = style;
    }

    public AttributePath<?> getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public <T> AttributeStyle<T> getStyle() {
        return (AttributeStyle<T>)this.style;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.ATTRIBUTE_PATH.writeJson(this.path).ifPresent(tag -> {
                json.add("path", tag);
            });

            Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> {
                json.add("name", tag);
            });

            Adapters.INT.writeJson(this.color).ifPresent(tag -> {
                json.add("color", tag);
            });

            AttributeStyle.ADAPTER.writeJson(this.style).ifPresent(tag -> {
                json.add("style", tag);
            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.path = Adapters.ATTRIBUTE_PATH.readJson(json.get("path")).orElseThrow();
        this.name = Adapters.UTF_8.readJson(json.get("name")).orElseThrow();
        this.color = Adapters.INT.readJson(json.get("color")).orElseThrow();
        this.style = AttributeStyle.ADAPTER.readJson(json.get("style")).orElseThrow();
    }

}
