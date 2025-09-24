package abeshutt.staracademy.block.entity;

import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.SafariData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class MeteoricChunkBlockEntity extends BaseBlockEntity {

    public MeteoricChunkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.Entities.METEORIC_CHUNK.get(), pos, state);
    }

    public void tick() {
        if(this.world == null || !this.world.isClient()) return;
        Random random = world.random;

        if(random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; i++) {
                SimpleParticleType particle = ParticleTypes.CAMPFIRE_COSY_SMOKE;

                world.addImportantParticle(
                        particle,
                        true,
                        pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                        pos.getY() + 0.6 + random.nextDouble() + random.nextDouble(),
                        pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                        0.0,
                        0.07,
                        0.0
                );
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {

    }

}
