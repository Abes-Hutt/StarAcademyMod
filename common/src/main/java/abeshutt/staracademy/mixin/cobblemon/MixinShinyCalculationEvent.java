package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.attribute.Attributes;
import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.ECLunarEventTags;
import dev.corgitaco.enhancedcelestials.lunarevent.EnhancedCelestialsLunarForecastWorldData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShinyChanceCalculationEvent.class)
public class MixinShinyCalculationEvent {

    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void calculate(ServerPlayerEntity player, CallbackInfoReturnable<Float> ci) {
        float chance = ci.getReturnValue();

        if(player != null) {
            Rational base = Rational.of(1, chance);

            EnhancedCelestialsLunarForecastWorldData data =
                    EnhancedCelestials.lunarForecastWorldData(player.getWorld()).orElse(null);

            if(data != null) {
                if(data.currentLunarEventHolder().isIn(ECLunarEventTags.BLUE_MOON)) {
                    if(data.currentLunarEventHolder().isIn(ECLunarEventTags.SUPER_MOON)) {
                        base = base.multiply(ModConfigs.ENHANCED_CELESTIALS.getSuperBlueMoonShinyMultiplier());
                    } else {
                        base = base.multiply(ModConfigs.ENHANCED_CELESTIALS.getBlueMoonShinyMultiplier());
                    }
                }
            }

            Rational value = base;

            ci.setReturnValue(AttributeHolder.getRoot(player).path(Attributes.SHINY_CHANCE).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(value),
                        AttributeContext.random());
                return result.isPresent() ? result.get() : value;
            }).orElse(value).min(Rational.ONE).floatValue());
        }
    }

}
