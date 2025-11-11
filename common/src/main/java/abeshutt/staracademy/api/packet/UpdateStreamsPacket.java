package abeshutt.staracademy.api.packet;

import abeshutt.staracademy.api.twitch.TwitchStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateStreamsPacket extends AcademyPacket {

    private final List<TwitchStream> streams;

    public UpdateStreamsPacket() {
        this.streams = new ArrayList<>();
    }

    public UpdateStreamsPacket(List<TwitchStream> streams) {
        this.streams = streams;
    }

    public List<TwitchStream> getStreams() {
        return this.streams;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            JsonArray streams = new JsonArray();

            for (TwitchStream stream : this.streams) {
                stream.writeJson().ifPresent(streams::add);
            }

            json.add("streams", streams);
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.streams.clear();

        if (json.get("streams") instanceof JsonArray streams) {
            for (JsonElement stream : streams) {
                TwitchStream value = new TwitchStream();
                value.readJson(stream.getAsJsonObject());
                this.streams.add(value);
            }
        }
    }

}
