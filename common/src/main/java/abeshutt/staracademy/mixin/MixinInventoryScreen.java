package abeshutt.staracademy.mixin;

import abeshutt.staracademy.screen.StarBadgeSlot;
import abeshutt.staracademy.screen.StarBadgeWidget;
import abeshutt.staracademy.screen.WardrobeScreen;
import abeshutt.staracademy.screen.WardrobeWidget;
import abeshutt.staracademy.util.ProxyStarBadges;
import abeshutt.staracademy.world.data.StarBadgeData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryScreen.class, priority = 0)
public abstract class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Unique private StarBadgeWidget starBadgeButton;
    @Unique private WardrobeWidget wardrobeButton;

    public MixinInventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("HEAD"))
    protected void init(CallbackInfo ci) {
        if(this.client != null && this.client.interactionManager != null && this.client.interactionManager.hasCreativeInventory()) {
            return;
        }

        this.starBadgeButton = this.addDrawableChild(new StarBadgeWidget(this, this.x + 160 - 14, this.y + 5, widget -> {
            ProxyStarBadges.of(this.handler).ifPresent(proxy -> {
                proxy.getHandler().setEnabled(!proxy.getHandler().isEnabled());
            });
        }));

        this.wardrobeButton = this.addDrawableChild(new WardrobeWidget(this, this.x + 160 - 14 - 120 + 1, this.y + 5 + 14 + 48, widget -> {
            MinecraftClient.getInstance().setScreen(new WardrobeScreen());
        }));

        this.addSelectableChild(this.starBadgeButton);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(!StarBadgeData.CLIENT.isEnabled()) {
            ProxyStarBadges.of(this.handler).ifPresent(proxy -> {
                proxy.getHandler().setEnabled(false);
            });
        }

        if(this.starBadgeButton != null) {
            this.starBadgeButton.visible = StarBadgeData.CLIENT.isEnabled();
            this.starBadgeButton.setPosition(this.x + 160 - 14, this.y + 5);
        }

        if(this.wardrobeButton != null) {
            this.wardrobeButton.setPosition(this.x + 160 - 14 - 120 + 1, this.y + 5 + 14 + 48);
        }
    }

    @Inject(method = "isClickOutsideBounds", at = @At("RETURN"), cancellable = true)
    protected void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> ci) {
        if(!ci.getReturnValue()) return;
        ProxyStarBadges proxy = ProxyStarBadges.of(this.handler).orElse(null);
        if(proxy == null) return;

        for(StarBadgeSlot slot : proxy.getHandler().getSlots()) {
            int minX = this.x + slot.x - 8, maxX = this.x + slot.x + 16 + 8 + 8;
            int minY = this.y + slot.y - 8, maxY = this.y + slot.y + 16 + 8 + 8;

            if(mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY) {
                ci.setReturnValue(false);
            }
        }
    }

}
