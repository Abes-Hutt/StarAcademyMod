package abeshutt.staracademy.screen;

import com.cobblemon.mod.common.client.gui.CobblemonRenderable;
import com.cobblemon.mod.common.mixin.accessor.EntryListWidgetAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.MathHelper;

public class ScrollingWidget<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget<E> implements CobblemonRenderable {

    public int top;
    public int left;
    public int width;
    public int height;
    public int slotHeight;
    public int scrollBarWidth;

    public ScrollingWidget(int top, int left, int width, int height, int slotHeight, int scrollBarWidth) {
        super(MinecraftClient.getInstance(), width, height, top, slotHeight);
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.slotHeight = slotHeight;
        this.scrollBarWidth = scrollBarWidth;

        this.setDimensionsAndPosition(width, height, top, top + height);
        this.setLeft(left);
    }

    @Override
    public void setDimensionsAndPosition(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.setX(this.left + width);
        this.setY(bottom);
    }

    public void setLeft(int left) {
        this.setX(left);
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {

    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {

    }

    @Override
    protected void renderDecorations(DrawContext context, int mouseX, int mouseY) {

    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        EntryListWidgetAccessor accessor = (EntryListWidgetAccessor)this;
        this.setFocused(this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null);

        int l1 = this.getRowLeft();
        int l = this.getY() + 4 - (int)this.getScrollAmount();

        this.enableScissor(context);

        if(accessor.getRenderHeader()) {
            this.renderHeader(context, l1, l);
        }

        this.renderListItems(context, mouseX, mouseY, delta);
        context.disableScissor();

        int maxScroll = this.getMaxScroll();

        if(maxScroll > 0) {
            this.renderScrollbar(context, mouseX, mouseY, delta);
        }

        this.renderDecorations(context, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    public void renderScrollbar(DrawContext context, int mouseX, int mouseY, float delta) {
        int xLeft = this.getScrollbarX();
        int xRight = xLeft + this.scrollBarWidth;

        int barHeight = this.getBottom() - this.getY();

        int j2 = (int)((float)(barHeight * barHeight) / this.getMaxPosition());
        j2 = MathHelper.clamp(j2, 32, barHeight - 8);
        var k1 = (int)this.getScrollAmount() * (barHeight - j2) / this.getMaxScroll() + this.getY();

        if(k1 < this.getY()) {
            k1 = this.getY();
        }

        context.fill(xLeft, this.getY(), xRight, this.getBottom(), -16777216);
        context.fill(xLeft, k1, xRight, k1 + j2, -8355712);
        context.fill(xLeft ,k1, xRight - 1, k1 + j2 - 1, -4144960);
    }

    public void renderHorizontalShadows(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);

        context.drawTexture(
                Screen.MENU_BACKGROUND_TEXTURE,
                this.left, 0, 0.0f, 0.0f,
                this.width,
                this.getY(), 32, 32
        );
        context.drawTexture(
                Screen.MENU_BACKGROUND_TEXTURE,
                this.left,
                this.getBottom(), 0.0f,
                this.getBottom(),
                this.width,
                this.height - this.getBottom(), 32, 32
        );
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        context.fillGradient(
                RenderLayer.getGuiOverlay(),
                this.left,
                this.getY(),
                this.getRight(),
                this.getY() + 4, -16777216, 0, 0
        );

        context.fillGradient(
                RenderLayer.getGuiOverlay(),
                this.left,
                this.getBottom() - 4,
                this.getRight(),
                this.getBottom(), 0, -16777216, 0
        );
    }

    public void renderListItems(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight;
        int l = this.getEntryCount();

        for(int m = 0; m < l; m++) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);

            if(o >= this.getY() && n <= this.getBottom()) {
                this.renderItem(context, mouseX, mouseY, delta, m, i, n, j, k);
            }
        }
    }

    public void renderItem(DrawContext context, int mouseX, int mouseY, float delta,
            int index, int x, int y, int entryWidth, int entryHeight) {
        E entry = this.getEntry(index);
        entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY,
                this.getFocused() == entry, delta);
    }

    @Override
    public int getRowLeft() {
        return this.left;
    }

    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public int getRowTop(int index) {
        return this.getY() - (int)this.getScrollAmount() + (index * this.itemHeight);
    }

    @Override
    public int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    @Override
    protected int getScrollbarX() {
        return this.left + this.width - this.scrollBarWidth;
    }

    public abstract static class Slot<T extends Slot<T>> extends AlwaysSelectedEntryListWidget.Entry<T> {

    }

}
