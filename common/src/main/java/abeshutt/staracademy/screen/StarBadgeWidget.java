package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ProxyStarBadges;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StarBadgeWidget extends ButtonWidget {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/star_badge.png");

    private final AbstractInventoryScreen<?> screen;

    public StarBadgeWidget(AbstractInventoryScreen<?> screen, int x, int y, PressAction onPress) {
        super(x, y, 13, 13, Text.of("Star Badges"), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(this.getX(), this.getY(), 300);
        context.drawTexture(TEXTURE, 0, 0, this.hovered ? 13.0F : 0.0F, 51.0F, 13, 13, 256, 256);
        context.getMatrices().pop();

        if(this.hovered) {
            context.drawTooltip(this.screen.textRenderer, Text.of("Badges")
                    .getWithStyle(Style.EMPTY.withColor(0xBF971C)), mouseX, mouseY);
        }

        ProxyStarBadges.of(this.screen.getScreenHandler()).ifPresent(proxy -> {
            if(proxy.getHandler().isEnabled()) {
                context.drawTexture(TEXTURE, this.screen.x - 5, this.screen.y - 42, 0, 0,
                        194, 39, 256, 256);
            }
        });
    }

}
