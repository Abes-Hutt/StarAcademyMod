package abeshutt.staracademy.live.api.adapter;

import com.google.gson.JsonObject;

public interface JsonSerializable {

    void writeJson(JsonAdapter adapter, JsonObject json);

    void readJson(JsonAdapter adapter, JsonObject json);

}
