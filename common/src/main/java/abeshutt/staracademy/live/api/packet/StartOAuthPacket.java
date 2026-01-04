package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import com.google.gson.JsonObject;

import java.util.Objects;

public class StartOAuthPacket extends Packet {

    public static final String MICROSOFT = "microsoft";

    private String service;
    private String authorizeUrl;

    public StartOAuthPacket() {

    }

    public StartOAuthPacket(String service, String authorizeUrl) {
        this.service = service;
        this.authorizeUrl = authorizeUrl;
    }

    public String getService() {
        return this.service;
    }

    public String getAuthorizeUrl() {
        return this.authorizeUrl;
    }

    public boolean is(String service) {
        return Objects.equals(this.service, service);
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("service", adapter.writeString(this.service));
        json.add("authorize_url", adapter.writeString(this.authorizeUrl));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.service = adapter.readString(json.get("service")).orElse(null);
        this.authorizeUrl = adapter.readString(json.get("authorize_url")).orElse(null);
    }

}
