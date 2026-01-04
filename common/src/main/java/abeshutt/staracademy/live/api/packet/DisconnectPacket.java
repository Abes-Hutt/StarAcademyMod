package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.DisconnectReason;
import com.google.gson.JsonObject;

public class DisconnectPacket extends Packet {

    private DisconnectReason reason;

    public  DisconnectPacket() {

    }

    public DisconnectPacket(DisconnectReason reason) {
        this.reason = reason;
    }

    public DisconnectReason getReason() {
        return this.reason;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("reason", adapter.writeDisconnectReason(this.reason));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.reason = adapter.readDisconnectReason(json.get("reason")).orElse(null);
    }

}
