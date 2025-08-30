package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.Optional;

public class CheckCodexPacket extends AcademyPacket {

    private Integer assetsChecksum;
    private Integer dataChecksum;

    public CheckCodexPacket() {

    }

    public CheckCodexPacket(Integer assetsChecksum, Integer dataChecksum) {
        this.assetsChecksum = assetsChecksum;
        this.dataChecksum = dataChecksum;
    }

    public Integer getAssetsChecksum() {
        return this.assetsChecksum;
    }

    public Integer getDataChecksum() {
        return this.dataChecksum;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            Adapters.INT.writeJson(this.assetsChecksum).ifPresent(tag -> object.add("assets_checksum", tag));
            Adapters.INT.writeJson(this.dataChecksum).ifPresent(tag -> object.add("data_checksum", tag));
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.assetsChecksum = Adapters.INT.readJson(json.get("assets_checksum")).orElse(null);
        this.dataChecksum = Adapters.INT.readJson(json.get("data_checksum")).orElse(null);
    }

}
