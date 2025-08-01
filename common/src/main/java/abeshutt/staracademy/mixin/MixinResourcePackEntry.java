package abeshutt.staracademy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackListWidget.ResourcePackEntry.class)
public abstract class MixinResourcePackEntry {

    @Shadow @Final @Mutable private OrderedText displayName;
    @Shadow @Final protected MinecraftClient client;
    @Shadow @Final private ResourcePackOrganizer.Pack pack;

    @Shadow
    private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
        return null;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        this.displayName = trimTextToWidth(this.client, this.pack.getDisplayName());
    }

}
