package abeshutt.staracademy.mixin.terrastorage;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(value = InventoryScreen.class, priority = 15000)
public class MixinInventoryScreenMixin {

    //@TargetHandler(mixin = "me.timvinci.mixin.client.InventoryScreenMixin", name = "onInit")
    //@Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void onInit1(CallbackInfo ci) {
        //if(TerraStorageCompat.shouldBlock(this)) {
        //    ci.cancel();
        //}
    }

    //@TargetHandler(mixin = "jakesmythuk.terrastorageicons.mixin.client.InventoryScreenMixin", name = "onInit")
    //@Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void onInit2(CallbackInfo ci) {
        //if(TerraStorageCompat.shouldBlock(this)) {
        //    ci.cancel();
        //}
    }


}
