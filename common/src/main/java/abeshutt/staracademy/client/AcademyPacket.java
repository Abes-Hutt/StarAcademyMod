package abeshutt.staracademy.client;

import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.JsonObject;

import java.util.Optional;

public class AcademyPacket implements IJsonSerializable<JsonObject> {

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject());
    }

    @Override
    public void readJson(JsonObject json) {

    }

}
