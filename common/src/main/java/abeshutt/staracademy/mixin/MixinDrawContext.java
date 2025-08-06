package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class MixinDrawContext extends Screen {

    protected MixinDrawContext(Text title) {
        super(title);
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
    protected void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getStack();

        if(stack.isEmpty()) {
            context.drawText(textRenderer, String.valueOf(slot.id),
                    slot.x + 8 - textRenderer.getWidth(String.valueOf(slot.id)) / 2,
                    slot.y + 8 - textRenderer.fontHeight / 2, 0xCCCCCC, false);
        }
    }

}
