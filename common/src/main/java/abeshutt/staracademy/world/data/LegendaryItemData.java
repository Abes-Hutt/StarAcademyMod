package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class LegendaryItemData extends WorldData {

    private final List<Entry> entries;

    public LegendaryItemData() {
        this.entries = new ArrayList<>();
    }

    public void add(MinecraftServer server, Identifier id, UUID player, BlockPos pos, Identifier dimension) {
        ServerPlayerEntity target = server.getPlayerManager().getPlayer(player);

        if(target != null) {
            for(ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                other.sendMessage(Text.empty()
                        .append(target.getDisplayName())
                        .append(Text.literal(" found the ").formatted(Formatting.GRAY))
                        .append(Registries.ITEM.get(id).getName())
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }
        }

        this.entries.add(new Entry(id, ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
                player, pos, dimension));
        this.setDirty(true);
    }

    public Optional<Item> getRemainingItem(RandomSource random) {
        Set<Identifier> remainingItems = new HashSet<>(ModConfigs.LEGENDARY_ITEMS.getOccurrences());

        for(Entry entry : this.entries) {
           remainingItems.remove(entry.item);
        }

        List<Identifier> result = new ArrayList<>(remainingItems);
        if(result.isEmpty()) return Optional.empty();
        return Optional.of(result.get(random.nextInt(result.size())))
                .flatMap(id -> Registries.ITEM.getEntry(id).map(RegistryEntry.Reference::value));
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtList entries = new NbtList();

            for(Entry entry : this.entries) {
               entry.writeNbt().ifPresent(entries::add);
            }

            nbt.put("entries", entries);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList entries = nbt.getList("entries", NbtElement.COMPOUND_TYPE);
        this.entries.clear();

        for(int i = 0; i < entries.size(); i++) {
           Entry entry = new Entry();
           entry.readNbt(entries.getCompound(i));
           this.entries.add(entry);
        }
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private Identifier item;
        private long time;
        private UUID owner;
        private BlockPos pos;
        private Identifier dimension;

        public Entry() {

        }

        public Entry(Identifier item, long time, UUID owner, BlockPos pos, Identifier dimension) {
            this.item = item;
            this.time = time;
            this.owner = owner;
            this.pos = pos;
            this.dimension = dimension;
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.IDENTIFIER.writeNbt(this.item).ifPresent(tag -> nbt.put("item", tag));
                Adapters.LONG.writeNbt(this.time).ifPresent(tag -> nbt.put("time", tag));
                Adapters.UUID.writeNbt(this.owner).ifPresent(tag -> nbt.put("owner", tag));
                Adapters.BLOCK_POS.writeNbt(this.pos).ifPresent(tag -> nbt.put("pos", tag));
                Adapters.IDENTIFIER.writeNbt(this.dimension).ifPresent(tag -> nbt.put("dimension", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.item = Adapters.IDENTIFIER.readNbt(nbt.get("item")).orElseThrow();
            this.time = Adapters.LONG.readNbt(nbt.get("time")).orElseThrow();
            this.owner = Adapters.UUID.readNbt(nbt.get("owner")).orElseThrow();
            this.pos = Adapters.BLOCK_POS.readNbt(nbt.get("pos")).orElse(null);
            this.dimension = Adapters.IDENTIFIER.readNbt(nbt.get("dimension")).orElse(null);
        }
    }

}
