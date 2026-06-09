package abeshutt.staracademy.mixin.itemzoomer;

import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import com.cobblemon.mod.common.item.PokemonItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "com.imeetake.itemzoomer.render.ZoomRenderer" })
public class MixinZoomRenderer {

    @Inject(method = "renderZoomedItem", at = @At("HEAD"), remap = false, cancellable = true)
    private static void render(DrawContext context, ItemStack stack, int x, int y, int size, CallbackInfo ci) {
        if (stack.getItem() instanceof CardItem) return;
        if (stack.getItem() instanceof PokemonItem) return;
        if (stack.getItem() == Items.PAPER) {
            var customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (customData != null && customData.contains("plushie")) return;

        }
        ci.cancel();
    }

    @Inject(method = "isMouseOverSlot", at = @At("RETURN"), remap = false, cancellable = true)
    private static void isMouseOverSlot(Slot slot, double mouseX, double mouseY, int guiLeft, int guiTop, CallbackInfoReturnable<Boolean> ci) {
        if(slot instanceof CardAlbumScreenHandler.CardSlot) {
            ci.setReturnValue(false);
        }
    }

}
