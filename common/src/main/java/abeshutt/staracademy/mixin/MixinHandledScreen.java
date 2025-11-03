package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModKeyBindings;
import abeshutt.staracademy.net.BroadcastItemC2SPacket;
import abeshutt.staracademy.net.ToggleArmorDisplayC2SPacket;
import abeshutt.staracademy.screen.CardAlbumScreen;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import abeshutt.staracademy.world.data.save.ArmorDisplayData;
import com.llamalad7.mixinextras.sugar.Local;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.ArmorSlot;
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
    @Unique private boolean academy$broadcastKeyDown;
    @Unique private boolean academy$toggleKeyDown;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if(slot instanceof CardAlbumScreenHandler.CardSlot) {
            CardAlbumScreen.drawSlotHead(context, slot);
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    protected void drawSlotToggle(DrawContext context, Slot slot, CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(slot instanceof ArmorSlot armorSlot && player != null) {
            if(ArmorDisplayData.CLIENT.isHidden(player.getUuid(), armorSlot.equipmentSlot)) {
                context.fill(i, j, i + 16, j + 16, 0x22FF0000);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V", shift = At.Shift.AFTER))
    protected void drawSlotToggle2(DrawContext context, Slot slot, CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(slot instanceof ArmorSlot armorSlot && player != null) {
            if(ArmorDisplayData.CLIENT.isHidden(player.getUuid(), armorSlot.equipmentSlot)) {
                context.fill(i, j, i + 16, j + 16, 0x22FF0000);
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    protected void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        boolean broadcastPressed = InputUtil.isKeyPressed(handle, ModKeyBindings.BROADCAST_ITEM.boundKey.getCode());

        if(this.focusedSlot != null && !this.academy$broadcastKeyDown && broadcastPressed) {
            NetworkManager.sendToServer(new BroadcastItemC2SPacket(this.focusedSlot.id));
        }

        this.academy$broadcastKeyDown = broadcastPressed;

        boolean togglePressed = InputUtil.isKeyPressed(handle, ModKeyBindings.TOGGLE_ARMOR_DISPLAY.boundKey.getCode());

        if(this.focusedSlot != null && !this.academy$toggleKeyDown && togglePressed) {
            NetworkManager.sendToServer(new ToggleArmorDisplayC2SPacket(this.focusedSlot.id));
        }

        this.academy$toggleKeyDown = togglePressed;
    }

}
