package abeshutt.staracademy.mixin;

import abeshutt.staracademy.screen.widget.StreamListWidget;
import abeshutt.staracademy.screen.widget.StreamWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow public int height;

    @Shadow public abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Inject(method = { "init(Lnet/minecraft/client/MinecraftClient;II)V", "clearAndInit" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V", shift = At.Shift.AFTER))
    private void init(CallbackInfo ci) {
        if (!((Object)this instanceof TitleScreen)) {
            return;
        }

        int fit = Integer.MAX_VALUE;

        for (int i = 0; i < 50; i++) {
            int height = 2 + 2 + i * StreamWidget.HEIGHT + StreamListWidget.GAP * (i - 1);

            if (Math.abs(this.height / 2 - height) < Math.abs(this.height / 2 - fit)) {
                fit = height;
            }
        }

        StreamListWidget streamList = new StreamListWidget(0, (this.height - fit) / 2, 120, fit);
        this.addDrawableChild(streamList);
    }

}
