package abeshutt.staracademy.mixin.eternalstarlight;

import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.util.ProxyStructureTemplate;
import cn.leolezury.eternalstarlight.common.world.gen.chunkgenerator.ESChunkGenerator;
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
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ESChunkGenerator.class)
public abstract class MixinESChunkGenerator extends NoiseChunkGenerator {

    public MixinESChunkGenerator(BiomeSource source, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(source, settings);
    }

    @Override
    public void generateFeatures(StructureWorldAccess access, Chunk chunk, StructureAccessor structureAccessor) {
        if(!(access instanceof ChunkRegion region)) {
            super.generateFeatures(access, chunk, structureAccessor);
            return;
        }

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

        super.generateFeatures(access, chunk, structureAccessor);
    }

}
