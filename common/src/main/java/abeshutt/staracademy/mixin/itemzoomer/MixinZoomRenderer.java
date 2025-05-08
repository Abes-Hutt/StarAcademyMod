package abeshutt.staracademy.mixin.itemzoomer;

import abeshutt.staracademy.item.CardItem;
import com.imeetake.itemzoomer.render.ZoomRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "com.imeetake.itemzoomer.render.ZoomRenderer" })
public class MixinZoomRenderer {

    @Inject(method = "renderZoomedItem", at = @At("HEAD"), remap = false, cancellable = true)
    private static void render(DrawContext context, ItemStack stack, int x, int y, int size, CallbackInfo ci) {
        if(!(stack.getItem() instanceof CardItem)) {
            ci.cancel();
        }
    }

}
