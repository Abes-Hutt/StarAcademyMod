package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.Optional;
import java.util.UUID;

public class HelloPacket extends AcademyPacket {

    private UUID uuid;
    private String name;

    public HelloPacket() {

    }

    public HelloPacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            Adapters.UUID.writeJson(this.uuid).ifPresent(tag -> object.add("uuid", tag));
            Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> object.add("name", tag));
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.uuid = Adapters.UUID.readJson(json.get("uuid")).orElseThrow();
        this.name = Adapters.UTF_8.readJson(json.get("name")).orElseThrow();
    }

}
