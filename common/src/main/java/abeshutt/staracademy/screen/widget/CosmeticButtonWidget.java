package abeshutt.staracademy.screen.widget;

import abeshutt.staracademy.cosmetic.Cosmetic;
import abeshutt.staracademy.cosmetic.CosmeticSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.function.Consumer;
import java.util.function.Function;

public class CosmeticButtonWidget extends ButtonWidget {

    private final Identifier texture;
    private final int textureHeight;
    private final int textureWidth;
    private final Function<CosmeticButtonWidget, Vector2i> uvs;
    private boolean triggered;


    protected CosmeticButtonWidget(int x, int y, int width, int height, Text message, Identifier texture,
                                   int textureWidth, int textureHeight, Function<CosmeticButtonWidget, Vector2i> uvs,
                                   Tooltip tooltip, Consumer<CosmeticButtonWidget> onPress) {
        super(x, y, width, height, message,
                button -> onPress.accept((CosmeticButtonWidget)button),
                ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.texture = texture;
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.uvs = uvs;
        this.setTooltip(tooltip);
    }

    public static CosmeticButtonWidget ofSlot(int x, int y, CosmeticSlot slot, Consumer<CosmeticButtonWidget> onPress) {
        Text tooltip = Text.empty().append(slot.getNameText())
                .append("\n").append(slot.getDescriptionText().formatted(Formatting.GRAY));

        return new CosmeticButtonWidget(x, y, 16, 16, slot.getNameText(),
                slot.getIcon(), 16, 48, button -> {
                    int u = 0;
                    int v = 0;

                    if (button.isTriggered()) {
                        v = 16;
                    } else if (button.isHovered()) {
                        v = 32;
                    }

                    return new Vector2i(u, v);
                }, Tooltip.of(tooltip, tooltip), onPress);
    }

    public static CosmeticButtonWidget ofTab(int x, int y, String name, String description, Identifier texture, Consumer<CosmeticButtonWidget> onPress) {
        Text tooltip = Text.empty().append(Text.literal(name))
                .append("\n").append(Text.literal(description).formatted(Formatting.GRAY));

        return new CosmeticButtonWidget(x, y, 22, 21, Text.literal(name),
                texture, 22, 63, button -> {
            int u = 0;
            int v = 0;

            if (button.isTriggered()) {
                v = 21;
            } else if (button.isHovered()) {
                v = 42;
            }

            return new Vector2i(u, v);
        }, Tooltip.of(tooltip, tooltip), onPress);
    }

    public static CosmeticButtonWidget ofTile(int x, int y, Cosmetic cosmetic, Identifier texture, Consumer<CosmeticButtonWidget> onPress) {
        Text tooltip = Text.empty().append(cosmetic.getNameText())
                .append("\n").append(cosmetic.getDescriptionText().formatted(Formatting.GRAY));

        return new CosmeticButtonWidget(x, y, 28, 28, cosmetic.getNameText(),
                texture, 28, 28, button -> {
            int u = 0;
            int v = 0;

            if (button.isTriggered()) {
                v = 28;
            } else if (button.isHovered()) {
                v = 56;
            }

            return new Vector2i(u, v);
        }, Tooltip.of(tooltip, tooltip), onPress) {
            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                context.getMatrices().push();
                context.getMatrices().translate(this.getX(), this.getY(), 0);
                context.getMatrices().scale(22.0f / 16.0f, 22.0f / 16.0f, 1.0f);
                context.getMatrices().translate(-this.getX(), -this.getY(), 0);
                context.drawTexture(cosmetic.getIcon(),
                        (int)(this.getX() + 3 * 16.0f / 22.0f),
                        (int)(this.getY() + 3 * 16.0f / 22.0f),
                        0, 0, 16, 16, 16, 16);
                context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                context.getMatrices().pop();
            }
        };
    }

    public boolean isTriggered() {
        return this.triggered;
    }

    public CosmeticButtonWidget setTriggered(boolean triggered) {
        this.triggered = triggered;
        return this;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        Vector2i uv = this.uvs.apply(this);
        context.drawTexture(this.texture, this.getX(), this.getY(),
                uv.x, uv.y, this.width, this.height, this.textureWidth, this.textureHeight);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
