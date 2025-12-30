package abeshutt.staracademy.mixin.itemobliterator;

import abeshutt.staracademy.compat.itemobliterator.ItemObliteratorCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "elocindev.item_obliterator.fabric_quilt.util.Utils" })
public class MixinUtils {

    @Inject(method = "isDisabled(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isDisabled(String itemid, CallbackInfoReturnable<Boolean> ci) {
        if (!ItemObliteratorCompat.COMPUTING) {
            ci.setReturnValue(ItemObliteratorCompat.isDisabled(itemid));
        }
    }

}
