package abeshutt.staracademy.config.sound;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.data.adapter.util.ServerSound;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class PlaySoundEvent implements ISerializable<NbtCompound, JsonObject> {

    private ServerSound sound;

    protected PlaySoundEvent() {

    }

    public PlaySoundEvent(ServerSound sound) {
        this.sound = sound;
    }

    public ServerSound getSound() {
        return this.sound;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.SERVER_SOUND.writeJson(this.sound).ifPresent(tag -> {
                json.add("sound", tag);
            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.sound = Adapters.SERVER_SOUND.readJson(json.get("sound")).orElse(null);
    }

    public static class Adapter extends TypeSupplierAdapter<PlaySoundEvent> {
        public static final Adapter INSTANCE = new Adapter();

        public Adapter() {
            super("type", true);
            this.register("dimension_enter", DimensionEnterPlaySoundEvent.class, DimensionEnterPlaySoundEvent::new);
        }
    }

}
