package abeshutt.staracademy.mixin.xercapaint;

import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "xerca.xercapaint.item.crafting.RecipeFillPalette", "xerca.xercapaint.item.crafting.RecipeCraftPalette" })
public class MixinRecipeFillPalette {

    @Inject(method = "isDye", at = @At("HEAD"), cancellable = true)
    private void isDye(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (stack.getItem() instanceof DyeItem dye) {
            ci.setReturnValue(dye.getColor().getId() >= 0 && dye.getColor().getId() < 16);
        }

        ci.setReturnValue(false);
    }

}
