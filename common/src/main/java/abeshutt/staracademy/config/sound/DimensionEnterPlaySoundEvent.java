package abeshutt.staracademy.config.sound;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.util.ServerSound;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class DimensionEnterPlaySoundEvent extends PlaySoundEvent {

    private Identifier dimension;

    protected DimensionEnterPlaySoundEvent() {

    }

    public DimensionEnterPlaySoundEvent(ServerSound sound, Identifier dimension) {
        super(sound);
        this.dimension = dimension;
    }

    public Identifier getDimension() {
        return this.dimension;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.IDENTIFIER.writeJson(this.dimension).ifPresent(tag -> json.add("dimension", tag));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.dimension = Adapters.IDENTIFIER.readJson(json.get("dimension")).orElse(null);
    }

}
