package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.Optional;

public class CompleteAuthPacket extends AcademyPacket {

    private boolean success;
    private String reason;

    public CompleteAuthPacket() {

    }

    public CompleteAuthPacket(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getReason() {
        return this.reason;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            Adapters.BOOLEAN.writeJson(this.success).ifPresent(tag -> object.add("success", tag));
            Adapters.UTF_8.writeJson(this.reason).ifPresent(tag -> object.add("reason", tag));
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.success = Adapters.BOOLEAN.readJson(json.get("success")).orElseThrow();
        this.reason = Adapters.UTF_8.readJson(json.get("reason")).orElse(null);
    }

}
