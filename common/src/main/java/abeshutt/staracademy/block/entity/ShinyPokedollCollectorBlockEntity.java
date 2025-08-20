package abeshutt.staracademy.block.entity;

import abeshutt.staracademy.block.ShinyPokedollCollectorBlock;
import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.util.FastBlockCheck;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShinyPokedollCollectorBlockEntity extends BaseBlockEntity {

    private UUID houseUuid;
    private float progress = 0.0f; // Progress of the collector, used for animations or other purposes.

    private int lifeTime = 0;

    public ShinyPokedollCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.Entities.SHINY_POKEDOLL_COLLECTOR.get(), pos, state);
    }

    public void tick(BlockState state) {
        lifeTime++;
        if (state.get(ShinyPokedollCollectorBlock.LIT) && this.world != null && !this.world.isClient()) {
            Registry<Block> blocks = world.getRegistryManager().get(RegistryKeys.BLOCK);
            if (lifeTime % ModConfigs.POKEDOLLS.getCheckInterval() == 0) {
                Set<Identifier> targets = new HashSet<>(ModConfigs.POKEDOLLS.getBlocksToDetect());

                int found = FastBlockCheck.countBlocksFast(ModConfigs.POKEDOLLS.getRadius(),
                        ModConfigs.POKEDOLLS.getNumberOfPokedolls(), true, world, pos,
                        state1 -> {
                            Identifier id = blocks.getId(state1.getBlock());

                            if(targets.contains(id)) {
                                targets.remove(id);
                                return true;
                            }

                            return false;
                        }, FastBlockCheck.EUCLIDEAN_DISTANCE);

                float newProgress = MathHelper.clamp((float) found / (float) ModConfigs.POKEDOLLS.getNumberOfPokedolls(), 0.0f, 1.0f);
                if(newProgress != this.progress) {
                    this.progress = newProgress;
                    this.markDirty();
                    this.sync();
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        if (this.houseUuid != null) {
            nbt.putUuid("house_uuid", this.houseUuid);
        } else {
            nbt.remove("house_uuid");
        }
        nbt.putFloat("progress", this.progress);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {
        if (nbt.contains("house_uuid")) {
            this.houseUuid = nbt.getUuid("house_uuid");
        } else {
            this.houseUuid = null;
        }
        this.progress = nbt.getFloat("progress");
    }

    public float getProgress() {
        return progress;
    }

    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(getPos());
        }
    }
}