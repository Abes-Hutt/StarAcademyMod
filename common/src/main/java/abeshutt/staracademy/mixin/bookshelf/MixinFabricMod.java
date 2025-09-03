package abeshutt.staracademy.mixin.bookshelf;

import com.llamalad7.mixinextras.sugar.Local;
import net.darkhax.bookshelf.fabric.impl.FabricMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.HttpURLConnection;

@Mixin(FabricMod.class)
public class MixinFabricMod {

    @Inject(method = "checkForUpdates", at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setRequestMethod(Ljava/lang/String;)V"), remap = false)
    private static void checkForUpdates(CallbackInfo ci, @Local HttpURLConnection connection) {
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
    }

}
