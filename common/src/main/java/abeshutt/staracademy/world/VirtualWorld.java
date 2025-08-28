package abeshutt.staracademy.world;

import abeshutt.staracademy.StarAcademyMod;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class VirtualWorld extends ServerWorld {

    private Identifier id;

    public VirtualWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session,
                        ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions,
                        WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed,
                        List<SpecialSpawner> spawners, boolean shouldTickTime,
                        @Nullable RandomSequencesState randomSequencesState, Identifier id) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
        this.id = id;
    }

    public Identifier getId() {
        return this.id;
    }

    public void safeTick(Profiler profiler, BooleanSupplier shouldKeepTicking) {
        profiler.push(() -> this + " " + this.getRegistryKey().getValue());

        profiler.push("timeSync");
        this.getServer().getPlayerManager().sendToDimension(new WorldTimeUpdateS2CPacket(
                this.getTime(), this.getTimeOfDay(), this.getGameRules()
                .getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), this.getRegistryKey());
        profiler.pop();

        profiler.push("tick");

        try {
            this.tick(shouldKeepTicking);
        } catch(Throwable exception) {
            for(ServerPlayerEntity player : this.getPlayers()) {
                player.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.island_crash"));
            }

            CrashReport crashReport = CrashReport.create(exception, "Exception ticking world");
            this.addDetailsToCrashReport(crashReport);
            this.getServer().addSystemDetails(crashReport.getSystemDetailsSection());
            Path path = this.getServer().getRunDirectory().resolve("crash-reports").resolve("islands")
                    .resolve("crash-" + Util.getFormattedCurrentTime() + "-server.txt");

            StarAcademyMod.LOGGER.error(exception);

            if(crashReport.writeToFile(path, ReportType.MINECRAFT_CRASH_REPORT)) {
                StarAcademyMod.LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
            } else {
                StarAcademyMod.LOGGER.error("We were unable to save this crash report to disk.");
            }
        }

        profiler.pop();
        profiler.pop();
    }

    public void swapThreadsAndRun(Thread thread, Runnable runnable) {
        String oldName = thread.getName();
        Thread oldThread = this.thread;
        Thread oldChunkManagerThread = this.getChunkManager().serverThread;

        thread.setName(this.getId().toString());
        this.thread = thread;
        this.getChunkManager().serverThread = thread;

        runnable.run();

        thread.setName(oldName);
        this.thread = oldThread;
        this.getChunkManager().serverThread = oldChunkManagerThread;
    }

    public static Identifier island(PlayerEntity player) {
        return StarAcademyMod.id("island/" + player.getUuid().toString());
    }

    public static VirtualWorld create(MinecraftServer server, Identifier id, DimensionOptions dimension) {
        SaveProperties properties = server.getSaveProperties();

        return new VirtualWorld(server, server.workerExecutor, server.session,
                new UnmodifiableLevelProperties(properties, properties.getMainWorldProperties()),
                RegistryKey.of(RegistryKeys.WORLD, id),
                dimension,
                new DummyWorldGenerationProgressListener(),
                properties.isDebugWorld(),
                BiomeAccess.hashSeed(properties.getGeneratorOptions().getSeed()),
                ImmutableList.of(),
                false,
                server.getOverworld().getRandomSequences(),
                id);
    }

}
