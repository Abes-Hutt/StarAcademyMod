package abeshutt.staracademy.live.api.dto.agent;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.Hash;
import com.google.gson.JsonObject;

import java.util.UUID;

public class MinecraftGameAgent extends Agent {

    private UUID uuid;
    private String username;
    private Hash codexHash;

    public MinecraftGameAgent() {

    }

    public MinecraftGameAgent(String version, UUID uuid, String username, Hash codexHash) {
        super(version);
        this.uuid = uuid;
        this.username = username;
        this.codexHash = codexHash;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public Hash getCodexHash() {
        return this.codexHash;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("uuid", adapter.writeUuid(this.uuid));
        json.add("username", adapter.writeString(this.username));
        json.add("codex_hash", adapter.writeHash(this.codexHash));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.uuid = adapter.readUuid(json.get("uuid"), true).orElse(null);
        this.username = adapter.readString(json.get("username")).orElse(null);
        this.codexHash = adapter.readHash(json.get("codex_hash")).orElse(null);
    }

}
