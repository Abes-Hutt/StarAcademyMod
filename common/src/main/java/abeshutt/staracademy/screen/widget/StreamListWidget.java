package abeshutt.staracademy.screen.widget;

import abeshutt.staracademy.api.TwitchManager;
import abeshutt.staracademy.api.twitch.TwitchStream;
import abeshutt.staracademy.proxy.ProxyAcademyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StreamListWidget implements Drawable, Element, Widget, Selectable {

    public static final int MARGIN = 2;
    public static final int GAP = 2;

    private final List<StreamWidget> streams;
    private int iteration;
    private final Map<StreamWidget, Float> offsets;
    private int scroll;

    private int x, y;
    private final int width, height;

    public StreamListWidget(int x, int y, int width, int height) {
        this.streams = new ArrayList<>();
        this.offsets = new HashMap<>();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void add(ImageTexture profilePicture, String login, String name, boolean live) {
        this.streams.add(new StreamWidget(profilePicture, login, name, live, 0, 0, this.width - MARGIN));
    }

    public void clear() {
        this.streams.clear();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        TwitchManager twitch = ProxyAcademyClient.get(MinecraftClient.getInstance()).getTwitch();

        if (this.iteration != twitch.getIteration()) {
            this.clear();

            for (TwitchStream stream : twitch.getStreams()) {
                this.add(stream.getProfilePicture().asTexture(), stream.getLogin(), stream.getName(), stream.isLive());
            }

            this.iteration = twitch.getIteration();
        }

        if (this.streams.isEmpty()) {
            return;
        }

        context.getMatrices().push();
        context.getMatrices().translate(this.x, this.y, 0);

        int totalHeight = this.streams.size() * StreamWidget.HEIGHT + (this.streams.size() - 1) * GAP + 2;
        int displayHeight = this.height - 2;
        int scrollableHeight = totalHeight - displayHeight;
        this.scroll = MathHelper.clamp(this.scroll, 0, scrollableHeight);
        int scrollbarOffset = (int)Math.round((double)this.scroll * displayHeight / totalHeight) + 1;
        int scrollbarHeight = (int)Math.round((double)displayHeight * displayHeight / totalHeight);
        context.fill(0, scrollbarOffset, 1, scrollbarOffset + scrollbarHeight, 0xFFFFFFFF);

        Matrix4f origin = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer buffer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        buffer.vertex(origin, 0.0f, 1.0f, 0.0f).color(0xFFFFFFFF);
        buffer.vertex(origin, this.width * 0.75f, 1.0f, 0.0f).color(0x00FFFFFF);
        buffer.vertex(origin, this.width * 0.75f, 0.0f, 0.0f).color(0x00FFFFFF);
        buffer.vertex(origin, 0.0f, 0.0f, 0.0f).color(0xFFFFFFFF);
        context.draw();
        buffer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        buffer.vertex(origin, 0.0f, this.height, 0.0f).color(0xFFFFFFFF);
        buffer.vertex(origin, this.width * 0.75f, this.height, 0.0f).color(0x00FFFFFF);
        buffer.vertex(origin, this.width * 0.75f, this.height - 1, 0.0f).color(0x00FFFFFF);
        buffer.vertex(origin, 0.0f, this.height - 1, 0.0f).color(0xFFFFFFFF);
        context.draw();

        context.enableScissor(this.x, this.y + 1, this.x + this.width, this.y + this.height - 1);

        for (int i = 0; i < this.streams.size(); i++) {
            StreamWidget stream = this.streams.get(i);
            stream.setX(MARGIN);
            stream.setY((StreamWidget.HEIGHT + GAP) * i - this.scroll + 2);

            context.getMatrices().push();
            context.getMatrices().translate(this.offsets.getOrDefault(stream, 0.0f), 0.0f, 0.0f);
            stream.render(context, mouseX - this.x, mouseY - this.y, delta);
            context.getMatrices().pop();
        }

        context.disableScissor();

        for (StreamWidget stream : this.streams) {
            if (stream.isHovered()) {
                float offset = this.offsets.getOrDefault(stream, 0.0f) + delta * 2;
                this.offsets.put(stream, Math.clamp(offset, 0.0f, 2.0f));
            } else {
                float offset = this.offsets.getOrDefault(stream, 0.0f) - delta * 2;
                this.offsets.put(stream, Math.clamp(offset, 0.0f, 2.0f));
            }
        }

        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isWithinBounds(mouseX, mouseY)) {
            for (StreamWidget stream : this.streams) {
                if (stream.mouseClicked(mouseX - this.x, mouseY - this.y, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isWithinBounds(mouseX, mouseY)) {
            for (StreamWidget stream : this.streams) {
                if (stream.mouseReleased(mouseX - this.x, mouseY - this.y, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isWithinBounds(mouseX, mouseY)) {
            for (StreamWidget stream : this.streams) {
                if (stream.mouseDragged(mouseX - this.x, mouseY - this.y, button, deltaX, deltaY)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.isWithinBounds(mouseX, mouseY)) {
            this.scroll -= (int)verticalAmount * (StreamWidget.HEIGHT + GAP) / 2;
            return true;
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isWithinBounds(mouseX, mouseY);
    }

    public boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY()
                && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return Element.super.getNavigationFocus();
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {

    }

}
