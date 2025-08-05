package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.Base64;
import java.util.Optional;

public class UpdateCodexPacket extends AcademyPacket {

    private byte[] assets;
    private byte[] data;

    public UpdateCodexPacket() {

    }

    public UpdateCodexPacket(byte[] assets, byte[] data) {
        this.assets = assets;
        this.data = data;
    }

    public byte[] getAssets() {
        return this.assets;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            if(this.assets != null) {
                Adapters.UTF_8.writeJson(Base64.getEncoder().encodeToString(this.assets)).ifPresent(tag -> {
                    object.add("assets", tag);
                });
            }

            if(this.data != null) {
                Adapters.UTF_8.writeJson(Base64.getEncoder().encodeToString(this.data)).ifPresent(tag -> {
                    object.add("data", tag);
                });
            }

            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.assets = Adapters.UTF_8.readJson(json.get("assets")).map(s -> Base64.getDecoder().decode(s)).orElse(null);
        this.data = Adapters.UTF_8.readJson(json.get("data")).map(s -> Base64.getDecoder().decode(s)).orElse(null);
    }

}
