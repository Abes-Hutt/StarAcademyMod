package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import com.google.gson.JsonObject;

public class UpdateCodexPacket extends Packet {

    private byte[] zip;

    public UpdateCodexPacket() {

    }

    public UpdateCodexPacket(byte[] zip) {
        this.zip = zip;
    }

    public byte[] getZip() {
        return this.zip;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("zip", adapter.writeBase64(this.zip));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.zip = adapter.readBase64(json.get("zip")).orElse(null);
    }

}
