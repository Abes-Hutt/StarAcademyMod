package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModKeyBindings;
import abeshutt.staracademy.net.BroadcastItemC2SPacket;
import abeshutt.staracademy.screen.CardAlbumScreen;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Shadow protected Slot focusedSlot;
    @Unique private boolean academy$keyDown;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if(slot instanceof CardAlbumScreenHandler.CardSlot) {
            CardAlbumScreen.drawSlotHead(context, slot);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    protected void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        boolean pressed = InputUtil.isKeyPressed(handle, ModKeyBindings.BROADCAST_ITEM.boundKey.getCode());

        if(this.focusedSlot != null && !this.academy$keyDown && pressed) {
            NetworkManager.sendToServer(new BroadcastItemC2SPacket(this.focusedSlot.id));
        }

        this.academy$keyDown = pressed;
    }

}
