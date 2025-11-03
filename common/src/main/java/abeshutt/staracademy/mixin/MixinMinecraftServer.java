package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.util.collection.FilteringIterable;
import abeshutt.staracademy.util.threading.ThreadPool;
import abeshutt.staracademy.world.VirtualWorld;
import abeshutt.staracademy.world.data.save.VirtualWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Unique private final ThreadPool academyMod$threadPool = new ThreadPool();

    @Shadow public abstract PlayerManager getPlayerManager();
    @Shadow protected abstract boolean shouldKeepTicking();

    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorlds()Ljava/lang/Iterable;"))
    public Iterable<ServerWorld> tickWorlds(MinecraftServer server) {
        return new FilteringIterable<>(server.getWorlds(), world -> !(world instanceof VirtualWorld));
    }

    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    public void swap(Profiler profiler, String value) {
        if("connection".equals(value)) {
            MinecraftServer server = (MinecraftServer)(Object)this;
            VirtualWorldData data = ModWorldData.VIRTUAL_WORLD.getGlobal(server);

            for(ServerPlayerEntity player : this.getPlayerManager().getPlayerList()) {
                data.get(server, VirtualWorld.island(player)).ifPresent(world -> {
                    this.academyMod$threadPool.execute(() -> {
                        world.swapThreadsAndRun(Thread.currentThread(), () -> {
                            world.safeTick(profiler, this::shouldKeepTicking);
                        });
                    });
                });
            }

            this.academyMod$threadPool.awaitCompletion();
        }

        profiler.swap("connection");
    }

}
