package abeshutt.staracademy.mixin.radgyms;

import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DiscreteSliderComponent.class)
public interface ProxyDiscreteSliderComponent {

    @Accessor("min")
    void setMin(double min);

    @Accessor("max")
    void setMax(double max);

}
