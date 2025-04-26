package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.item.SafariTicketEntry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;

public class UpdateSafariConfigS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateSafariConfigS2CPacket> ID = new Id<>(StarAcademyMod.id("update_safari_config_s2c"));

    private Map<String, SafariTicketEntry> entries;

    public UpdateSafariConfigS2CPacket() {

    }

    public UpdateSafariConfigS2CPacket(Map<String, SafariTicketEntry> profiles) {
        this.entries = profiles;
    }

    public UpdateSafariConfigS2CPacket(String id, SafariTicketEntry entry) {
        this.entries = new HashMap<>();
        this.entries.put(id, entry);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        Map<String, SafariTicketEntry> entries = SafariConfig.CLIENT.getTickets();
        entries.clear();
        entries.putAll(this.entries);
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.entries.size(), buffer);

        this.entries.forEach((id, entry) -> {
            Adapters.UTF_8.writeBits(id, buffer);
            entry.writeBits(buffer);
        });
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.entries = new HashMap<>();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            String id = Adapters.UTF_8.readBits(buffer).orElseThrow();
            SafariTicketEntry entry = new SafariTicketEntry();
            entry.readBits(buffer);
            this.entries.put(id, entry);
        }

    }

}