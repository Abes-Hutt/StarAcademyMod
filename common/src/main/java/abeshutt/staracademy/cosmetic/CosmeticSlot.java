package abeshutt.staracademy.cosmetic;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CosmeticSlot implements IJsonSerializable<JsonElement> {

    private String id;
    private String name;
    private String description;
    private Identifier icon;

    public CosmeticSlot() {

    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public MutableText getNameText() {
        return Text.translatable(this.name);
    }

    public String getDescription() {
        return this.description;
    }

    public MutableText getDescriptionText() {
        return Text.translatable(this.description);
    }

    public Identifier getIcon() {
        return this.icon;
    }

    @Override
    public Optional<JsonElement> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.UTF_8.writeJson(this.id).ifPresent(value -> json.add("id", value));
            Adapters.UTF_8.writeJson(this.name).ifPresent(value -> json.add("name", value));
            Adapters.UTF_8.writeJson(this.description).ifPresent(value -> json.add("description", value));
            Adapters.IDENTIFIER.writeJson(this.icon).ifPresent(value -> json.add("icon", value));
            return json;
        });
    }

    @Override
    public void readJson(JsonElement json) {
        if (json instanceof JsonObject object) {
            this.id = Adapters.UTF_8.readJson(object.get("id")).orElse(null);
            this.name = Adapters.UTF_8.readJson(object.get("name")).orElse(null);
            this.description = Adapters.UTF_8.readJson(object.get("description")).orElse(null);
            this.icon = Adapters.IDENTIFIER.readJson(object.get("icon")).orElse(null);
        } else {
            this.id = null;
            this.name = null;
            this.description = null;
            this.icon = null;
        }
    }

}
