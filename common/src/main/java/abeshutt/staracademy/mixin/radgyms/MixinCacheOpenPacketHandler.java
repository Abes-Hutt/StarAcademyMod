package abeshutt.staracademy.mixin.radgyms;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "lol.gito.radgyms.network.CacheOpenPacketHandler" })
public class MixinCacheOpenPacketHandler {

    @Redirect(method = "invoke", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;)V"))
    private void sendMessage(ServerPlayerEntity player, Text message) {

    }

}
