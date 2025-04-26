package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WardrobeWidget extends ButtonWidget {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/wardrobe.png");

    private final AbstractInventoryScreen<?> screen;

    public WardrobeWidget(AbstractInventoryScreen<?> screen, int x, int y, PressAction onPress) {
        super(x, y, 12, 12, Text.of("Wardrobe"), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(this.getX(), this.getY(), 300);
        context.drawTexture(TEXTURE, 0, 0, this.hovered ? 12.0F : 0.0F, 24.0F, 12, 12, 256, 256);
        context.getMatrices().pop();

        if(this.hovered) {
            context.drawTooltip(this.screen.textRenderer, Text.of("Wardrobe")
                    .getWithStyle(Style.EMPTY.withColor(0xBF971C)), mouseX, mouseY);
        }
    }

}
