package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.serializable.INbtSerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.io.File;

public abstract class WorldData extends PersistentState implements INbtSerializable<NbtCompound> {

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return this.writeNbt().orElse(nbt);
    }

    @Override
    public void save(File file, RegistryWrapper.WrapperLookup registryLookup) {
        file.getParentFile().mkdirs();
        super.save(file, registryLookup);
    }

}
