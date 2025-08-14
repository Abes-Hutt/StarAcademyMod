package abeshutt.staracademy.screen;

import abeshutt.staracademy.net.SelectBoosterPackC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

import java.util.*;

public class BoosterPackScreen extends Screen {

    private final Set<CardSection> sections;
    private final Set<Integer> selected;

    public BoosterPackScreen() {
        super(Text.literal("Booster Pack"));
        this.sections = new HashSet<>();
        this.selected = new LinkedHashSet<>();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        ContainerComponent container = stack.getOrDefault(DataComponentTypes.CONTAINER, null);
        if(container == null) return;

        List<ItemStack> items = new ArrayList<>();
        container.iterateNonEmpty().forEach(items::add);

        float width = 56.0F;
        float spanX = width * items.size();

        for(CardSection section : this.sections) {
           this.remove(section);
        }

        this.sections.clear();

        for (int i = 0; i < items.size(); i++) {
            ItemStack outcome = items.get(i);
            float x = this.width / 2.0F - spanX / 2.0F + i * width;
            CardSection element = new CardSection(i, outcome, (int) x, 0, (int) width, this.height);
            this.addDrawableChild(element);
            this.sections.add(element);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        NetworkManager.sendToServer(new SelectBoosterPackC2SPacket(this.selected));
        super.close();
    }

    public class CardSection extends ClickableWidget {
        private final int index;
        private final ItemStack stack;

        public CardSection(int index, ItemStack stack, int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty());

            this.index = index;
            this.stack = stack;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if((this.isFocused() || this.isHovered()) && !BoosterPackScreen.this.selected.contains(this.index)) {
                //context.fillGradient(this.getX(), this.getY(), this.getX() + this.getWidth(),
                //        this.getY() + this.getHeight(), -1000, 0x15CCCCCC, 0x15CCCCCC);
            }

            RenderSystem.disableDepthTest();
            boolean selected = this.isHovered() || this.isFocused();
            float scale = 4.0F;
            context.getMatrices().push();
            context.getMatrices().translate(this.getX() + this.width / 2.0F, this.getY() + this.height / 2.0F, 0.0F);

            if(!BoosterPackScreen.this.selected.contains(this.index)) {
                context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

                if(selected) {
                    context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-1.0F));
                    scale += 0.5F;
                }
            }

            context.getMatrices().scale(scale, scale, 1.0F);
            context.getMatrices().translate(-8.0F, -8.0F, 0.0F);
            context.drawItem(this.stack, 0, 0, 0);
            context.getMatrices().pop();
            RenderSystem.enableDepthTest();

            if(this.isHovered() && BoosterPackScreen.this.selected.contains(this.index)) {
                context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, this.stack, mouseX, mouseY);
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.active && this.visible && !BoosterPackScreen.this.selected.contains(this.index)) {
                if (this.isValidClickButton(button)) {
                    boolean bl = this.clicked(mouseX, mouseY);
                    if (bl) {
                        MinecraftClient.getInstance().getSoundManager()
                                .play(PositionedSoundInstance.master(SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, 1.5F));
                        this.onClick(mouseX, mouseY);
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            BoosterPackScreen.this.selected.add(this.index);
        }
    }

}
