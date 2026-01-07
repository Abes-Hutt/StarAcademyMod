package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;
import java.util.UUID;

public class UpdatePlayerTrackingPacket extends Packet {

    private Set<UUID> players;
    private boolean add;

    public UpdatePlayerTrackingPacket() {

    }

    public UpdatePlayerTrackingPacket(Set<UUID> players, boolean add) {
        this.players = players;
        this.add = add;
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public boolean isAdd() {
        return this.add;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        JsonArray players = new JsonArray();

        for (UUID player : this.players) {
            players.add(adapter.writeUuid(player));
        }

        json.add("players", players);
        json.add("add", adapter.writeBoolean(this.add));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.players.clear();

        if (json.get("players") instanceof JsonArray players) {
            for (JsonElement player : players) {
                adapter.readUuid(player, true).ifPresent(this.players::add);
            }
        }

        this.add = adapter.readBoolean(json.get("add")).orElse(false);
    }

}
