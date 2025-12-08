package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.proxy.ProxyItemRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class ItemRegistryS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<ItemRegistryS2CPacket> ID = new Id<>(StarAcademyMod.id("item_registry_s2c"));

    private final Set<Identifier> items;

    public ItemRegistryS2CPacket() {
        this.items = new HashSet<>();
    }

    public ItemRegistryS2CPacket(Set<Identifier> items) {
        this.items = items;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        ProxyItemRegistry.setItemRegistry(listener, this.items);
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_7.writeBits(this.items.size(), buffer);

        for (Identifier id : this.items) {
            Adapters.IDENTIFIER.writeBits(id, buffer);
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();

        for (int i = 0; i < size; i++) {
            this.items.add(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
        }
    }

}
