package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.data.WardrobeData;
import abeshutt.staracademy.world.data.WardrobeData.Entry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateOutfitS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateOutfitS2CPacket> ID = new Id<>(StarAcademyMod.id("update_outfit_s2c"));

    private Map<UUID, Entry> entries;

    public UpdateOutfitS2CPacket() {

    }

    public UpdateOutfitS2CPacket(Map<UUID, Entry> entries) {
        this.entries = entries;
    }

    public UpdateOutfitS2CPacket(UUID uuid, Entry profile) {
        this.entries = new HashMap<>();
        this.entries.put(uuid, profile);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        Map<UUID, Entry> entries = WardrobeData.CLIENT.getEntries();

        if(this.entries == null) {
            entries.clear();
        } else {
            this.entries.forEach((uuid, profile) -> {
                if(profile == null) {
                    entries.remove(uuid);
                } else {
                    entries.put(uuid, profile);
                }
            });
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.entries == null, buffer);

        if(this.entries != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.entries.size(), buffer);

            this.entries.forEach((uuid, entry) -> {
                Adapters.UUID.writeBits(uuid, buffer);
                entry.writeBits(buffer);
            });
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.entries = null;
        } else {
            this.entries = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
            Entry entry = new Entry();
            entry.readBits(buffer);

            for(int i = 0; i < size; i++) {
                this.entries.put(uuid, entry);
            }
        }
    }

}
