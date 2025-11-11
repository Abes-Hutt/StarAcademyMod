package abeshutt.staracademy.api.twitch;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.JsonObject;

import java.util.Optional;

public class TwitchStream implements IJsonSerializable<JsonObject> {

    private String login;
    private String name;
    private boolean live;
    private TwitchImage profilePicture;

    public TwitchStream() {

    }

    private TwitchStream(String login, String name, boolean live, TwitchImage profilePicture) {
        this.login = login;
        this.name = name;
        this.live = live;
        this.profilePicture = profilePicture;
    }

    public String getLogin() {
        return this.login;
    }

    public String getName() {
        return this.name;
    }

    public boolean isLive() {
        return this.live;
    }

    public TwitchImage getProfilePicture() {
        return this.profilePicture;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.UTF_8.writeJson(this.login).ifPresent(tag -> json.add("login", tag));
            Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
            Adapters.BOOLEAN.writeJson(this.live).ifPresent(tag -> json.add("live", tag));
            this.profilePicture.writeJson().ifPresent(tag -> json.add("profile_picture", tag));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        this.login = Adapters.UTF_8.readJson(json.get("login")).orElse(null);
        this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
        this.live = Adapters.BOOLEAN.readJson(json.get("live")).orElse(false);
        this.profilePicture.readJson(json.get("profile_picture"));
    }

}
