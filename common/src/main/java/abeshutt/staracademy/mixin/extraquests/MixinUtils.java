package abeshutt.staracademy.mixin.extraquests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mixin(targets = { "com.vecoo.extraquests.util.Utils" })
public class MixinUtils {

    @Redirect(method = "startTimer", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"), remap = false)
    private static long init() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

}
