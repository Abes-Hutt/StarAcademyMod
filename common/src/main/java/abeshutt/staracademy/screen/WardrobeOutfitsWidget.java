package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.client.OutfitManager;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.ValueOutfitEntry;
import abeshutt.staracademy.net.UpdateOutfitC2SPacket;
import abeshutt.staracademy.screen.helper.Texture9SliceRegion;
import abeshutt.staracademy.util.ProxyAcademyClient;
import abeshutt.staracademy.world.data.WardrobeData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class WardrobeOutfitsWidget extends ScrollableWidget {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/wardrobe.png");

    protected static Texture9SliceRegion OUTFIT_BG = new Texture9SliceRegion(19, 0, 18, 19, 256, 256);
    protected static Texture9SliceRegion OUTFIT_BG_HOVER = new Texture9SliceRegion(38, 0, 19, 19, 256, 256);

    protected int entryHeight = 25;
    protected int gap = 5;

    protected Map<String, ItemStack> outfitStackCache = new HashMap<>();
    protected List<String> unlockedOutfits;

    public WardrobeOutfitsWidget(int x, int y, int w, int h, Text text) {
        super(x, y, w, h, text);
    }

    //TODO: wth? <-- Such a Wutax comment xd
    //@Override
    //public boolean canFocus(FocusSource source) {
    //    return false;
    //}

    protected WardrobeData.Entry getServerWardrobe() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return new WardrobeData.Entry();
        return WardrobeData.CLIENT.get(player.getUuid()).orElse(new WardrobeData.Entry());
    }

    protected OutfitManager.Entry getGlobalWardrobe() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return new OutfitManager.Entry();
        OutfitManager outfits = ProxyAcademyClient.get(MinecraftClient.getInstance()).getOutfits();
        return outfits.getEntries().getOrDefault(player.getUuid(), new OutfitManager.Entry());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected int getContentsHeight() {
        return this.unlockedOutfits.size() * (this.entryHeight + this.gap);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 9;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);

        int pointerX = (int) mouseX;
        int pointerY = (int) (mouseY + getScrollY());

        WardrobeData.Entry serverWardrobe = getServerWardrobe();
        OutfitManager.Entry globalWardrobe = getGlobalWardrobe();

        int i = 0;
        for (String outfitId : this.unlockedOutfits) {
            int x = getX() + this.gap;
            int y = getY() + (i + 1) * this.gap + i * this.entryHeight - 2;
            int w = width - 2 * gap + 2;
            int h = entryHeight + gap;

            if ((x <= pointerX && pointerX <= x + w)
                    && (y <= pointerY && pointerY <= y + h)) {
                if(serverWardrobe.getUnlocked().contains(outfitId)) {
                    NetworkManager.sendToServer(new UpdateOutfitC2SPacket(outfitId,
                            !serverWardrobe.getEquipped().contains(outfitId)));
                } else if(globalWardrobe.getUnlocked().contains(outfitId)) {
                    OutfitManager outfits = ProxyAcademyClient.get(MinecraftClient.getInstance()).getOutfits();
                    outfits.setEquipped(outfitId, !globalWardrobe.getEquipped().contains(outfitId));
                }

                break;
            }

            i++;
        }

        return clicked;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        WardrobeData.Entry serverWardrobe = getServerWardrobe();
        OutfitManager.Entry globalWardrobe = getGlobalWardrobe();
        Set<String> unlocked = new LinkedHashSet<>();
        unlocked.addAll(serverWardrobe.getUnlocked());
        unlocked.addAll(globalWardrobe.getUnlocked());
        this.unlockedOutfits = new ArrayList<>(unlocked);
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        if (this.overflows()) {
            this.drawScrollbar(context);
        }
    }

    @Override
    protected void drawBox(DrawContext context, int x, int y, int width, int height) {
//        int i = this.isFocused() ? 0xffffffff : 0xffa0a0a0;
//        context.fill(x, y, x + width, y + height, i);
//        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xff000000);
        new Texture9SliceRegion(10, 0, 7, 7, 256, 256)
                .draw(context, TEXTURE, x + 1, y, width, height + 2);
    }

    private void drawScrollbar(DrawContext context) {
        int contentsHeight = this.getContentsHeight() + 4;
        int i = MathHelper.clamp((int) ((float) (this.height * this.height) / (float) contentsHeight), 32, this.height);
        int j = this.getX() + this.width;
        int k = this.getX() + this.width + 8;
        int l = Math.max(this.getY(), (int) this.getScrollY() * (this.height - i) / this.getMaxScrollY() + this.getY());
        int m = l + i;

        int trackBorder = 0xff_C6C6C6;
        int trackBg = 0xff_333333;
        int thumbBorder = 0xff_555555;
        int thumbBg = 0xff_A0A0A0;

        context.fill(j, this.getY(), k, getY() + getHeight(), trackBorder);
        context.fill(j + 1, this.getY() + 1, k - 1, getY() + getHeight() - 1, trackBg);
        context.fill(j, l, k, m, thumbBorder);
        context.fill(j + 1, l + 1, k - 1, m - 1, thumbBg);
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int pointerX = mouseX;
        int pointerY = (int) (mouseY + getScrollY());

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        WardrobeData.Entry serverWardrobe = getServerWardrobe();
        OutfitManager.Entry globalWardrobe = getGlobalWardrobe();

        int i = 0;

        for(String outfitId : this.unlockedOutfits) {
            int x = getX() + this.gap;
            int y = getY() + (i + 1) * this.gap + i * this.entryHeight - 2;
            int w = width - 2 * gap + 2;
            int h = entryHeight + gap;

            Texture9SliceRegion outfitBg = (x <= pointerX && pointerX <= x + w)
                    && (y <= pointerY && pointerY <= y + h) ? OUTFIT_BG_HOVER : OUTFIT_BG;

            outfitBg.draw(context, TEXTURE, x, y, w, h);

            ItemStack outfitStack = outfitStackCache.computeIfAbsent(outfitId, id -> {
                ItemStack itemStack = new ItemStack(ModItems.OUTFIT.get());
                itemStack.set(ModDataComponents.OUTFIT_ENTRY.get(), new ValueOutfitEntry(outfitId));
                return itemStack;
            });

            context.drawItem(outfitStack, x + 6, y + 7);

            context.getMatrices().push();
            float scale = 0.75f;
            context.getMatrices().translate(x + 24, y + 11, 0);
            context.getMatrices().scale(scale, scale, scale);
            context.drawText(textRenderer,
                    Text.translatable("item.academy.outfit." + outfitId),
                    0, 0, 0xFF_FFFFFF, false);
            context.getMatrices().pop();

            if(serverWardrobe.getEquipped().contains(outfitId)) {
                context.drawTexture(TEXTURE, x + width - 26, y + 11, 0, 39, 7, 6);
            } else if(globalWardrobe.getEquipped().contains(outfitId)) {
                context.drawTexture(TEXTURE, x + width - 26, y + 11, 0, 39, 7, 6);
            }

            i++;
        }
    }

}
