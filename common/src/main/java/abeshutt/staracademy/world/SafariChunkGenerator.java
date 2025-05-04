package abeshutt.staracademy.world;

import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.util.ProxyStructureTemplate;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class SafariChunkGenerator extends NoiseChunkGenerator {

    public static final MapCodec<SafariChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(NoiseChunkGenerator::getSettings))
                .apply(instance, instance.stable(SafariChunkGenerator::new));
    });

    public SafariChunkGenerator(BiomeSource source, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(source, settings);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess access, Chunk chunk, StructureAccessor structureAccessor) {
        super.generateFeatures(access, chunk, structureAccessor);
        if(!(access instanceof ChunkRegion region)) return;
        ServerWorld world = region.toServerWorld();

        world.getStructureTemplateManager().getTemplate(ModConfigs.SAFARI.getStructure()).ifPresent(template -> {
            ProxyStructureTemplate.of(template).ifPresent(proxy -> {
                ChunkPos chunkPos = chunk.getPos();

                for(int ox = 0; ox < 16; ox++) {
                    for(int oz = 0; oz < 16; oz++) {
                        for(int oy = world.getBottomY(); oy <= world.getTopY(); oy++) {
                            BlockPos pos = new BlockPos((chunkPos.x << 4) + ox, oy, (chunkPos.z << 4) + oz);
                            BlockPos target = pos.subtract(ModConfigs.SAFARI.getPlacementOffset());

                            if(target.getX() < template.getSize().getX() && target.getZ() < template.getSize().getZ()
                                    && target.getY() < template.getSize().getY() && target.getX() >= 0
                                    && target.getZ() >= 0 && target.getY() >= 0) {
                                StructureTemplate.StructureBlockInfo entry = proxy.get(target);

                                if(entry == null) {
                                    entry = new StructureTemplate.StructureBlockInfo(target, Blocks.AIR.getDefaultState(), null);
                                }

                                FluidState fluidState = region.getFluidState(pos);
                                BlockState state = entry.state();

                                if(entry.nbt() != null) {
                                    BlockEntity blockEntity = region.getBlockEntity(pos);
                                    Clearable.clear(blockEntity);
                                    region.setBlockState(pos, Blocks.BARRIER.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
                                }

                                if(region.setBlockState(pos, state, Block.NOTIFY_ALL)) {
                                    if(entry.nbt() != null) {
                                        BlockEntity blockEntity = region.getBlockEntity(pos);

                                        if(blockEntity != null) {
                                            blockEntity.read(entry.nbt(), world.getRegistryManager());
                                        }
                                    }

                                    if(fluidState != null && state.getBlock() instanceof FluidFillable fillable) {
                                        fillable.tryFillWithFluid(region, pos, state, fluidState);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }

}
