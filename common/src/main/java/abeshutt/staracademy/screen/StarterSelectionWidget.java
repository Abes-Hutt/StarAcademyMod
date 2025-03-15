package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
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

public class StarterSelectionWidget implements Drawable {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/starter_selection.png");

    private final Species species;
    private final Long timeLeft;
    private final boolean paused;

    public StarterSelectionWidget(Species species, long timeLeft, boolean paused) {
        this.species = species;
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

        String title = this.species != null ? this.species.getName() : "Raffle";
        int color = this.species != null ? this.species.getPrimaryType().getHue() : Formatting.GRAY.getColorValue();

        context.getMatrices().push();
        context.getMatrices().translate(x + 57.0D, y + 1.5D, 0.0D);

        MutableText description;

        if(this.species != null) {
            description = Text.translatable(this.species.getForm(new HashSet<>()).getPokedex().get(0));
        } else {
            description = Text.empty()
                .append(Text.literal("Press ["))
                .append(CobblemonKeyBinds.INSTANCE.getSUMMARY().getBoundKeyLocalizedText())
                .append(Text.literal("] to choose your starter Pokémon. If another player picks the same " +
                        "Pokémon as you, neither of you will receive it"));

            int cooldown = PokemonStarterData.CLIENT.getSelectionCooldown();

            if(cooldown > 1) {
                description = description.append(", and you won’t be able to choose that Pokémon for the next %d rounds".formatted(cooldown));
            } else if(cooldown == 1) {
                description = description.append(", and you won’t be able to choose that Pokémon for the next round");
            }

            description = description.append(Text.literal(". Choose wisely!"));
        }

        List<OrderedText> lines = text.wrapLines(description, 172);

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

        Species renderSpecies = this.getDisplaySpecies();

        if(renderSpecies != null) {
            ModelWidget widget = new ModelWidget(0, 0, 0, 0,
                    new RenderablePokemon(renderSpecies, new HashSet<>()),
                    1.0F, -22.0F, 0.0F);

            widget.render(context, 0, 0, delta);
        }

        context.getMatrices().pop();
    }

    public Species getDisplaySpecies() {
        Species species = this.species;

        if(species == null) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player == null) return null;
            StarterEntry entry = PokemonStarterData.CLIENT.getEntries().get(player.getUuid());
            if(entry == null) return null;
            List<Identifier> starters = new ArrayList<>(PokemonStarterData.CLIENT.getStarters());
            starters.removeIf(entry::isOnCooldown);

            while(species == null && !starters.isEmpty()) {
                Identifier speciesId = starters.get((int)ClientScheduler.getTick() / 40 % starters.size());
                species = speciesId == null ? null : PokemonSpecies.INSTANCE.getByIdentifier(speciesId);
            }
        }

        return species;
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
