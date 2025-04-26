package abeshutt.staracademy.world.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.io.File;
import java.util.function.Supplier;

public class WorldDataType<T extends WorldData> {

    private final String path;
    private final Supplier<T> constructor;

    public WorldDataType(String path, Supplier<T> constructor) {
        this.path = path.replace(".", File.separator);
        this.constructor = constructor;
    }

    public T getLocal(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(new PersistentState.Type<T>(
                this.constructor, (nbt, wrapperLookup) -> {
            T data = this.constructor.get();

            if(nbt instanceof NbtCompound compound) {
                data.readNbt(compound);
            }

            return data;
        }, null
        ), this.path);
    }

    public T getGlobal(MinecraftServer server) {
        return this.getLocal(server.getOverworld());
    }

    public T getGlobal(World world) {
        return this.getGlobal(world.getServer());
    }

}
