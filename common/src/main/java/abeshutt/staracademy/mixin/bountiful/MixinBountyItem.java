package abeshutt.staracademy.mixin.bountiful;

import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(targets = { "io.ejekta.bountiful.content.BountyItem" }, remap = false)
public class MixinBountyItem {

    /*
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true, require = 0)
    public void getName(ItemStack stack, CallbackInfoReturnable<Text> ci) {
        if(Platform.getEnv() == EnvType.SERVER) {
            ci.setReturnValue(Text.literal("Bounty Item"));
        }
    }*/

}
