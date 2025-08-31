package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.util.ProxySelectionButton;
import abeshutt.staracademy.util.ProxySelectionScreen;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.StarterId;
import abeshutt.staracademy.world.data.StarterMode;
import com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen;
import com.cobblemon.mod.common.client.gui.startselection.widgets.CategoryList;
import com.cobblemon.mod.common.client.gui.startselection.widgets.preview.SelectionButton;
import com.cobblemon.mod.common.config.starter.RenderableStarterCategory;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static abeshutt.staracademy.GameStarterHandler.*;

@Mixin(StarterSelectionScreen.class)
public abstract class MixinStarterSelectionScreen extends Screen implements ProxySelectionScreen {

    @Shadow private int currentSelection;
    @Shadow private RenderableStarterCategory currentCategory;

    @Unique private SelectionButton button;
    @Unique private CategoryList list;
    @Unique private boolean initialized;
    @Unique private List<RenderableStarterCategory> categories;

    protected MixinStarterSelectionScreen(Text title) {
        super(title);
    }

    @Shadow protected abstract void init();
    @Shadow protected abstract void updateSelection();
    @Shadow public abstract void changeCategory(@NotNull RenderableStarterCategory category);

    @Override
    public int getSelection() {
        return this.currentSelection;
    }

    @Override
    public void setSelection(int selection) {
        this.currentSelection = MathHelper.clamp(selection, 0, this.currentCategory.getPokemon().size() - 1);
        this.updateSelection();
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        for(Element child : this.children()) {
            if(child instanceof SelectionButton button) {
                this.button = button;
            } else if(child instanceof CategoryList list) {
                try {
                    Field field = StarterSelectionScreen.class.getDeclaredField("categories");
                    field.setAccessible(true);
                    this.categories = (List<RenderableStarterCategory>)field.get(this);
                } catch(NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                this.list = list;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        PokemonStarterData data = PokemonStarterData.CLIENT;
        StarterEntry entry = data.getEntries().get(player.getUuid());
        if(entry == null) return;

        boolean isRaffle = data.getMode() == StarterMode.RAFFLE_ENABLED || data.getMode() == StarterMode.RAFFLE_PAUSED;
        StarterId current = new StarterId(this.currentCategory.getName(), this.currentSelection);

        if(isRaffle) {
            this.button.visible = entry.isAvailable() && data.getRemainingAllocations(current) > 0;
        } else {
            this.button.visible = true;
        }

        ProxySelectionButton.of(this.button).ifPresent(proxy -> {
            if(!isRaffle) {
                proxy.setText(null);
                return;
            }

            StarterId pick = data.getPick(player.getUuid());
            if(pick == null) return;

            if(current.equals(pick)) {
                proxy.setText("ui.starter.choosebutton.same");
            } else {
                proxy.setText(null);
            }
        });

        if(!this.initialized) {
            if(SCROLL_AMOUNT >= 0) {
                this.list.setScrollAmount(SCROLL_AMOUNT);
            }

            if(SELECTED_CATEGORY >= 0) {
                int index = MathHelper.clamp(SELECTED_CATEGORY, 0, this.children().size() - 1);
                this.list.setSelected(this.list.children().get(index));
                this.changeCategory(this.categories.get(index));
            }

            if(SELECTED_POKEMON >= 0) {
                this.setSelection(SELECTED_POKEMON);
            }

            this.initialized = true;
        }

        SCROLL_AMOUNT = this.list.getScrollAmount();
        SELECTED_CATEGORY = IntStream.range(0, this.list.children().size())
                .filter(i -> Objects.equals(this.list.getSelectedOrNull(), this.list.children().get(i)))
                .findFirst().orElse(-1);

        SELECTED_POKEMON = this.getSelection();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldPause() {
        return false;
    }

}