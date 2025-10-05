package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.init.ModPokeBalls;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.pokeball.PokeBall;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PokeBalls.class)
public class MixinPokeBalls {

    @Inject(method = "all", at = @At("RETURN"), remap = false)
    public void reload(CallbackInfoReturnable<List<PokeBall>> ci) {
        ci.getReturnValue().add(ModPokeBalls.GREAT_SAFARI_BALL);
        ci.getReturnValue().add(ModPokeBalls.GOLDEN_SAFARI_BALL);
    }

}
