package abeshutt.staracademy.mixin.mythsandlegends;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.utils.FormAndAspectUtils" })
public class MixinFormAndAspectUtils {

    @Redirect(method = "processPlayerData", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessageToClient(Lnet/minecraft/text/Text;Z)V"), require = 0)
    private static void processPlayerData(ServerPlayerEntity instance, Text message, boolean overlay) {

    }

}
