package abeshutt.staracademy.init;

import abeshutt.staracademy.world.SafariChunkGenerator;
import abeshutt.staracademy.world.generator.IslandChunkGenerator;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ModChunkGenerators extends ModRegistries {

    public static RegistrySupplier<MapCodec<SafariChunkGenerator>> SAFARI;
    public static RegistrySupplier<MapCodec<IslandChunkGenerator>> ISLAND;

    public static void register() {
        SAFARI = register("safari", SafariChunkGenerator.CODEC);
        ISLAND = register("island", IslandChunkGenerator.CODEC);
    }

    public static <C extends ChunkGenerator> RegistrySupplier<MapCodec<C>> register(Identifier id, MapCodec<C> generator) {
        return register(CHUNK_GENERATORS, id, () -> generator);
    }

    public static <C extends ChunkGenerator> RegistrySupplier<MapCodec<C>> register(String name, MapCodec<C> generator) {
        return register(CHUNK_GENERATORS, name, () -> generator);
    }

}
