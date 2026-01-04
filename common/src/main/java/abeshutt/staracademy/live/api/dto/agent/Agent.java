package abeshutt.staracademy.live.api.dto.agent;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.adapter.JsonSerializable;
import com.google.gson.JsonObject;

public abstract class Agent implements JsonSerializable {

    private String version;

    public Agent() {

    }

    public Agent(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        json.add("version", adapter.writeString(this.version));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        this.version = adapter.readString(json.get("version")).orElse(null);
    }

}
