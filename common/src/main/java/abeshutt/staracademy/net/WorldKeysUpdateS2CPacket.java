package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorldKeysUpdateS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<WorldKeysUpdateS2CPacket> ID = new Id<>(StarAcademyMod.id("world_keys_update_s2c"));

    private final Set<Identifier> added;
    private final Set<Identifier> removed;

    public WorldKeysUpdateS2CPacket() {
        this.added = new LinkedHashSet<>();
        this.removed = new LinkedHashSet<>();
    }

    public WorldKeysUpdateS2CPacket(Set<Identifier> added, Set<Identifier> removed) {
        this.added = added;
        this.removed = removed;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        for(Identifier id : this.added) {
            listener.getWorldKeys().add(RegistryKey.of(RegistryKeys.WORLD, id));
        }

        listener.getWorldKeys().removeIf(key -> this.removed.contains(key.getValue()));
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.added.size(), buffer);

        for(Identifier id : this.added) {
           Adapters.IDENTIFIER.writeBits(id, buffer);
        }

        Adapters.INT_SEGMENTED_3.writeBits(this.removed.size(), buffer);

        for(Identifier id : this.removed) {
            Adapters.IDENTIFIER.writeBits(id, buffer);
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        int size;
        size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            this.added.add(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
        }

        size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            this.removed.add(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
        }
    }

}
