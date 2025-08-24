package abeshutt.staracademy.screen;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.text.TextKt;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import kotlin.text.StringsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCALE;
import static com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.SCROLL_SLOT_SIZE;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class PokemonSlotWidget extends ClickableWidget {

    private static final Identifier slotResource = cobblemonResource("textures/gui/pokedex/pokedex_slot.png");
    private static final Identifier slotHighlight = cobblemonResource("textures/gui/pokedex/slot_select.png");

    private final Supplier<Pokemon> pokemon;
    private final float scale;
    private boolean triggered;

    public PokemonSlotWidget(Supplier<Pokemon> pokemon, int x, int y, float scale) {
        super(x, y, Math.round(SCROLL_SLOT_SIZE * scale), Math.round(SCROLL_SLOT_SIZE * scale), Text.empty());
        this.pokemon = pokemon;
        this.scale = scale;
        this.triggered = false;
    }

    public boolean isTriggered() {
        return this.triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        FloatingState state = new FloatingState();
        Pokemon pokemon = this.pokemon.get();

        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(this.getX(), this.getY(), 0);
        matrices.scale(this.scale, this.scale, 1.0F);
        blitk(matrices, slotResource, 0, 0,
                SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE);

        if(this.isTriggered()) {
            blitk(matrices, slotHighlight, 0, 0,
                    SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE, 0, 0,
                    SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE * 2);
        } else if(this.isHovered()) {
            blitk(matrices, slotHighlight, 0, 0,
                    SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE, 0, SCROLL_SLOT_SIZE,
                    SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE * 2);
        }

        //context.enableScissor(1, 1, SCROLL_SLOT_SIZE - 1, SCROLL_SLOT_SIZE - 2);

        matrices.push();
        matrices.translate(SCROLL_SLOT_SIZE / 2.0, 1.0, 0.0);
        matrices.scale(2.5F, 2.5F, 1F);

        if(pokemon != null) {
            PokemonGuiUtilsKt.drawProfilePokemon(
                    pokemon.asRenderablePokemon(), matrices,
                    QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, 35F, 0F)),
                    PoseType.PROFILE, state, 0F, 4.5F,
                    true, false, 1F, 1F, 1F, 1F);
        }

        matrices.pop();
        //context.disableScissor();

        // Ensure elements are not hidden behind Pokémon render
        matrices.push();
        matrices.translate(0.0, 0.0, 100.0);

        if(pokemon != null) {
            Species species = pokemon.getSpecies();
            String pokemonNumber = species.getNationalPokedexNumber() + "";
            pokemonNumber = StringsKt.padStart(pokemonNumber, 4, '0');
            MutableText speciesNumber = TextKt.text(pokemonNumber);
            RenderHelperKt.drawScaledText(context,
                    null, speciesNumber,
                    1.5,//2,
                    2.5,//2
                    SCALE, 1F, Integer.MAX_VALUE, 0xFFFFFFFF,
                    false, true, null, null);
        }

        //blitk(matrices, caughtIcon, (0 + 18) / SCALE, (0 + 1.5) / SCALE,
        //                    11, 11, 0, 0, 11, 11, 0,
        //                    1, 1, 1, 1, true, SCALE);

        matrices.pop();
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > this.getX() && mouseX <= this.getX() + this.getWidth()
                && mouseY > this.getY() && mouseY <= this.getY() + this.getHeight()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(CobblemonSounds.POKEDEX_CLICK, 1.0F));
            this.triggered = !this.triggered;
            return true;
        }

        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

}
