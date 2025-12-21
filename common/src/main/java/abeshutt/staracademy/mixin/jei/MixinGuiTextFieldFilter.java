package abeshutt.staracademy.mixin.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.gui.input.GuiTextFieldFilter;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiTextFieldFilter.class)
public class MixinGuiTextFieldFilter {

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Lmezz/jei/common/gui/elements/DrawableNineSliceTexture;draw(Lnet/minecraft/client/gui/DrawContext;Lmezz/jei/common/util/ImmutableRect2i;)V", shift = At.Shift.BEFORE))
    public void renderWidget(DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

}
