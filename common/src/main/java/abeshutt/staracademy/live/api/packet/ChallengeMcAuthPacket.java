package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import com.google.gson.JsonObject;

public class ChallengeMcAuthPacket extends Packet {

    private String serverId;

    public ChallengeMcAuthPacket() {

    }

    public ChallengeMcAuthPacket(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return this.serverId;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("server_id", adapter.writeString(this.serverId));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.serverId = adapter.readString(json.get("server_id")).orElse(null);
    }

}
