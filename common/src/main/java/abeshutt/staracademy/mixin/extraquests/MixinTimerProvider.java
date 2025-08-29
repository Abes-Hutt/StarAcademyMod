package abeshutt.staracademy.mixin.extraquests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mixin(targets = { "com.vecoo.extraquests.storage.quests.TimerProvider" })
public class MixinTimerProvider {

    @Redirect(method = "lambda$init$0", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"), remap = false)
    private long init() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

}
