package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.data.CardGradingData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateCardGradingS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateCardGradingS2CPacket> ID = new Id<>(StarAcademyMod.id("update_card_grading_s2c"));

    private Map<UUID, CardGradingData.Entry> entries;

    public UpdateCardGradingS2CPacket() {

    }

    public UpdateCardGradingS2CPacket(Map<UUID, CardGradingData.Entry> entries) {
        this.entries = entries;
    }

    public UpdateCardGradingS2CPacket(UUID uuid, CardGradingData.Entry entry) {
        this.entries = new HashMap<>();
        this.entries.put(uuid, entry);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        Map<UUID, CardGradingData.Entry> entries = CardGradingData.CLIENT.getEntries();

        if(this.entries == null) {
            entries.clear();
        } else {
            this.entries.forEach((uuid, entry) -> {
                if(entry == null) {
                    entries.remove(uuid);
                } else {
                    entries.put(uuid, entry);
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
                Adapters.BOOLEAN.writeBits(entry == null, buffer);

                if(entry != null) {
                    entry.writeBits(buffer);
                }
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

            if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                this.entries.put(uuid, null);
            } else {
                CardGradingData.Entry entry = new CardGradingData.Entry();
                entry.readBits(buffer);

                for(int i = 0; i < size; i++) {
                    this.entries.put(uuid, entry);
                }
            }
        }
    }

}
