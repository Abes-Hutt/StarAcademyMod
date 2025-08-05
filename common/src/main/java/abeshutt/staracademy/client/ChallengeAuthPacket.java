package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.Optional;

public class ChallengeAuthPacket extends AcademyPacket {

    private String serverId;

    public ChallengeAuthPacket() {

    }

    public ChallengeAuthPacket(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return this.serverId;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            Adapters.UTF_8.writeJson(this.serverId).ifPresent(tag -> object.add("server_id", tag));
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.serverId = Adapters.UTF_8.readJson(json.get("server_id")).orElseThrow();
    }

}
