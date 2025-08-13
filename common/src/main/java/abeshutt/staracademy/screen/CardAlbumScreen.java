package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler.CardSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CardAlbumScreen extends HandledScreen<CardAlbumScreenHandler> implements ScreenHandlerProvider<CardAlbumScreenHandler> {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/card_album.png");

    public CardAlbumScreen(CardAlbumScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 250;
        this.backgroundWidth = 256;
    }

    public static void drawSlotHead(DrawContext context, Slot slot) {
        float scale = 48.0F / 18.0F;
        context.getMatrices().scale(scale, scale, scale);
        context.getMatrices().translate(-slot.x, -slot.y, 0.0F);
        context.getMatrices().translate(slot.x / scale - 2.2, slot.y / scale + 1.2, -100.0F);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int i = this.x;
        int j = this.y;
        RenderSystem.disableDepthTest();
        context.getMatrices().push();
        context.getMatrices().translate((float)i, (float)j, 0.0F);

        for(int k = 0; k < this.handler.slots.size(); k++) {
            Slot slot = this.handler.slots.get(k);

            if(this.isPointOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                int l = slot.x;
                int m = slot.y;

                if(slot instanceof CardSlot) {
                    context.fillGradient(RenderLayer.getGuiOverlay(), l, m, l + 31, m + 48, -2130706433, -2130706433, 0);
                }
            }
        }

        context.getMatrices().pop();
        RenderSystem.enableDepthTest();
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        if(slot instanceof CardSlot) {
            return this.isPointWithinBounds(slot.x, slot.y, 31, 48, pointX, pointY);
        }

        return super.isPointOverSlot(slot, pointX, pointY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, 256);
    }

}
