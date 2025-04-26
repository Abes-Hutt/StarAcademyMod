package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.WardrobeData;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class UpdateOutfitC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<UpdateOutfitC2SPacket> ID = new Id<>(StarAcademyMod.id("update_outfit_c2s"));

    private final Map<String, Boolean> equipped;

    public UpdateOutfitC2SPacket() {
        this.equipped = new HashMap<>();
    }

    public UpdateOutfitC2SPacket(Map<String, Boolean> equipped) {
        this.equipped = equipped;
    }

    public UpdateOutfitC2SPacket(String id, boolean equipped) {
        this.equipped = new HashMap<>();
        this.equipped.put(id, equipped);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        ServerPlayerEntity player = listener.getPlayer();
        WardrobeData data = ModWorldData.WARDROBE.getGlobal(player.server);

        this.equipped.forEach((id, equipped) -> {
            data.setEquipped(player.getUuid(), id, equipped);
        });
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.equipped.size(), buffer);

        this.equipped.forEach((id, equipped) -> {
           Adapters.UTF_8.writeBits(id, buffer);
           Adapters.BOOLEAN.writeBits(equipped, buffer);
        });
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.equipped.clear();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            this.equipped.put(
                Adapters.UTF_8.readBits(buffer).orElseThrow(),
                Adapters.BOOLEAN.readBits(buffer).orElseThrow()
            );
        }
    }

}
