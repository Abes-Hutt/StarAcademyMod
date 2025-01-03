package abeshutt.staracademy.world.data;

import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.init.ModNetwork;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.item.StarBadgeItem;
import abeshutt.staracademy.net.UpdateStarBadgeS2CPacket;
import abeshutt.staracademy.world.StarOwnership;
import abeshutt.staracademy.world.inventory.BaseInventory;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class StarBadgeData extends WorldData {

    public static final StarBadgeData CLIENT = new StarBadgeData();

    private final Map<UUID, BaseInventory> inventories;

    public StarBadgeData() {
        this.inventories = new LinkedHashMap<>();
    }

    public Map<UUID, BaseInventory> getInventories() {
        return this.inventories;
    }

    public BaseInventory getOrCreate(PlayerEntity player) {
        if(this.inventories.containsKey(player.getUuid())) {
            return this.inventories.get(player.getUuid());
        }

        BaseInventory inventory = new BaseInventory(10);
        this.inventories.put(player.getUuid(), inventory);

        inventory.addListener(sender -> {
            this.markDirty();
        });

        for(int i = 0; i < 5; i++) {
            inventory.addStack(new ItemStack(ModItems.STAR_BADGE.get()));
        }

        this.markDirty();

        if(player.getServer() != null) {
            ModNetwork.CHANNEL.sendToPlayers(player.getServer().getPlayerManager().getPlayerList(),
                    new UpdateStarBadgeS2CPacket(player.getUuid(), inventory));
        }

        return inventory;
    }

    public void onTick(MinecraftServer server) {
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.getOrCreate(player);
        }

        Set<UUID> changed = new HashSet<>();

        this.inventories.forEach((uuid, inventory) -> {
            for(int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                if(!stack.isOf(ModItems.STAR_BADGE.get())) continue;
                List<StarOwnership> ownership = StarBadgeItem.getOwnership(stack);

                if(ownership.isEmpty() || !ownership.get(ownership.size() - 1).getUuid().equals(uuid)) {
                    ownership.add(StarOwnership.ofNow(uuid));
                    StarBadgeItem.setOwnership(stack, ownership);
                    inventory.setStack(slot, stack);
                    changed.add(uuid);
                }
            }

            if(inventory.isDirty()) {
                changed.add(uuid);
                inventory.setDirty(false);
            }
        });

        if(!changed.isEmpty()) {
            for(UUID uuid : changed) {
                ModNetwork.CHANNEL.sendToPlayers(server.getPlayerManager().getPlayerList(),
                        new UpdateStarBadgeS2CPacket(uuid, this.inventories.get(uuid)));
            }
        }
    }

    private void onJoin(ServerPlayerEntity player) {
        ModNetwork.CHANNEL.sendToPlayer(player, new UpdateStarBadgeS2CPacket(null));
        ModNetwork.CHANNEL.sendToPlayer(player, new UpdateStarBadgeS2CPacket(this.inventories));
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            this.inventories.forEach((uuid, inventory) -> {
                inventory.writeNbt().ifPresent(tag -> nbt.put(uuid.toString(), tag));
            });

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.inventories.clear();

        for(String key : nbt.getKeys()) {
            BaseInventory inventory = new BaseInventory();
            inventory.readNbt(nbt.getCompound(key));
            this.inventories.put(UUID.fromString(key), inventory);

            inventory.addListener(sender -> {
                this.markDirty();
            });
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            StarBadgeData data = ModWorldData.STAR_BADGE.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            StarBadgeData data = ModWorldData.STAR_BADGE.getGlobal(server);
            data.onTick(server);
        });
    }

}
