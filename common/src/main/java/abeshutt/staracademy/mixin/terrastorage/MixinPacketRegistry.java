package abeshutt.staracademy.mixin.terrastorage;

//import ca.landonjw.gooeylibs2.api.container.GooeyContainer;
//import me.timvinci.util.StorageAction;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(targets = { "me.timvinci.network.PacketRegistry" }, remap = false)
public class MixinPacketRegistry {

    //@Inject(method = "processStorageActionPacket", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    //private static void processStorageActionPacket(ServerPlayerEntity player, StorageAction action, boolean hotbarProtection, CallbackInfo ci) {
    //    if(player.currentScreenHandler instanceof GooeyContainer) {
    //        ci.cancel();
   //     }
   // }

}
