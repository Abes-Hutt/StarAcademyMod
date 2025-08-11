package abeshutt.staracademy.mixin;

import com.cobblemon.mod.common.Cobblemon;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.inject.ComponentStub;
import lol.gito.radgyms.client.gui.GymGuiIdentifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "io.wispforest.owo.ui.component.DiscreteSliderComponent" }, remap = false)
public abstract class MixinDiscreteSliderComponent {

    @Shadow protected double max;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lio/wispforest/owo/ui/component/DiscreteSliderComponent;updateMessage()V", shift = At.Shift.BEFORE))
    private void init(Sizing horizontalSizing, double min, double max, CallbackInfo ci) {
        if(this instanceof ComponentStub stub) {
            if(GymGuiIdentifiers.ID_GYM_SLIDER.equals(stub.id())) {
                this.max = Cobblemon.config.getMaxPokemonLevel();
            }
        }
    }

}
