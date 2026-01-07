package abeshutt.staracademy.live;

import abeshutt.staracademy.live.api.dto.CosmeticData;
import abeshutt.staracademy.live.api.packet.UpdateCosmeticsPacket;
import abeshutt.staracademy.live.api.packet.UpdatePlayerTrackingPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.*;

public class CosmeticsManager {

    private final AcademyClient client;
    private final Map<UUID, CosmeticData> entries;
    private final Set<UUID> tracked;

    public CosmeticsManager(AcademyClient client) {
        this.client = client;
        this.entries = new HashMap<>();
        this.tracked = new HashSet<>();
    }

    public Map<UUID, CosmeticData> getEntries() {
        return this.entries;
    }

    public Set<UUID> getTracked() {
        return this.tracked;
    }

    public Map<String, String> getEquipped(UUID uuid) {
        if(this.entries.containsKey(uuid)) {
            return this.entries.get(uuid).getSlots();
        }

        return new HashMap<>();
    }

    public void receive(boolean delta, Map<UUID, CosmeticData> entries) {
        if (delta) {
            entries.forEach((uuid, entry) -> {
                if (entry.isDelta()) {
                    CosmeticData existing = this.entries.get(uuid);

                    if (existing == null) {
                        this.entries.put(uuid, entry);
                    } else {
                        existing.getSlots().forEach((slot, cosmetic) -> {
                            existing.getSlots().put(slot, cosmetic);
                            existing.getUnlocked().addAll(entry.getUnlocked());
                        });
                    }
                } else {
                    this.entries.put(uuid, entry);
                }
            });
        } else {
            this.entries.clear();
            this.entries.putAll(entries);
        }
    }

    public void tick(AcademyClient client) {
        Set<UUID> added = new HashSet<>();
        Set<UUID> removed = new HashSet<>(this.tracked);

        if (client.getMinecraft().world == null) {
            removed.addAll(this.tracked);
            this.tracked.clear();
        } else {
            for(AbstractClientPlayerEntity player : client.getMinecraft().world.getPlayers()) {
                if(!this.tracked.contains(player.getUuid())) {
                    added.add(player.getUuid());
                }

                removed.remove(player.getUuid());
            }
        }

        if(!added.isEmpty()) {
            client.send(new UpdatePlayerTrackingPacket(added, true));
        }

        if(!removed.isEmpty()) {
            client.send(new UpdatePlayerTrackingPacket(added, false));
        }
    }

    public boolean isUnlocked(UUID uuid, String cosmetic) {
        CosmeticData entry = this.entries.get(uuid);

        if(entry != null) {
            return entry.getUnlocked().contains(cosmetic);
        }

        return false;
    }

    public void setEquipped(String slot, String cosmetic) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null || slot == null) return;
        CosmeticData entry = this.entries.computeIfAbsent(player.getUuid(), uuid -> new CosmeticData());

        Map<String, String> update = new HashMap<>();
        update.put(slot, cosmetic);

        if (!Objects.equals(entry.getSlots().get(slot), cosmetic)) {
            this.client.send(new UpdateCosmeticsPacket(Map.of(
                    player.getUuid(), new CosmeticData(update,
                            new HashSet<>(), true)), true));
            entry.getSlots().put(slot, cosmetic);
        }
    }

}
