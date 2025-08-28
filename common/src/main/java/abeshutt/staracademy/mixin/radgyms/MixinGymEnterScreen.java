package abeshutt.staracademy.mixin.radgyms;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.client.settings.ServerSettings;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static lol.gito.radgyms.client.gui.GymGuiIdentifiers.ID_GYM_SLIDER;

@Mixin(targets = { "lol.gito.radgyms.client.gui.GymEnterScreen" })
public abstract class MixinGymEnterScreen extends BaseUIModelScreen<FlowLayout> {

    @Shadow public FlowLayout root;
    @Shadow private double gymLevel;

    protected MixinGymEnterScreen(Class<FlowLayout> rootComponentClass, DataSource source) {
        super(rootComponentClass, source);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        DiscreteSliderComponent slider = (DiscreteSliderComponent)this.root
                .childById((Class)DiscreteSliderComponent.class, ID_GYM_SLIDER);
        ((ProxyDiscreteSliderComponent)slider).setMax(ServerSettings.INSTANCE.getMaxPokemonLevel());
        this.gymLevel = Math.clamp(this.gymLevel, slider.min(), slider.max());
        slider.setFromDiscreteValue(this.gymLevel);
        super.render(context, mouseX, mouseY, delta);
    }

}
