package abeshutt.staracademy.mixin.extraquests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mixin(targets = { "com.vecoo.extraquests.storage.quests.TimerStorage" })
public class MixinTimerStorage {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"))
    private long init() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

}
