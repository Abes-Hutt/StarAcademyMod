package abeshutt.staracademy.mixin.megashowdown;

import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "com.cobblemon.yajatkaul.mega_showdown.utility.backporting.BattleTargetSelection" })
public class MixinBattleTargetSelection {

    @Inject(method = "appendClickableNarrations", at = @At("HEAD"), cancellable = true)
    private void appendClickableNarrations(NarrationMessageBuilder builder, CallbackInfo ci) {
        ci.cancel();
    }

}
