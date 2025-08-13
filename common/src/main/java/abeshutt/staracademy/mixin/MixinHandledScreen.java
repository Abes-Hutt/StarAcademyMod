package abeshutt.staracademy.mixin;

import abeshutt.staracademy.screen.CardAlbumScreen;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if(slot instanceof CardAlbumScreenHandler.CardSlot) {
            CardAlbumScreen.drawSlotHead(context, slot);
        }
    }

}
