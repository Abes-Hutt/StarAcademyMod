package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.StarterId;
import abeshutt.staracademy.world.data.StarterPokemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.gui.trade.ModelWidget;
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class StarterSelectionWidget implements Drawable {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/starter_selection.png");

    private final StarterId starter;
    private final Long timeLeft;
    private final boolean paused;

    public StarterSelectionWidget(StarterId starter, long timeLeft, boolean paused) {
        this.starter = starter;
        this.timeLeft = timeLeft;
        this.paused = paused;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = 0;
        int y = 68;
        int width = 163;
        int height = 70 - 16;
        TextRenderer text = MinecraftClient.getInstance().textRenderer;

        for(int i = 0; i < width; i++) {
           context.drawTexture(TEXTURE, x + i, y - 8, 0, 0, 1, 8, 256, 256);
            context.drawTexture(TEXTURE, x + i, y + height, 0, 11, 1, 8, 256, 256);
        }

        for(int i = 0; i < height; i++) {
            context.drawTexture(TEXTURE, x + width, y + i, 2, 9, 8, 1, 256, 256);
        }

        context.drawTexture(TEXTURE, x + width, y - 8, 2, 0, 8, 8, 256, 256);
        context.drawTexture(TEXTURE, x + width, y + height, 2, 11, 8, 8, 256, 256);

        context.fill(x, y, x + width, y + height, 0xFF333333);

        StarterPokemon pokemon = Optional.ofNullable(this.starter).flatMap(StarterId::resolve).orElse(null);
        String title = pokemon != null ? pokemon.getSpecies().getName() : "Raffle";
        int color = pokemon != null ? pokemon.getSpecies().getPrimaryType().getHue() : Formatting.GRAY.getColorValue();

        context.getMatrices().push();
        context.getMatrices().translate(x + 57.0D, y + 1.5D, 0.0D);

        MutableText description;

        if(pokemon != null) {
            description = Text.translatable(pokemon.getSpecies().getForm(pokemon.getAspects()).getPokedex().getFirst());
        } else {
            description = Text.empty()
                .append(Text.literal("Press ["))
                .append(CobblemonKeyBinds.INSTANCE.getSUMMARY().getBoundKeyLocalizedText())
                .append(Text.literal("] to choose your starter Pokémon. If more than "
                        + PokemonStarterData.CLIENT.getAllocations() + " players select the same Pokémon, " +
                        "no one will receive it. Choose wisely!"));
        }

        List<OrderedText> lines = text.wrapLines(description, 172);
        lines = lines.subList(0, Math.min(lines.size(), 7));
        context.getMatrices().push();
        context.getMatrices().translate(0.0D, 13.0D, 0.0D);
        context.getMatrices().scale(0.61F, 0.61F, 0.61F);

        for(OrderedText line : lines) {
            context.drawText(text, line, 0, 0, Formatting.GRAY.getColorValue(), true);
            context.getMatrices().translate(0.0D, text.fontHeight, 0.0D);
        }

        context.getMatrices().pop();

        context.getMatrices().push();
        context.getMatrices().scale(1.2F, 1.2F, 1.2F);
        context.drawText(text, Text.empty()
                .append(Text.literal(title).setStyle(Style.EMPTY.withColor(color))), 0, 0, 0xFFFFFF, true);
        context.getMatrices().pop();

        context.getMatrices().push();
        double size = text.getWidth(title + " ") * 1.2D;
        context.getMatrices().translate(size - 2.5D, 0.0D, 0.0D);
        context.getMatrices().scale(0.75F, 0.75F, 0.75F);

        context.getMatrices().push();
        context.getMatrices().scale(1.0F, 1.60F, 1.0F);
        context.drawText(text, Text.empty()
                .append(Text.literal("|")), 0, 0, color, true);
        context.getMatrices().pop();

        String time = this.paused ? "Waiting" : this.formatTimeString(this.timeLeft);
        int timeColor = !this.paused && this.timeLeft < 20 * 20 && this.timeLeft % 10 < 5 ? 0xFFFFFF : color;

        if(this.paused && ClientScheduler.getTick() % 80 < 6) {
            context.drawText(text, Text.empty()
                    .append(Text.literal(" "))
                    .append(Text.literal(time).setStyle(Style.EMPTY.withColor(timeColor)).formatted(Formatting.OBFUSCATED)), 0, 0, color, true);
        } else {
            context.drawText(text, Text.empty()
                    .append(Text.literal(" "))
                    .append(Text.literal(time).setStyle(Style.EMPTY.withColor(timeColor))), 0, 0, color, true);
        }

        context.getMatrices().pop();
        context.getMatrices().pop();

        context.getMatrices().push();
        context.getMatrices().scale(2.7F, 2.7F, 2.7F);
        context.getMatrices().translate(10.0D, 10.0D, 0.0D);

        RenderablePokemon renderablePokemon = null;

        if(pokemon != null) {
            renderablePokemon = pokemon.asRenderable();
        } else if(!PokemonStarterData.CLIENT.getStarters().isEmpty()) {
            List<StarterPokemon> starters = new ArrayList<>(PokemonStarterData.CLIENT.getStarters());
            starters.removeIf(starter -> PokemonStarterData.CLIENT.getRemainingAllocations(starter.getId()) <= 0);
            renderablePokemon = starters.get((int)ClientScheduler.getTick() / 40 % starters.size()).asRenderable();
        }

        if(renderablePokemon != null) {
            ModelWidget widget = new ModelWidget(0, 0, 0, 0,
                    renderablePokemon, 1.0F, -22.0F, 0.0F);
            widget.render(context, 0, 0, delta);
        }

        context.getMatrices().pop();
    }

    public String formatTimeString(long remainingTicks) {
        long seconds = (remainingTicks / 20) % 60;
        long minutes = ((remainingTicks / 20) / 60) % 60;
        long hours = ((remainingTicks / 20) / 60) / 60;
        return hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

}
