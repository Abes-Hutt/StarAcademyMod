package abeshutt.staracademy.world.data;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.block.entity.SafariPortalBlockEntity;
import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateSafariConfigS2CPacket;
import abeshutt.staracademy.net.UpdateSafariS2CPacket;
import abeshutt.staracademy.world.DummyWorldGenerationProgressListener;
import com.cobblemon.mod.common.CobblemonItems;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorderListener.WorldBorderSyncer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.UnmodifiableLevelProperties;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static net.minecraft.entity.Entity.RemovalReason.CHANGED_DIMENSION;

public class SafariData extends WorldData {

    public static final SafariData CLIENT = new SafariData(false);

    private long timeLeft;
    private boolean paused;
    private long lastUpdated;
    private final Map<RegistryKey<World>, Set<BlockPos>> portals;
    private final Map<UUID, Entry> entries;

    private SafariConfig lastConfig;

    private SafariData(boolean paused) {
        this.paused = paused;
        this.portals = new HashMap<>();
        this.entries = new HashMap<>();
    }

    public SafariData() {
        this(ModConfigs.SAFARI.isPaused());
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean setPaused(boolean paused) {
        boolean changed = this.paused != paused;
        this.paused = paused;
        return changed;
    }

    public Map<UUID, Entry> getEntries() {
        return this.entries;
    }

    public Optional<Entry> get(UUID uuid) {
        return Optional.ofNullable(this.entries.get(uuid));
    }

    public Entry getOrCreate(UUID uuid) {
        return this.entries.computeIfAbsent(uuid, key -> {
            this.markDirty();
            Entry entry = new Entry();
            entry.setTimeLeft(ModConfigs.SAFARI.getPlayerDuration());
            return entry;
        });
    }

    public void joinSafari(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if(server == null) return;
        ServerWorld destination = server.getWorld(StarAcademyMod.SAFARI);

        if(destination != null) {
            this.getOrCreate(player.getUuid()).setLastState(new EntityState(player));
            //player.interactionManager.changeGameMode(GameMode.ADVENTURE);
            player.tryUsePortal(ModBlocks.SAFARI_PORTAL.get(), player.getBlockPos());
            //ProxyEntity.of(player).ifPresent(proxy -> {
            //    proxy.schedulePortalTick(() -> player.moveToWorld(destination));
            //});

            //TODO: For some reason this causes a CME...
            //player.moveToWorld(destination);
        }
    }

    public void leaveSafari(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if(server == null) return;
        SafariData.Entry entry = this.get(player.getUuid()).orElse(null);

        if(entry == null || entry.getLastState() == null) {
            player.getServer().getPlayerManager().respawnPlayer(player, true, CHANGED_DIMENSION);
        } else {
            //player.interactionManager.changeGameMode(entry.getLastState().getGameMode());
            player.tryUsePortal(ModBlocks.SAFARI_PORTAL.get(), player.getBlockPos());

            /*
            RegistryKey<World> dimension = entry.getLastState().getDimension();
            ServerWorld destination = server.getWorld(dimension);

            if(destination != null) {
                ProxyEntity.of(player).ifPresent(proxy -> {
                    proxy.schedulePortalTick(() -> player.moveToWorld(destination));
                });

                player.moveToWorld(destination);
            }*/
        }

        //ProxyEntity.of(player).ifPresent(proxy -> {
        //    proxy.setSafariPortalCooldown(true);
        //});

        //player.setPortalCooldown(20);
    }

    public void addPortal(World world, BlockPos pos) {
        this.portals.computeIfAbsent(world.getRegistryKey(), key -> new HashSet<>()).add(pos);
    }

    public void onStart(MinecraftServer server) {
        ServerWorld world = server.getWorld(StarAcademyMod.SAFARI);
        if(world == null) return;

        for(ServerPlayerEntity player : new ArrayList<>(world.getPlayers())) {
            this.leaveSafari(player);
        }

        Path folder = world.getServer().session.getWorldDirectory(world.getRegistryKey());
        server.worlds.remove(StarAcademyMod.SAFARI);

        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                    StarAcademyMod.LOGGER.debug("Deleting {}", path);
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
                    if(exception != null) throw exception;
                    if(path.equals(folder)) Files.deleteIfExists(path);
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch(IOException ignored) { }

        UnmodifiableLevelProperties worldProperties = new UnmodifiableLevelProperties(server.getSaveProperties(),
                server.getSaveProperties().getMainWorldProperties());

        DimensionOptions options = server.getCombinedDynamicRegistries().getCombinedRegistryManager()
                .get(RegistryKeys.DIMENSION).get(StarAcademyMod.SAFARI.getValue());

        ServerWorld newWorld = new ServerWorld(server, Util.getMainWorkerExecutor(), server.session, worldProperties,
                world.getRegistryKey(), options, new DummyWorldGenerationProgressListener(),
                server.getSaveProperties().isDebugWorld(), BiomeAccess.hashSeed(server.getSaveProperties().getGeneratorOptions().getSeed()),
                ImmutableList.of(), false, server.getOverworld().getRandomSequences());

        server.getOverworld().getWorldBorder().addListener(new WorldBorderSyncer(newWorld.getWorldBorder()));
        server.worlds.put(world.getRegistryKey(), newWorld);

        this.timeLeft = ModConfigs.SAFARI.getTimeLeft(this.lastUpdated) / 50;
        this.entries.clear();
    }

    private void onJoin(ServerPlayerEntity player) {
        NetworkManager.sendToPlayer(player, new UpdateSafariConfigS2CPacket(ModConfigs.SAFARI.getTickets()));
    }

    public void onTick(MinecraftServer server) {
        this.timeLeft = ModConfigs.SAFARI.getTimeLeft(this.lastUpdated) / 50;

        if(this.timeLeft <= 0) {
            this.lastUpdated = System.currentTimeMillis();
            this.onStart(server);
        }

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
           this.getOrCreate(player.getUuid());

           this.get(player.getUuid()).ifPresent(entry -> {
               if(player.getWorld().getRegistryKey() == StarAcademyMod.SAFARI) {
                   if(entry.getTimeLeft() <= 0 || player.getY() < player.getWorld().getBottomY()) {
                       player.fallDistance = 0.0F;
                       this.leaveSafari(player);
                   }
               }
           });
        }

        this.portals.forEach((key, positions) -> {
            ServerWorld world = server.getWorld(key);
            if(world == null) return;

            positions.removeIf(pos -> {
                Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
                if(chunk == null) return false;
                return !(world.getBlockEntity(pos) instanceof SafariPortalBlockEntity);
            });
        });

        Set<UUID> dirty = new HashSet<>();

        this.entries.forEach((uuid, entry) -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if(player == null) return;

            if(player.getWorld().getRegistryKey() == StarAcademyMod.SAFARI) {
                if(entry.getBallsGiven() < ModConfigs.SAFARI.getProvidedSafariBalls()) {
                    int missing = ModConfigs.SAFARI.getProvidedSafariBalls() - entry.getBallsGiven();
                    ItemStack stack = new ItemStack(CobblemonItems.SAFARI_BALL, missing);
                    player.getInventory().insertStack(stack);
                    entry.setBallsGiven(entry.getBallsGiven() + (missing - stack.getCount()));
                    dirty.add(uuid);
                }

                if(entry.getTimeLeft() > 0) {
                    entry.setTimeLeft(entry.getTimeLeft() - 1);
                    entry.setTimeLeft(Math.min(entry.getTimeLeft(), this.timeLeft));
                    dirty.add(uuid);
                }
            }

            BlockPos nearestPortal = null;
            double nearestDistance = Double.MAX_VALUE;
            Set<BlockPos> positions = this.portals.getOrDefault(player.getWorld().getRegistryKey(), new HashSet<>());

            for(BlockPos position : positions) {
                double dx = player.getCameraPosVec(1.0F).x - position.getX() + 0.5D;
                double dy = player.getCameraPosVec(1.0F).y - position.getY() + 0.5D;
                double dz = player.getCameraPosVec(1.0F).z - position.getZ() + 0.5D;
                double distance = dx * dx + dy * dy + dz * dz;

                if(distance < nearestDistance) {
                    nearestPortal = position;
                    nearestDistance = distance;
                }
            }

            entry.setNearestPortal(nearestPortal);
            dirty.add(uuid);
        });

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if(dirty.contains(player.getUuid())) {
                NetworkManager.sendToPlayer(player, new UpdateSafariS2CPacket(this.timeLeft, this.paused, player.getUuid(), this.entries.get(player.getUuid())));
            } else {
                NetworkManager.sendToPlayer(player, new UpdateSafariS2CPacket(this.timeLeft, this.paused, new HashMap<>()));
            }
        }

        if(this.lastConfig != ModConfigs.SAFARI) {
            NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(), new UpdateSafariConfigS2CPacket(ModConfigs.SAFARI.getTickets()));
            this.lastConfig = ModConfigs.SAFARI;
        }

        this.markDirty();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound portals = new NbtCompound();

            this.portals.forEach((key, positions) -> {
                NbtList entry = new NbtList();

                for(BlockPos position : positions) {
                   Adapters.BLOCK_POS.writeNbt(position).ifPresent(entry::add);
                }

                portals.put(key.getValue().toString(), entry);
            });

            nbt.put("portals", portals);

            NbtCompound entries = new NbtCompound();

            this.entries.forEach((uuid, entry) -> {
                entry.writeNbt().ifPresent(tag -> entries.put(uuid.toString(), tag));
            });

            nbt.put("entries", entries);
            Adapters.LONG.writeNbt(this.timeLeft).ifPresent(tag -> nbt.put("timeLeft", tag));
            Adapters.LONG.writeNbt(this.lastUpdated).ifPresent(tag -> nbt.put("lastUpdated", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.portals.clear();

        for(String s : nbt.getCompound("portals").getKeys()) {
            RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(s));
            Set<BlockPos> positions = new HashSet<>();

            if(nbt.getCompound("portals").get(s) instanceof NbtList entry) {
                for(NbtElement child : entry) {
                   Adapters.BLOCK_POS.readNbt(child).ifPresent(positions::add);
                }
            }

            this.portals.put(key, positions);
        }

        this.entries.clear();

        for(String key : nbt.getCompound("entries").getKeys()) {
            Entry entry = new Entry();
            entry.readNbt(nbt.getCompound("entries").getCompound(key));
            this.entries.put(UUID.fromString(key), entry);
        }

        this.timeLeft = Adapters.LONG.readNbt(nbt.get("timeLeft")).orElse(0L);
        this.lastUpdated = Adapters.LONG.readNbt(nbt.get("lastUpdated")).orElse(0L);
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            SafariData data = ModWorldData.SAFARI.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            SafariData data = ModWorldData.SAFARI.getGlobal(server);
            data.onTick(server);
        });
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private EntityState lastState;
        private long timeLeft;
        private int ballsGiven;
        private BlockPos nearestPortal;

        public EntityState getLastState() {
            return this.lastState;
        }

        public void setLastState(EntityState lastState) {
            this.lastState = lastState;
        }

        public long getTimeLeft() {
            return this.timeLeft;
        }

        public void setTimeLeft(long timeLeft) {
            this.timeLeft = timeLeft;
        }

        public int getBallsGiven() {
            return this.ballsGiven;
        }

        public void setBallsGiven(int ballsGiven) {
            this.ballsGiven = ballsGiven;
        }

        public BlockPos getNearestPortal() {
            return this.nearestPortal;
        }

        public void setNearestPortal(BlockPos nearestPortal) {
            this.nearestPortal = nearestPortal;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.BOOLEAN.writeBits(this.lastState != null, buffer);

            if(this.lastState != null) {
                this.lastState.writeBits(buffer);
            }

            Adapters.LONG.writeBits(this.timeLeft, buffer);
            Adapters.INT.writeBits(this.ballsGiven, buffer);
            Adapters.BLOCK_POS.asNullable().writeBits(this.nearestPortal, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                this.lastState = new EntityState();
                this.lastState.readBits(buffer);
            } else {
                this.lastState = null;
            }

            this.timeLeft = Adapters.LONG.readBits(buffer).orElseThrow();
            this.ballsGiven = Adapters.INT.readBits(buffer).orElseThrow();
            this.nearestPortal = Adapters.BLOCK_POS.asNullable().readBits(buffer).orElse(null);
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                if(this.lastState != null) {
                    this.lastState.writeNbt().ifPresent(tag -> nbt.put("lastState", tag));
                }

                Adapters.LONG.writeNbt(this.timeLeft).ifPresent(tag -> nbt.put("timeLeft", tag));
                Adapters.INT.writeNbt(this.ballsGiven).ifPresent(tag -> nbt.put("ballsGiven", tag));
                Adapters.BLOCK_POS.writeNbt(this.nearestPortal).ifPresent(tag -> nbt.put("nearestPortal", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            if(nbt.contains("lastState")) {
                this.lastState = new EntityState();
                this.lastState.readNbt(nbt.getCompound("lastState"));
            } else {
                this.lastState = null;
            }

            this.timeLeft = Adapters.LONG.readNbt(nbt.get("timeLeft")).orElse(0L);
            this.ballsGiven = Adapters.INT.readNbt(nbt.get("ballsGiven")).orElse(0);
            this.nearestPortal = Adapters.BLOCK_POS.readNbt(nbt.get("nearestPortal")).orElse(null);
        }
    }

}
