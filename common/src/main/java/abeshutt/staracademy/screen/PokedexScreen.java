package abeshutt.staracademy.screen;

import com.bedrockk.molang.runtime.MoLangRuntime;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.cobblemon.mod.common.api.pokedex.Dexes;
import com.cobblemon.mod.common.api.pokedex.SeenCount;
import com.cobblemon.mod.common.api.pokedex.def.PokedexDef;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.api.pokedex.filter.EntryFilter;
import com.cobblemon.mod.common.api.pokedex.filter.SearchByType;
import com.cobblemon.mod.common.api.pokedex.filter.SearchFilter;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.text.TextKt;
import com.cobblemon.mod.common.client.ClientMoLangFunctions;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.CobblemonRenderable;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.gui.pokedex.widgets.*;
import com.cobblemon.mod.common.client.pokedex.PokedexType;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.net.messages.server.block.AdjustBlockEntityViewerCountPacket;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.stream.Streams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.*;
import static com.cobblemon.mod.common.util.LocalizationUtilsKt.lang;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class PokedexScreen extends Screen implements CobblemonRenderable {

    private static final Identifier BACKGROUND = cobblemonResource("textures/gui/pokedex/pokedex_screen.png");
    private static final Identifier GLOBE_ICON = cobblemonResource("textures/gui/pokedex/globe_icon.png");
    private static final Identifier CAUGHT_SEEN_ICON = cobblemonResource("textures/gui/pokedex/caught_seen_icon.png");
    private static final Identifier ARROW_UP_ICON = cobblemonResource("textures/gui/pokedex/arrow_up.png");
    private static final Identifier ARROW_DOWN_ICON = cobblemonResource("textures/gui/pokedex/arrow_down.png");
    private static final Identifier TOOLTIP_EDGE = cobblemonResource("textures/gui/pokedex/tooltip_edge.png");
    private static final Identifier TOOLTIP_BACKGROUND = cobblemonResource("textures/gui/pokedex/tooltip_background.png");
    private static final Identifier TAB_SELECT_ARROW = cobblemonResource("textures/gui/pokedex/select_arrow.png");
    private static final Identifier[] TAB_ICONS = new Identifier[] {
            cobblemonResource("textures/gui/pokedex/tab_info.png"),
            cobblemonResource("textures/gui/pokedex/tab_abilities.png"),
            cobblemonResource("textures/gui/pokedex/tab_size.png"),
            cobblemonResource("textures/gui/pokedex/tab_stats.png"),
            cobblemonResource("textures/gui/pokedex/tab_drops.png")
    };

    private final AbstractPokedexManager pokedex;
    private final PokedexType type;
    private final Identifier initSpecies;
    private final BlockPos blockPos;

    private double oldDragPosX;
    private boolean canDragRender = false;

    private Collection<PokedexDef> filteredPokedex = new ArrayList<>();
    private String seenCount = "0000";
    private String ownedCount = "0000";

    private final MoLangRuntime runtime = MoLangFunctions.INSTANCE.setup(ClientMoLangFunctions.INSTANCE.setupClient(new MoLangRuntime()));
    private PokedexEntry selectedEntry;
    private PokedexForm selectedForm;

    private List<Identifier> availableRegions = new ArrayList<>();
    private int selectedRegionIndex = 0;

    private ScaledButton regionSelectWidgetUp;
    private ScaledButton regionSelectWidgetDown;
    private ScaledButton searchByTypeButton;
    private EntriesScrollingWidget scrollScreen;
    private InfoWidget pokemonInfoWidget;
    private SearchWidget searchWidget;

    private SearchByType selectedSearchByType = SearchByType.SPECIES;
    private final List<ScaledButton> tabButtons = new ArrayList<>();

    private Element tabInfoElement;
    private int tabInfoIndex = PokedexGUIConstants.TAB_DESCRIPTION;

    public PokedexScreen(AbstractPokedexManager pokedex, PokedexType type, Identifier initSpecies, BlockPos blockPos) {
        super(Text.translatable("cobblemon.ui.pokedex.title"));
        this.pokedex = pokedex;
        this.type = type;
        this.initSpecies = initSpecies;
        this.blockPos = blockPos;

        this.runtime.getEnvironment().query.addFunction("get_pokedex", params -> {
            return this.pokedex.getStruct();
        });
    }

    public static void open(AbstractPokedexManager manager, PokedexType type, Identifier species, BlockPos blockPos) {
        //MinecraftClient.getInstance().setScreen(new PokedexScreen(CobblemonClient.INSTANCE.getClientPokedexData(),
        //        type, species, blockPos));

        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(CobblemonSounds.POKEDEX_OPEN, 1.0F));
        MinecraftClient.getInstance().setScreen(new PokedexScreen(manager, type, species, blockPos));
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();

        this.availableRegions = new ArrayList<>(Dexes.INSTANCE.getDexEntryMap().keySet());
        this.selectedRegionIndex = 0;

        int ownedAmount = this.pokedex.getDexCalculatedValue(cobblemonResource("national"), CaughtCount.INSTANCE);
        this.ownedCount = String.format("%04d", ownedAmount);

        int seenAmount = this.pokedex.getDexCalculatedValue(cobblemonResource("national"), SeenCount.INSTANCE);
        this.seenCount = String.format("%04d", seenAmount);

        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if(this.pokemonInfoWidget != null) {
            this.remove(this.pokemonInfoWidget);
        }

        this.pokemonInfoWidget = new InfoWidget(this.pokedex, x + 180, y + 28, pokedexForm -> {
            this.updateSelectedForm(pokedexForm);
            return Unit.INSTANCE;
        });

        this.addDrawableChild(this.pokemonInfoWidget);

        this.setUpTabs();
        this.displaytabInfoElement(tabInfoIndex, false);

        if (this.searchWidget != null) {
            this.remove(this.searchWidget);
        }

        this.searchWidget = new SearchWidget(x + 26, y + 28, 128, PokedexGUIConstants.HEADER_BAR_HEIGHT,
                TextKt.text("Search"), () -> {
            this.updateFilters();
            return Unit.INSTANCE;
        });

        this.addDrawableChild(this.searchWidget);

        if(this.regionSelectWidgetUp != null) {
            this.remove(this.regionSelectWidgetUp);
        }

        this.regionSelectWidgetUp = new ScaledButton((float)(x + 95), (float)(y + 14.5), 8,
                6, ARROW_UP_ICON, PokedexGUIConstants.SCALE, false, button -> this.updatePokedexRegion(false));
        this.addDrawableChild(this.regionSelectWidgetUp);

        if(this.regionSelectWidgetDown != null) {
            this.remove(regionSelectWidgetDown);
        }

        this.regionSelectWidgetDown = new ScaledButton((float)(x + 95), (float)(y + 19.5), 8,
                6, ARROW_DOWN_ICON, PokedexGUIConstants.SCALE, false, button -> this.updatePokedexRegion(true));
        this.addDrawableChild(this.regionSelectWidgetDown);

        if(this.searchByTypeButton != null) {
            this.remove(this.searchByTypeButton);
        }

        this.searchByTypeButton = new ScaledButton((float)(x + 154.5), (float)(y + 29.5), 16, 16,
                cobblemonResource("textures/gui/pokedex/tab_" + selectedSearchByType.name().toLowerCase(Locale.ROOT) + ".png"),
                PokedexGUIConstants.SCALE, false,
                button -> {
                    List<SearchByType> types = Arrays.asList(SearchByType.values());
                    int index = types.indexOf(this.selectedSearchByType);
                    this.selectedSearchByType = types.get((index + 1) % types.size());
                    this.searchByTypeButton.setResource(cobblemonResource("textures/gui/pokedex/tab_" + selectedSearchByType.name().toLowerCase(Locale.ROOT) + ".png"));
                    this.updateFilters();
                });

        this.addDrawableChild(this.searchByTypeButton);
        this.updateFilters(true);
    }

    @Override
    protected void applyBlur(float delta) {

    }

    @Override
    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        this.renderBackground(context, mouseX, mouseY, delta);

        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        blitk(matrices, type.getTexturePath(), x, y, BASE_HEIGHT, BASE_WIDTH);
        blitk(matrices, BACKGROUND, x, y, BASE_HEIGHT, BASE_WIDTH);
        blitk(matrices, GLOBE_ICON, (x + 26) / SCALE, (y + 15) / SCALE, 14, 14,
                0, 0, 14, 14, 0, 1, 1, 1, 1,
                true, SCALE);

        // Region label
        RenderHelperKt.drawScaledText(context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                Text.translatable("cobblemon.ui.pokedex.region."
                        + availableRegions.get(selectedRegionIndex).getPath()).setStyle(Style.EMPTY.withBold(true)),
                x + 36,
                y + 14,
                1.0F,
                1.0F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                false,
                true,
                null,
                null
        );

        // Seen icon
        blitk(matrices, CAUGHT_SEEN_ICON, (x + 252) / SCALE, (y + 15) / SCALE, 14, 14,
                0, 0, 14, 28, 0, 1, 1, 1, 1,
                true, SCALE);

        // Caught icon
        blitk(matrices, CAUGHT_SEEN_ICON, (x + 290) / SCALE, (y + 15) / SCALE, 14, 14,
                0, 14, 14, 28, 0, 1, 1, 1, 1,
                true, SCALE);

        // Seen
        RenderHelperKt.drawScaledText(context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                TextKt.bold(TextKt.text(this.seenCount)),
                x + 262,
                y + 14,
                1.0F,
                1.0F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                false,
                true,
                null,
                null
        );

        // Owned
        RenderHelperKt.drawScaledText(context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                TextKt.bold(TextKt.text(this.ownedCount)),
                x = x + 300,
                y = y + 14,
                1.0F,
                1.0F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                false,
                true,
                null,
                null
        );

        // Show selected tab pointer if selected Pokémon has tab info to be shown
        if(this.selectedEntry != null && this.pokedex.getCaughtForms(this.selectedEntry).contains(this.selectedForm)) {
            // Tab arrow
            blitk(matrices, TAB_SELECT_ARROW,
                    (x + 198 + (25 * tabInfoIndex)) / SCALE,
                    // (x + 191.5 + (22 * tabInfoIndex)) / SCALE for 6 tabs
                    (y + 177) / SCALE, 6, 12,
                    0, 14, 12, 6, 0, 1, 1, 1, 1,
                    true, SCALE);
        }

        super.render(context, mouseX, mouseY, delta);

        // Search type tooltip
        if (searchByTypeButton.isButtonHovered(mouseX, mouseY)) {
            matrices.push();
            matrices.translate(0.0, 0.0, 1000.0);
            MutableText searchTypeText = lang("ui.pokedex.search.search_by", lang("ui.pokedex.search.type." + selectedSearchByType.name().toLowerCase()));
            searchTypeText.setStyle(searchTypeText.getStyle().withBold(true));
            int searchTypeTextWidth = MinecraftClient.getInstance().textRenderer.getWidth(searchTypeText.setStyle(searchTypeText.getStyle().withFont(CobblemonResources.INSTANCE.getDEFAULT_LARGE())));
            int tooltipWidth = searchTypeTextWidth + 6;

            blitk(matrices, TOOLTIP_EDGE, mouseX - (tooltipWidth / 2) - 1, mouseY - 16, 11, 1);
            blitk(matrices, TOOLTIP_BACKGROUND, mouseX - (tooltipWidth / 2), mouseY - 16, 11, tooltipWidth);
            blitk(matrices, TOOLTIP_EDGE, mouseX + (tooltipWidth / 2), mouseY - 16, 11, 1);
            RenderHelperKt.drawScaledText(context, CobblemonResources.INSTANCE.getDEFAULT_LARGE(), searchTypeText,
                    mouseX, mouseY - 15, 1.0F,
                    1.0F,
                    Integer.MAX_VALUE,
                    0xFFFFFFFF, true, true, null, null);

            matrices.pop();
        }
    }

    @Override
    public void close() {
        if(this.blockPos != null) {
            new AdjustBlockEntityViewerCountPacket(this.blockPos, false).sendToServer();
        }

        this.playSound(CobblemonSounds.POKEDEX_CLOSE);
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean canDisplayEntry = true;

        if(this.pokemonInfoWidget != null && this.pokemonInfoWidget.isWithinPortraitSpace(mouseX, mouseY) && canDisplayEntry) {
            this.canDragRender = true;
            this.setDragging(true);
            oldDragPosX = mouseX;
            this.playSound(CobblemonSounds.POKEDEX_CLICK_SHORT);
        }

        try {
            return super.mouseClicked(mouseX, mouseY, button);
        } catch(ConcurrentModificationException e) {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.canDragRender = false;
        this.setDragging(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isDragging() && canDragRender) {
            float dragOffsetY = (float)(oldDragPosX - mouseX);
            this.pokemonInfoWidget.rotationY = ((this.pokemonInfoWidget.rotationY + dragOffsetY) % 360 + 360) % 360;
        }

        this.oldDragPosX = mouseX;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void tick() {
        if(this.pokemonInfoWidget != null) {
            this.pokemonInfoWidget.tick();
        }
    }

    public void updatePokedexRegion(Boolean nextIndex) {
        if(nextIndex != null) {
            if(selectedRegionIndex < availableRegions.size() - 1) selectedRegionIndex++;
            else selectedRegionIndex = 0;
        } else {
            if(selectedRegionIndex > 0) selectedRegionIndex--;
            else selectedRegionIndex = availableRegions.size() - 1;
        }

        this.updateFilters();
    }

    public void updateFilters() {
        this.updateFilters(false);
    }

    public void updateFilters(Boolean init) {
        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        this.filteredPokedex = CollectionsKt.listOfNotNull(Dexes.INSTANCE.getDexEntryMap().get(this.availableRegions.get(this.selectedRegionIndex)));

        // Scroll Screen
        if(this.scrollScreen != null) {
            this.remove(this.scrollScreen);
        }

        this.scrollScreen = new EntriesScrollingWidget(this.pokedex, x + 26, y + 39,  pokedexEntry -> {
            PokedexScreen.this.setSelectedEntry(pokedexEntry);
            return Unit.INSTANCE;
        });

        Stream<PokedexEntry> entries = this.filteredPokedex.stream().flatMap(pokedexDef -> pokedexDef.getEntries().stream());

        if(Cobblemon.config.getHideUnimplementedPokemonInThePokedex()) {
            entries = entries.filter(entry -> {
                Species species = PokemonSpecies.INSTANCE.getByIdentifier(entry.getSpeciesId());
                return species != null && species.getImplemented();
            });
        }

        for(EntryFilter filter : this.getFilters()) {
            entries = entries.filter(filter::test);
        }

        List<PokedexEntry> entriesList = entries.collect(Collectors.toList());
        this.scrollScreen.createEntries(entriesList);
        this.addDrawableChild(this.scrollScreen);

        if(!entriesList.isEmpty()) {
            if(init && this.initSpecies != null) {
                var entry = entriesList.stream().filter(pokedexEntry -> pokedexEntry.getSpeciesId().equals(initSpecies)).findFirst().orElse(null);

                if(entry == null) {
                    entry = entriesList.getFirst();
                }

                this.setSelectedEntry(entry);
                this.scrollScreen.setScrollAmount(((double)entriesList.indexOf(entry) / (double)entriesList.size()) * this.scrollScreen.getMaxScroll());
            } else {
                this.setSelectedEntry(entriesList.getFirst());
            }
        }
    }

    public Collection<EntryFilter> getFilters() {
        List<EntryFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter(this.pokedex, this.searchWidget.getText(), this.selectedSearchByType));
        return filters;
    }

    public void setSelectedEntry(PokedexEntry newSelectedEntry) {
        this.selectedEntry = newSelectedEntry;
        List<PokedexForm> forms = this.pokedex.getEncounteredForms(newSelectedEntry);
        this.selectedForm = forms.isEmpty() ? null : forms.getFirst();
        this.pokemonInfoWidget.setDexEntry(this.selectedEntry);
        this.displaytabInfoElement(this.tabInfoIndex);
    }

    public void setUpTabs() {
        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if(!this.tabButtons.isEmpty()) {
            this.tabButtons.clear();
        }

        for(int i = 0; i < TAB_ICONS.length; i++) {
            int finalI = i;

            this.tabButtons.add(new ScaledButton(
                    x + 197F + (i * 25F), // x + 190.5F + (i * 22F) for 6 tabs
                    y + 181.5F,
                    TAB_ICON_SIZE,
                    TAB_ICON_SIZE,
                    TAB_ICONS[i],
                    0.5F,
                    false,
                    button -> {
                        if(this.canSelectTab(finalI)) {
                            this.displaytabInfoElement(finalI);
                        }
                    }
            ));
        }

        for(ScaledButton tab : this.tabButtons) {
            this.addDrawableChild(tab);
        }
    }

    public void displaytabInfoElement(Integer tabIndex) {
        this.displaytabInfoElement(tabIndex, true);
    }

    public void displaytabInfoElement(Integer tabIndex, Boolean update) {
        boolean showActiveTab = this.selectedEntry != null && this.pokedex.getCaughtForms(this.selectedEntry).contains(this.selectedForm);

        if(!this.tabButtons.isEmpty() && this.tabButtons.size() > tabIndex) {
            for(int i = 0; i < this.tabButtons.size(); i++) {
                ScaledButton tab = this.tabButtons.get(i);
                tab.setWidgetActive(showActiveTab && i == tabIndex);
            }
        }

        if(this.tabInfoIndex == TAB_ABILITIES && this.tabInfoElement instanceof AbilitiesWidget abilities) {
            this.remove(abilities.getLeftButton());
            this.remove(abilities.getRightButton());
        }

        this.tabInfoIndex = tabIndex;

        if(this.tabInfoElement != null) {
            this.remove(this.tabInfoElement);
        }

        int x = (this.width - BASE_WIDTH) / 2;
        int y = (this.height - BASE_HEIGHT) / 2;

        switch(tabIndex) {
            case TAB_DESCRIPTION -> this.tabInfoElement = new DescriptionWidget(x + 180, y + 135);
            case TAB_ABILITIES -> this.tabInfoElement = new AbilitiesWidget( x + 180, y + 135);
            case TAB_SIZE -> this.tabInfoElement = new SizeWidget( x + 180, y + 135);
            case TAB_STATS -> this.tabInfoElement = new StatsWidget( x + 180, y + 135);
            case TAB_DROPS -> this.tabInfoElement = new DropsScrollingWidget(x + 189, y + 135);
        }

        Element element = this.tabInfoElement;

        if(element instanceof Drawable && element instanceof Selectable) {
            this.addDrawableChild(cast(element));
        }

        if(update) {
            this.updateTabInfoElement();
        }
    }

    public void updateTabInfoElement() {
        Species species = this.selectedEntry != null ? PokemonSpecies.INSTANCE.getByIdentifier(this.selectedEntry.getSpeciesId()) : null;
        String formName = this.selectedForm != null ? this.selectedForm.getDisplayForm() : null;
        boolean canDisplay = this.selectedEntry != null && this.pokedex.getCaughtForms(this.selectedEntry).contains(this.selectedForm);

        List<String> textToShowInDescription = new ArrayList<>();

        if(canDisplay && species != null) {
            FormData form = species.getForms().stream()
                    .filter(f -> f.getName().equalsIgnoreCase(formName))
                    .findFirst()
                    .orElse(species.getStandardForm());

            switch(this.tabInfoIndex) {
                case TAB_DESCRIPTION -> {
                    textToShowInDescription.addAll(form.getPokedex());
                    ((DescriptionWidget)this.tabInfoElement).setShowPlaceholder(false);
                }
                case TAB_ABILITIES -> {
                    if(this.tabInfoElement instanceof AbilitiesWidget abilities) {
                        abilities.setAbilitiesList(Streams.of(form.getAbilities())
                                .sorted(Comparator.comparing(a -> !(a instanceof HiddenAbility)))
                                .map(PotentialAbility::getTemplate)
                                .collect(Collectors.toList()));

                        abilities.setSelectedAbilitiesIndex(0);
                        abilities.setAbility();
                        abilities.setScrollAmount(0.0D);

                        if(abilities.getAbilitiesList().size() > 1) {
                            this.addDrawableChild(abilities.getLeftButton());
                            this.addDrawableChild(abilities.getRightButton());
                        }
                    }
                }
                case TAB_SIZE -> {
                    if(this.pokemonInfoWidget != null && this.pokemonInfoWidget.renderablePokemon != null
                            && this.tabInfoElement instanceof SizeWidget size) {
                        size.setPokemonHeight(form.getHeight());
                        size.setWeight(form.getWeight());
                        size.setBaseScale(form.getBaseScale());
                        size.setRenderablePokemon(this.pokemonInfoWidget.renderablePokemon);
                    }
                }
                case TAB_STATS -> {
                    ((StatsWidget)this.tabInfoElement).setBaseStats(form.getBaseStats());
                }
                case TAB_DROPS -> {
                    ((DropsScrollingWidget)this.tabInfoElement).setDropTable(form.getDrops());
                    ((DropsScrollingWidget)this.tabInfoElement).setEntries();
                }
//              TAB_MOVES -> {
//                  form.moves.getLevelUpMovesUpTo(100)
//              }
            }
        } else {
            if(tabInfoIndex != TAB_DESCRIPTION) {
                this.displaytabInfoElement(TAB_DESCRIPTION);
            }

            ((DescriptionWidget)this.tabInfoElement).setShowPlaceholder(true);
        }

        if(this.tabInfoIndex == TAB_DESCRIPTION) {
            if (this.tabInfoElement instanceof DescriptionWidget description) {
                description.setText(textToShowInDescription);
                description.setScrollAmount(0.0D);
            }
        }
    }

    public void updateSelectedForm(PokedexForm newForm) {
        this.selectedForm = newForm;
        this.displaytabInfoElement(tabInfoIndex);
    }

    public boolean canSelectTab(Integer tabIndex) {
        if(this.selectedForm == null || this.selectedEntry == null) {
            return false;
        }

        boolean encounteredForm = this.pokedex.getEncounteredForms(this.selectedEntry).contains(this.selectedForm);
        return encounteredForm && (tabIndex != tabInfoIndex);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(MiscUtilsKt.isInventoryKeyPressed(this, MinecraftClient.getInstance(), keyCode, scanCode)
                && !(this.getFocused() instanceof SearchWidget)) {
            this.close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void playSound(SoundEvent soundEvent) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1.0F));
    }

    public <T extends Element & Drawable & Selectable> T cast(Object object) {
        return (T)object;
    }

}
