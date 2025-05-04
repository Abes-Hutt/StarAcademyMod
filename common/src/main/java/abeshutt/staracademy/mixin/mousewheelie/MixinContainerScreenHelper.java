package abeshutt.staracademy.mixin.mousewheelie;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

//@Mixin(targets = { "de.siphalor.mousewheelie.client.inventory.ContainerScreenHelper" })
public class MixinContainerScreenHelper {

    //@Inject(method = "restockAllOfAKind(Ljava/util/Iterator;I)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void restockAllOfAKind(Iterator<Slot> targetSlots, int complementaryScope, CallbackInfo ci) {
        ci.cancel();
    }

}
