package abeshutt.staracademy.card;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CardAlbumEntry implements ISerializable<NbtCompound, JsonObject> {

    private Identifier model;
    private int color;

    public CardAlbumEntry() {

    }

    public CardAlbumEntry(Identifier model, int color) {
        this.model = model;
        this.color = color;
    }

    public Identifier getModel() {
        return this.model;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.IDENTIFIER.writeJson(this.model).ifPresent(tag -> json.add("model", tag));
            Adapters.INT.writeJson(this.color).ifPresent(tag -> json.add("color", tag));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.model = Adapters.IDENTIFIER.readJson(json.get("model")).orElseThrow();
        this.color = Adapters.INT.readJson(json.get("color")).orElseThrow();
    }

}
