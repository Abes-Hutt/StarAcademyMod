package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AcademyPackets {

    private static final Map<String, Supplier<? extends AcademyPacket>> ID_TO_CONSTRUCTOR = new HashMap<>();
    private static final Map<Class<?>, String> CLASS_TO_ID = new HashMap<>();

    public static <T extends AcademyPacket> void register(String id, Class<T> type, Supplier<T> constructor) {
        ID_TO_CONSTRUCTOR.put(id, constructor);
        CLASS_TO_ID.put(type, id);
    }

    public static void register() {
        register("hello", HelloPacket.class, HelloPacket::new);
        register("challenge_auth", ChallengeAuthPacket.class, ChallengeAuthPacket::new);
        register("complete_auth", CompleteAuthPacket.class, CompleteAuthPacket::new);
        register("check_codex", CheckCodexPacket.class, CheckCodexPacket::new);
        register("update_codex", UpdateCodexPacket.class, UpdateCodexPacket::new);
        register("update_outfit_registry", UpdateOutfitRegistryPacket.class, UpdateOutfitRegistryPacket::new);
        register("update_outfit_entry", UpdateOutfitEntryPacket.class, UpdateOutfitEntryPacket::new);
        register("update_outfit_tracking", UpdateOutfitTrackingPacket.class, UpdateOutfitTrackingPacket::new);
    }

    public static Optional<JsonObject> encode(AcademyPacket packet) {
        String type = CLASS_TO_ID.get(packet.getClass());

        if(type == null) {
            StarAcademyMod.LOGGER.error("Attempted to encode unregistered packet {}{}.",
                    packet.getClass().getSimpleName(),
                    packet.writeJson().orElseGet(JsonObject::new));

            return Optional.empty();
        }

        return packet.writeJson().map(object -> {
            object.addProperty("type", type);
            return object;
        });
    }

    public static Optional<AcademyPacket> decode(JsonObject json) {
        String type = json.has("type") ? json.get("type").getAsString() : null;

        if(type == null || !ID_TO_CONSTRUCTOR.containsKey(type)) {
            StarAcademyMod.LOGGER.error("Attempted to decode unknown packet {}.", json);
            return Optional.empty();
        }

        AcademyPacket packet = ID_TO_CONSTRUCTOR.get(type).get();

        try {
            packet.readJson(json);
        } catch(Exception e) {
            StarAcademyMod.LOGGER.error("Failed to deserialize packet {}.", json, e);
        }

        return Optional.of(packet);
    }

}
