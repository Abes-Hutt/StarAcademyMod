package abeshutt.staracademy.mixin.scout;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(targets = { "pm.c7.scout.client.ScoutUtilClient" }, remap = false)
public class MixinScoutUtilClient {

    //@Inject(method = "isScreenBlacklisted", at = @At("RETURN"), cancellable = true, require = 0)
    private static void isScreenBlacklisted(Screen screen, CallbackInfoReturnable<Boolean> ci) {
        if(!ci.getReturnValue()) {
            if(!(screen instanceof InventoryScreen)) {
                ci.setReturnValue(true);
            }
        }
    }

}
