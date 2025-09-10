package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.data.NickData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateNickS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateNickS2CPacket> ID = new Id<>(StarAcademyMod.id("update_nick_s2c"));

    private final Map<UUID, String> entries;

    public UpdateNickS2CPacket() {
        this.entries = new LinkedHashMap<>();
    }

    public UpdateNickS2CPacket(Map<UUID, String> entries) {
        this.entries = entries;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        if(this.entries == null) {
            NickData.CLIENT.getEntries().clear();
        } else {
            NickData.CLIENT.getEntries().putAll(this.entries);
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.entries != null, buffer);

        if(this.entries != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.entries.size(), buffer);

            this.entries.forEach((uuid, entry) -> {
                Adapters.UUID.writeBits(uuid, buffer);
                Adapters.UTF_8.asNullable().writeBits(entry, buffer);
            });
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
                String nick = Adapters.UTF_8.asNullable().readBits(buffer).orElseThrow();
                this.entries.put(uuid, nick);
            }
        }
    }

}

