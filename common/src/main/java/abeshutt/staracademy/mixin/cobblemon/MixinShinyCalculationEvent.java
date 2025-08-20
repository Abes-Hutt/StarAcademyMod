package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.attribute.Attributes;
import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abeshutt.staracademy.attribute.Attributes.ofBucketWeight;

@Mixin(ShinyChanceCalculationEvent.class)
public class MixinShinyCalculationEvent {

    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void calculate(ServerPlayerEntity player, CallbackInfoReturnable<Float> ci) {
        if(player != null) {
            Rational value = Rational.of(1, ci.getReturnValue());

            ci.setReturnValue(AttributeHolder.getRoot(player).path(Attributes.SHINY_CHANCE).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(value),
                        AttributeContext.random());
                return result.isPresent() ? result.get() : value;
            }).orElse(value).min(Rational.ONE).floatValue());
        }
    }

}
