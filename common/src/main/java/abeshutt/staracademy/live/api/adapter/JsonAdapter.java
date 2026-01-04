package abeshutt.staracademy.live.api.adapter;

import abeshutt.staracademy.live.api.dto.DisconnectReason;
import abeshutt.staracademy.live.api.dto.Hash;
import abeshutt.staracademy.live.api.dto.LivestreamDisplay;
import abeshutt.staracademy.live.api.registry.TypeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class JsonAdapter {

    private final int protocol;

    public JsonAdapter(int protocol) {
        this.protocol = protocol;
    }

    public JsonPrimitive writeBoolean(boolean value) {
        return new JsonPrimitive(value);
    }

    public Optional<Boolean> readBoolean(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            return Optional.of(primitive.getAsBoolean());
        }

        return Optional.empty();
    }

    public JsonPrimitive writeInt(int value) {
        return new JsonPrimitive(value);
    }

    public Optional<Integer> readInt(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isNumber()) {
            return Optional.of(primitive.getAsInt());
        }

        return Optional.empty();
    }

    public JsonPrimitive writeLong(long value) {
        return new JsonPrimitive(value);
    }

    public Optional<Long> readLong(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isNumber()) {
            return Optional.of(primitive.getAsLong());
        }

        return Optional.empty();
    }

    public JsonElement writeInstant(Instant value) {
        return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.toString());
    }

    public Optional<Instant> readInstant(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {

            try {
                return Optional.of(Instant.parse(primitive.getAsString()));
            } catch (Exception ignored) {

            }
        }

        return Optional.empty();
    }

    public JsonElement writeUuid(UUID uuid) {
        return uuid == null ? JsonNull.INSTANCE : new JsonPrimitive(uuid.toString());
    }

    public Optional<UUID> readUuid(JsonElement json, boolean dashed) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            String raw = primitive.getAsString();

            if (dashed) {
                return Optional.of(UUID.fromString(raw));
            } else if (raw.length() == 32) {
                return Optional.of(UUID.fromString(raw.substring(0, 8) + "-" +
                        raw.substring(8, 12) + "-" + raw.substring(12, 16) + "-" +
                        raw.substring(16, 20) + "-" + raw.substring(20)));
            }
        }

        return Optional.empty();
    }

    public JsonElement writeString(String string) {
        return string == null ? JsonNull.INSTANCE : new JsonPrimitive(string);
    }

    public Optional<String> readString(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            return Optional.of(primitive.getAsString());
        }

        return Optional.empty();
    }

    public JsonElement writeBase64(byte[] bytes) {
        if (bytes == null) {
            return JsonNull.INSTANCE;
        }

        return this.writeString(Base64.getEncoder().encodeToString(bytes));
    }

    public Optional<byte[]> readBase64(JsonElement json) {
        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            return Optional.of(Base64.getDecoder().decode(primitive.getAsString()));
        }

        return Optional.empty();
    }

    public JsonElement writeSerializable(JsonSerializable object) {
        if (object == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject json = new JsonObject();
        object.writeJson(this, json);
        return json;
    }

    public <T extends JsonSerializable> Optional<T> readSerializable(JsonElement json, Supplier<T> factory) {
        if (json instanceof JsonObject obj) {
            T object = factory.get();

            if (object != null) {
                object.readJson(this, obj);
            }

            return Optional.ofNullable(object);
        }

        return Optional.empty();
    }

    public JsonElement writeHash(Hash hash) {
        if (hash == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject object = new JsonObject();
        object.add("algorithm", this.writeString(hash.getAlgorithm().getId()));
        object.add("value", this.writeBase64(hash.getValue()));
        return object;
    }

    public Optional<Hash> readHash(JsonElement json) {
        if (json instanceof JsonObject object) {
            Hash.Algorithm algorithm = this.readString(object.get("algorithm"))
                    .flatMap(Hash.Algorithm::fromId).orElse(null);
            byte[] value = this.readBase64(object.get("value")).orElse(null);

            if (algorithm == null || value == null) {
                return Optional.empty();
            }

            return Optional.of(Hash.of(algorithm, value));
        }

        return Optional.empty();
    }

    public JsonElement writeLivestreamDisplay(LivestreamDisplay display) {
        if (display == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject object = new JsonObject();
        object.add("page_url", this.writeString(display.getPageUrl()));
        object.add("profile_picture_url", this.writeString(display.getProfilePictureUrl()));
        object.add("display_name", this.writeString(display.getDisplayName()));
        object.add("display_hint", this.writeString(display.getDisplayHint()));
        object.add("display_status_color", this.writeInt(display.getDisplayStatusColor()));
        return object;
    }

    public Optional<LivestreamDisplay> readLivestreamDisplay(JsonElement json) {
        if (json instanceof JsonObject object) {
            String pageUrl = this.readString(object.get("page_url")).orElse(null);
            String profilePictureUrl = this.readString(object.get("profile_picture_url")).orElse(null);
            String displayName = this.readString(object.get("display_name")).orElse(null);
            String displayHint = this.readString(object.get("display_hint")).orElse(null);
            int displayStatusColor = this.readInt(object.get("display_status_color")).orElse(0);
            return Optional.of(new LivestreamDisplay(pageUrl, profilePictureUrl, displayName, displayHint, displayStatusColor));
        }

        return Optional.empty();
    }

    public JsonElement writeDisconnectReason(DisconnectReason reason) {
        if (reason == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject object = new JsonObject();
        object.add("code", this.writeInt(reason.getCode()));
        object.add("message", this.writeString(reason.getMessage()));
        return object;
    }

    public Optional<DisconnectReason> readDisconnectReason(JsonElement json) {
        if (json instanceof JsonObject object) {
            int code = this.readInt(object.get("code")).orElse(0);
            String message = this.readString(object.get("message")).orElse(null);
            return Optional.of(new DisconnectReason(code, message));
        }

        return Optional.empty();
    }

    public <T extends JsonSerializable> JsonElement writeTypeObject(TypeRegistry<T> registry, T value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject object = new JsonObject();
        value.writeJson(this, object);

        if (object.has(registry.getKey())) {
            throw new IllegalStateException("Typed object " + object + " already has key '" + registry.getKey() + "'");
        }

        String id = registry.getId(value).orElseThrow(() -> {
            return new IllegalStateException("Type " + value.getClass().getSimpleName() + " doesn't have an id");
        });

        object.addProperty(registry.getKey(), id);
        return object;
    }

    public <T extends JsonSerializable> Optional<T> readTypeObject(TypeRegistry<T> registry, JsonElement json) {
        if (json instanceof JsonObject object && object.get(registry.getKey()) instanceof JsonPrimitive id && id.isString()) {
            T instance = registry.construct(id.getAsString()).orElseThrow(() -> {
                return new IllegalStateException("Id " + id + " doesn't have a constructor");
            });

            object.remove(registry.getKey());
            instance.readJson(this, object);
            return Optional.of(instance);
        }

        return Optional.empty();
    }

}
