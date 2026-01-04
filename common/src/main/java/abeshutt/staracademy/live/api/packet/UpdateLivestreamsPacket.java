package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.LivestreamDisplay;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateLivestreamsPacket extends Packet {

    private final List<LivestreamDisplay> livestreams;

    public UpdateLivestreamsPacket() {
        this.livestreams = new ArrayList<>();
    }

    public UpdateLivestreamsPacket(List<LivestreamDisplay> livestreams) {
        this.livestreams = livestreams;
    }

    public List<LivestreamDisplay> getLivestreams() {
        return this.livestreams;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        JsonArray array = new JsonArray();

        for (LivestreamDisplay stream : this.livestreams) {
            array.add(adapter.writeLivestreamDisplay(stream));
        }

        json.add("livestreams", array);
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.livestreams.clear();

        if (json.get("livestreams") instanceof JsonArray array) {
            for (JsonElement child : array) {
                adapter.readLivestreamDisplay(child).ifPresent(this.livestreams::add);
            }
        }
    }

}
