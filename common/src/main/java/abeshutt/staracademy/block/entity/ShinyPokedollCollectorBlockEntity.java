package abeshutt.staracademy.block.entity;

import abeshutt.staracademy.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ShinyPokedollCollectorBlockEntity extends BaseBlockEntity {
    public ShinyPokedollCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.Entities.SHINY_POKEDOLL_COLLECTOR.get(), pos, state);
    }

    public void tick(BlockState state) {

    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type) {

    }






}
