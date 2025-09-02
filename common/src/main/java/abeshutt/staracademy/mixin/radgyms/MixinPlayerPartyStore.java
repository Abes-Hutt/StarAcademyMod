package abeshutt.staracademy.mixin.radgyms;

import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import lol.gito.radgyms.world.DimensionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerPartyStore.class)
public class MixinPlayerPartyStore {

    @Inject(method = "onSecondPassed", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/battles/BattleRegistry;getBattleByParticipatingPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;)Lcom/cobblemon/mod/common/api/battles/model/PokemonBattle;"), cancellable = true)
    private void getBattleByParticipatingPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if(player.getWorld().getRegistryKey() == DimensionManager.INSTANCE.getRADGYMS_LEVEL_KEY()) {
            ci.cancel();
        }
    }

}
