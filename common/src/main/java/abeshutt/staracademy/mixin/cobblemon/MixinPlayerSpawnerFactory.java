package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.attribute.again.AttributePlayerInfluence;
import com.cobblemon.mod.common.api.spawning.SpawnerManager;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSpawnerFactory.class)
public class MixinPlayerSpawnerFactory {

    @Inject(method = "create", at = @At("RETURN"), remap = false)
    private void create(SpawnerManager spawnerManager, ServerPlayerEntity player, CallbackInfoReturnable<PlayerSpawner> ci) {
        PlayerSpawner spawner = ci.getReturnValue();
        spawner.getInfluences().add(new AttributePlayerInfluence(player));
    }

}
