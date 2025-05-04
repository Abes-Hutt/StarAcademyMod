package abeshutt.staracademy.screen;

import com.bedrockk.molang.runtime.MoLangRuntime;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.text.TextKt;
import com.cobblemon.mod.common.client.ClientMoLangFunctions;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.*;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class EntriesScrollingWidget extends ScrollingWidget<EntriesScrollingWidget.PokemonScrollSlotRow> {

    private AbstractPokedexManager pokedex;
    private int x;
    private int y;
    private Function1<PokedexEntry, Unit> setPokedexEntry;

    public EntriesScrollingWidget(AbstractPokedexManager pokedex, int x, int y, Function1<PokedexEntry, Unit> setPokedexEntry) {
        super(y - SCROLL_BASE_HEIGHT, x, PokedexGUIConstants.HALF_OVERLAY_WIDTH, SCROLL_BASE_HEIGHT,
                SCROLL_SLOT_SIZE + 2, 5);
        this.pokedex = pokedex;
        this.x = x;
        this.y = y;
        this.setPokedexEntry = setPokedexEntry;
    }

    public void createEntries(Collection<PokedexEntry> filteredPokedex) {
        List<List<PokedexEntry>> collection = CollectionsKt.chunked(filteredPokedex, 5);

        for(List<PokedexEntry> listChunk : collection) {
            List<PokedexEntryProgress> discoveryList = listChunk.stream()
                    .map(this.pokedex::getHighestKnowledgeFor)
                    .collect(Collectors.toList());

            PokemonScrollSlotRow newEntry = new PokemonScrollSlotRow(this.pokedex, listChunk,
                    discoveryList, this.setPokedexEntry);
            this.addEntry(newEntry);
        }
    }

    @Override
    public int addEntry(PokemonScrollSlotRow entry) {
        return super.addEntry(entry);
    }

    @Override
    protected int getScrollbarX() {
        return this.x + this.width - 3;
    }

    @Override
    public void renderScrollbar(DrawContext context, int mouseX, int mouseY, float delta) {
        int xLeft = this.getScrollbarX();
        int xRight = xLeft + 3;

        int barHeight = this.getBottom() - this.y;

        var yBottom = (int)(((float)barHeight * barHeight) / this.getMaxPosition());
        yBottom = MathHelper.clamp(yBottom, 32, barHeight - 8);
        var yTop = (int)this.getScrollAmount() * (barHeight - yBottom) / this.getMaxScroll() + this.y;

        if(yTop < this.y) {
            yTop = this.y;
        }

        context.fill(xLeft, this.y + 3, xRight, this.getBottom() - 3,
                ColorHelper.Argb.getArgb(255, 58, 150, 182)); // background
        context.fill(xLeft,yTop + 3, xRight, yTop + yBottom - 3,
                ColorHelper.Argb.getArgb(255, 252, 252, 252)); // base
    }

    @Override
    public void renderItem(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        PokemonScrollSlotRow entry = this.getEntry(index);
        entry.x = x;
        entry.y = y;
        entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, this.getFocused() == entry, delta);
    }

    @Override
    public PokemonScrollSlotRow getEntry(int index) {
        return this.children().get(index);
    }

    public static class PokemonScrollSlotRow extends Slot<PokemonScrollSlotRow> {

        private static final Identifier slotResource = cobblemonResource("textures/gui/pokedex/pokedex_slot.png");
        private static final Identifier slotHighlight = cobblemonResource("textures/gui/pokedex/slot_select.png");
        private static final Identifier caughtIcon = cobblemonResource("textures/gui/pokedex/caught_icon_small.png");
        private static final Identifier unknownIcon = cobblemonResource("textures/gui/pokedex/pokedex_slot_unknown.png");
        private static final Identifier unimplementedIcon = cobblemonResource("textures/gui/pokedex/pokedex_slot_unimplemented.png");

        private final AbstractPokedexManager pokedex;
        private final List<PokedexEntry> dexDataList;
        private final List<PokedexEntryProgress> discoveryLevelList;
        private final Function1<PokedexEntry, Unit> setPokedexEntry;

        private final MoLangRuntime runtime = MoLangFunctions.INSTANCE.setup(ClientMoLangFunctions.INSTANCE.setupClient(new MoLangRuntime()));
        private int x = 0;
        private int y = 0;

        public PokemonScrollSlotRow(AbstractPokedexManager pokedex, List<PokedexEntry> dexDataList,
                                    List<PokedexEntryProgress> discoveryLevelList, Function1<PokedexEntry, Unit> setPokedexEntry) {
            this.pokedex = pokedex;
            this.dexDataList = dexDataList;
            this.discoveryLevelList = discoveryLevelList;
            this.setPokedexEntry = setPokedexEntry;

            this.runtime.getEnvironment().query.addFunction("get_pokedex", params -> {
                return this.pokedex.getStruct();
            });
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                int mouseX, int mouseY, boolean hovered, float tickDelta) {
            for(int i = 0; i < this.dexDataList.size(); i++) {
                PokedexEntry dexData = this.dexDataList.get(i);
                FloatingState state = new FloatingState();

                Species species = PokemonSpecies.INSTANCE.getByIdentifier(dexData.getSpeciesId());
                //FIXME: This may not work properly when accounting for custom pokemon with the same dex number
                String pokemonNumber = species != null ? species.getNationalPokedexNumber() + "" : "0";
                pokemonNumber = StringsKt.padStart(pokemonNumber, 4, '0');

                MutableText speciesNumber = TextKt.text(pokemonNumber);
                PokedexEntryProgress discoveryLevel = this.discoveryLevelList.get(i);
                List<PokedexForm> forms = this.pokedex.getEncounteredForms(dexData);
                PokedexForm firstVisibleForm = forms.isEmpty() ? null : forms.getFirst();
                boolean shouldDrawMon = firstVisibleForm != null;

                if (species == null) {
                    continue;
                }

                MatrixStack matrices = context.getMatrices();

                int startPosX = x + ((SCROLL_SLOT_SPACING + SCROLL_SLOT_SIZE) * i);
                int startPosY = y + SCROLL_SLOT_SPACING + 1;

                blitk(matrices, slotResource, startPosX, startPosY,
                        SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE);

                if(this.getHoveredSlotIndex(mouseX, mouseY) == i) {
                    blitk(matrices, slotHighlight, startPosX, startPosY,
                            SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE, 0, SCROLL_SLOT_SIZE,
                            SCROLL_SLOT_SIZE, SCROLL_SLOT_SIZE * 2);
                }

                if(shouldDrawMon) {
                    List<Gender> genders = new ArrayList<>(this.pokedex.getSeenGenders(dexData, firstVisibleForm));
                    Gender firstVisibleGender = genders.isEmpty() ? null : genders.getFirst();
                    List<String> shinyStates = new ArrayList<>(this.pokedex.getSeenShinyStates(dexData, firstVisibleForm));
                    boolean firstVisibleShiny = shinyStates.size() == 1 && shinyStates.getFirst().equals("shiny");

                    List<String> formAspects = species.getForms().stream()
                            .filter(form -> form.getName().equalsIgnoreCase(firstVisibleForm.getDisplayForm()))
                            .findFirst()
                            .orElseGet(species::getStandardForm)
                            .getAspects();

                    Set<String> seenAspects = this.pokedex.getSeenAspects(dexData);

                    List<String> variationAspects = dexData.getVariations().stream().map(var -> {
                        return var.getAspects().stream().filter(seenAspects::contains).findFirst().orElse(null);
                    }).filter(Objects::nonNull).toList();

                    context.enableScissor(startPosX + 1, startPosY + 1,
                            startPosX + SCROLL_SLOT_SIZE - 1, startPosY + SCROLL_SLOT_SIZE - 2);

                    List<String> aspectsToDraw = new ArrayList<>();
                    aspectsToDraw.addAll(dexData.getDisplayAspects());
                    aspectsToDraw.addAll(variationAspects);
                    aspectsToDraw.addAll(formAspects);
                    aspectsToDraw.add((firstVisibleGender != null ? firstVisibleGender : Gender.GENDERLESS).name().toLowerCase());

                    if(firstVisibleShiny) {
                        aspectsToDraw.add("shiny");
                    }

                    matrices.push();
                    matrices.translate(startPosX + (SCROLL_SLOT_SIZE / 2.0), startPosY + 1.0, 0.0);
                    matrices.scale(2.5F, 2.5F, 1F);

                    PokemonGuiUtilsKt.drawProfilePokemon(
                            new RenderablePokemon(species, new LinkedHashSet<>(aspectsToDraw)), matrices,
                            QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, 35F, 0F)),
                            PoseType.PROFILE, state, 0F, 4.5F,
                            true, false, 1F, 1F, 1F, 1F);

                    matrices.pop();
                    context.disableScissor();
                } else {
                    blitk(matrices, unknownIcon, startPosX + 8.5, startPosY + 9, 10, 8);

                    if(!species.getImplemented()) {
                        blitk(matrices, unimplementedIcon,
                                (startPosX + 14) / SCALE, (startPosY + 15.5) / SCALE,
                                7, 7, 0, 0, 7, 7,
                                0, 1, 1, 1, 1, true, SCALE);
                    }
                }

                // Ensure elements are not hidden behind Pokémon render
                matrices.push();
                matrices.translate(0.0, 0.0, 100.0);

                RenderHelperKt.drawScaledText(context,
                        null, speciesNumber,
                        startPosX + 1.5,//2,
                        startPosY + 2.5,//2
                        SCALE, 1F, Integer.MAX_VALUE, 0xFFFFFFFF,
                        false, true, null, null);

                if(discoveryLevel == PokedexEntryProgress.CAUGHT) {
                    blitk(matrices, caughtIcon, (startPosX + 18) / SCALE, (startPosY + 1.5) / SCALE,
                            11, 11, 0, 0, 11, 11, 0,
                            1, 1, 1, 1, true, SCALE);
                }

                matrices.pop();
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int hoverIndex = this.getHoveredSlotIndex((int)mouseX, (int)mouseY);

            if(hoverIndex > -1 && hoverIndex < this.dexDataList.size()) {
                this.setPokedexEntry.invoke(this.dexDataList.get(hoverIndex));
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(CobblemonSounds.POKEDEX_CLICK, 1.0F));
            }

            return true;
        }

        private int getHoveredSlotIndex(int mouseX, int mouseY) {
            for(int i = 0; i < this.dexDataList.size(); i++) {
                int startPosX = this.x + ((SCROLL_SLOT_SPACING + SCROLL_SLOT_SIZE) * i);
                int startPosY = this.y + SCROLL_SLOT_SPACING + 1;

                if(mouseX >= startPosX && mouseX <= startPosX + SCROLL_SLOT_SIZE
                        && mouseY >= startPosY && mouseY <= startPosY + SCROLL_SLOT_SIZE) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public Text getNarration() {
            if(!this.dexDataList.isEmpty()) {
                return TextKt.text(this.dexDataList.getFirst() + "-" + this.dexDataList.getLast());
            }

            return TextKt.text("");
        }
    }

}
