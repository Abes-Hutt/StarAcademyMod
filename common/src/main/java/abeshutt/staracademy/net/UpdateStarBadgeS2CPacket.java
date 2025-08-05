package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.data.StarBadgeData;
import abeshutt.staracademy.world.inventory.BaseInventory;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateStarBadgeS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateStarBadgeS2CPacket> ID = new Id<>(StarAcademyMod.id("update_star_badge_s2c"));

    private boolean enabled;
    private Map<UUID, BaseInventory> inventories;

    public UpdateStarBadgeS2CPacket() {

    }

    public UpdateStarBadgeS2CPacket(boolean enabled, Map<UUID, BaseInventory> profiles) {
        this.enabled = enabled;
        this.inventories = profiles;
    }

    public UpdateStarBadgeS2CPacket(boolean enabled, UUID uuid, BaseInventory inventory) {
        this.enabled = enabled;
        this.inventories = new HashMap<>();
        this.inventories.put(uuid, inventory);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        StarBadgeData.CLIENT.setEnabled(this.enabled);
        Map<UUID, BaseInventory> profiles = StarBadgeData.CLIENT.getInventories();

        if(this.inventories == null) {
            profiles.clear();
        } else {
            this.inventories.forEach((uuid, profile) -> {
                if(profile == null) {
                    profiles.remove(uuid);
                } else {
                    profiles.put(uuid, profile);
                }
            });
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.enabled, buffer);
        Adapters.BOOLEAN.writeBits(this.inventories == null, buffer);

        if(this.inventories != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.inventories.size(), buffer);

            this.inventories.forEach((uuid, inventory) -> {
                Adapters.UUID.writeBits(uuid, buffer);
                Adapters.COMPOUND_NBT.asNullable().writeBits(inventory == null ? null :
                        inventory.writeNbt().orElse(null), buffer);
            });
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.enabled = Adapters.BOOLEAN.readBits(buffer).orElseThrow();

        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.inventories = null;
        } else {
            this.inventories = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
                BaseInventory inventory = Adapters.COMPOUND_NBT.asNullable().readBits(buffer).map(tag -> {
                    BaseInventory value = new BaseInventory();
                    value.readNbt(tag);
                    return value;
                }).orElse(null);

               this.inventories.put(uuid, inventory);
            }
        }
    }

}
