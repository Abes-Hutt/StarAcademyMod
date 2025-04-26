package abeshutt.staracademy.mixin.terrastorage;

import abeshutt.staracademy.compat.terrastorage.TerraStorageCompat;
//import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(value = HandledScreen.class, priority = 15000)
public class MixinHandledScreenMixin {

    //@TargetHandler(mixin = "me.timvinci.mixin.client.HandledScreenMixin", name = "onInit")
    //@Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void onInit1(CallbackInfo ci) {
        //if(TerraStorageCompat.shouldBlock(this)) {
        //    ci.cancel();
        //}
    }

    //@TargetHandler(mixin = "jakesmythuk.terrastorageicons.mixin.client.HandledScreenMixin", name = "onInit")
    //@Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void onInit2(CallbackInfo ci) {
        //if(TerraStorageCompat.shouldBlock(this)) {
        //    ci.cancel();
        //}
    }

}
