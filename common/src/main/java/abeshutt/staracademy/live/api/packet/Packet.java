package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.adapter.JsonSerializable;
import com.google.gson.JsonObject;

public abstract class Packet implements JsonSerializable {

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {

    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {

    }

}
