package abeshutt.staracademy.screen;

import com.bedrockk.molang.runtime.MoLangRuntime;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexCosmeticVariation;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.text.TextKt;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.client.ClientMoLangFunctions;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.gui.TypeIcon;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.aspects.PokemonAspectsKt;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.*;
import static com.cobblemon.mod.common.util.LocalizationUtilsKt.lang;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class InfoWidget extends SoundlessWidget {

    public static final float scaleAmount = 2F;
    public static final int portraitStartY = 25;

    public static final Identifier backgroundOverlay = cobblemonResource("textures/gui/pokedex/pokedex_screen_info_overlay.png");
    public static final Identifier pokeBallOverlay = cobblemonResource("textures/gui/pokedex/pokedex_screen_poke_ball.png");

    public static final Identifier platformUnknown = cobblemonResource("textures/gui/pokedex/platform_unknown.png");
    public static final Identifier platformBase = cobblemonResource("textures/gui/pokedex/platform_base.png");
    public static final Identifier platformShadow = cobblemonResource("textures/gui/pokedex/platform_shadow.png");

    public static final Identifier arrowFormLeft = cobblemonResource("textures/gui/pokedex/forms_arrow_left.png");
    public static final Identifier arrowFormRight = cobblemonResource("textures/gui/pokedex/forms_arrow_right.png");

    public static final Identifier caughtIcon = cobblemonResource("textures/gui/pokedex/caught_icon.png");
    public static final Identifier typeBar = cobblemonResource("textures/gui/pokedex/type_bar.png");
    public static final Identifier typeBarDouble = cobblemonResource("textures/gui/pokedex/type_bar_double.png");

    public static final Identifier buttonCryBase = cobblemonResource("textures/gui/pokedex/button_sound.png");
    public static final Identifier buttonCryArrow = cobblemonResource("textures/gui/pokedex/button_sound_arrow.png");
    public static final Identifier buttonAnimationBase = cobblemonResource("textures/gui/pokedex/button_animation.png");
    public static final Identifier buttonAnimationArrowLeft = cobblemonResource("textures/gui/pokedex/button_animation_arrow_left.png");
    public static final Identifier buttonAnimationArrowRight = cobblemonResource("textures/gui/pokedex/button_animation_arrow_right.png");

    public static final Identifier buttonGenderMale = cobblemonResource("textures/gui/pokedex/button_male.png");
    public static final Identifier buttonGenderFemale = cobblemonResource("textures/gui/pokedex/button_female.png");
    public static final Identifier buttonNone = cobblemonResource("textures/gui/pokedex/button_none.png");
    public static final Identifier buttonShiny = cobblemonResource("textures/gui/pokedex/button_shiny.png");

    public static final Identifier tooltipEdge = cobblemonResource("textures/gui/pokedex/tooltip_edge.png");
    public static final Identifier tooltipBackground = cobblemonResource("textures/gui/pokedex/tooltip_background.png");

    private AbstractPokedexManager pokedex;
    private int x, y;
    private final Function1<PokedexForm, Unit> updateForm;

    private PokedexEntry currentEntry;
    private MutableText speciesName = Text.translatable("");
    private Text speciesNumber = TextKt.text("0000");
    private final MoLangRuntime runtime = MoLangFunctions.INSTANCE.setup(ClientMoLangFunctions.INSTANCE.setupClient(new MoLangRuntime()));
    private List<PokedexForm> visibleForms = new ArrayList<>();
    private int selectedFormIndex = 0;
    private ElementalType[] type = {null, null};
    private List<String> seenShinyStates = new ArrayList<>();
    private boolean shiny = false;
    private float maleRatio = -1.0F;
    private Gender gender = Gender.GENDERLESS;
    public RenderablePokemon renderablePokemon;
    private List<PoseType> poseList = new ArrayList<>(Arrays.asList(PoseType.PROFILE, PoseType.WALK, PoseType.SLEEP));
    private int selectedPoseIndex = 0;
    private FloatingState state = new FloatingState();
    public float rotationY = 30.0F;
    private int ticksElapsed = 0;
    private int pokeBallBackgroundFrame = 0;

    public InfoWidget(AbstractPokedexManager pokedex, int x, int y, Function1<PokedexForm, Unit> updateForm) {
        super(x, y, POKEMON_PORTRAIT_WIDTH, POKEMON_PORTRAIT_HEIGHT, lang("ui.pokedex.pokemon_info"));
        this.pokedex = pokedex;
        this.x = x;
        this.y = y;
        this.updateForm = updateForm;

        this.runtime.getEnvironment().query.addFunction("get_pokedex", params -> {
            return this.pokedex.getStruct();
        });

        this.genderButton = new ScaledButton(x + 114F, y + 27F, 20, 20,
                buttonGenderMale, 0.5F, false, element -> {
                    if(maleRatio > 0 && maleRatio < 1) {
                        this.gender = this.gender == Gender.MALE ? Gender.FEMALE : Gender.MALE;
                    }

                    this.updateAspects();
                });

        this.addWidget(this.genderButton);

        this.shinyButton = new ScaledButton(x + 126F, y + 27F, 20, 20,
                buttonNone, 0.5F, false, element -> {
                    if(this.seenShinyStates.size() > 1) {
                        this.shiny = !this.shiny;
                        this.updateAspects();
                    }
                });

        this.addWidget(this.shinyButton);

        this.cryButton = new ScaledButton(x + 115F, y + 83F, 12, 12,
                buttonCryArrow, 0.5F, true, element -> {
            this.playCry();
        });

        this.addWidget(this.cryButton);

        this.formLeftButton = new ScaledButton(x + 18F, y + 55.5F, 10, 16,
                arrowFormLeft, 0.5F, false, element -> {
            this.switchForm(false);
        });

        this.addWidget(this.formLeftButton);

        this.formRightButton = new ScaledButton(x + 116F, y + 55.5F, 10, 16,
                arrowFormRight, 0.5F, false, element -> {
            this.switchForm(true);
        });

        this.addWidget(this.formRightButton);

        this.animationLeftButton = new ScaledButton(x + 3.5F, y + 83F, 12, 12,
                buttonAnimationArrowLeft, 0.5F, false, element -> {
            this.switchPose(false);
        });

        this.addWidget(this.animationLeftButton);

        this.animationRightButton = new ScaledButton(x + 18.5F, y + 83F, 12, 12,
                buttonAnimationArrowRight, 0.5F, false, element -> {
            this.switchPose(true);
        });

        this.addWidget(this.animationRightButton);
    }

    private final List<VariationButtonWrapper> variationButtons = new ArrayList<>();
    private ScaledButton genderButton;
    private ScaledButton shinyButton;
    private ScaledButton cryButton;
    private ScaledButton formLeftButton;
    private ScaledButton formRightButton;
    private ScaledButton animationLeftButton;
    private ScaledButton animationRightButton;

    public List<Pair<Float, Float>> possibleVariationButtonPositions = Arrays.asList(
            new Pair<>(126.0F, 27.0F),
            new Pair<>(114F, 27.0F),
            new Pair<>(102F, 27.0F),
            new Pair<>(90F, 27.0F),
            new Pair<>(78F, 27.0F),
            new Pair<>(66F, 27.0F),
            new Pair<>(54F, 27.0F),
            new Pair<>(42F, 27.0F),
            new Pair<>(30F, 27.0F)
    );

    public class VariationButtonWrapper {
        private final InfoWidget parent;
        private final float x;
        private final float y;

        private PokedexCosmeticVariation variation = new PokedexCosmeticVariation();
        private int buttonStateIndex = 0;
        private ScaledButton button;

        public VariationButtonWrapper(InfoWidget parent, float x, float y) {
            this.parent = parent;
            this.x = x;
            this.y = y;

            this.button = new ScaledButton(x, y, 20, 20, buttonNone,
                    0.5F, false, element -> this.click());
        }

        public ScaledButton getWidget() {
            return this.button;
        }

        public void show(PokedexCosmeticVariation variation) {
            this.variation = variation;
            this.button.setResource(variation.getIcon());
            this.buttonStateIndex = 0;
            this.button.visible = true;
            this.button.active = this.getPossibleAspects().size() > 1;
        }


        public List<String> getPossibleAspects() {
            return this.variation.getAspects().stream().filter(s -> {
                if(s.isEmpty()) return true;
                if(this.parent.currentEntry == null) return false;
                Identifier speciesId = this.parent.currentEntry.getSpeciesId();
                return this.parent.pokedex.getSpeciesRecord(speciesId).getAspects().contains(s);
            }).distinct().collect(Collectors.toList());
        }

        public int getMaxStateIndex() {
            return this.getPossibleAspects().size() - 1;
        }

        public String getAspect() {
            List<String> aspects = this.getPossibleAspects();

            if(this.buttonStateIndex < 0 || this.buttonStateIndex >= aspects.size()) {
                return null;
            }

            return aspects.get(this.buttonStateIndex);
        }

        public void hide() {
            this.button.visible = false;
        }

        public boolean isVisible() {
            return this.button.visible;
        }

        public void click() {
            this.buttonStateIndex++;

            if(this.buttonStateIndex > getMaxStateIndex()) {
                this.buttonStateIndex = 0;
            }

            this.parent.updateAspects();
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if(this.currentEntry == null) {
            return;
        }

        boolean hasKnowledge = this.pokedex.getKnowledgeForSpecies(this.currentEntry.getSpeciesId()) != PokedexEntryProgress.NONE;
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(currentEntry.getSpeciesId());

        if(species == null) {
            return;
        }

        MatrixStack matrices = context.getMatrices();

        blitk(matrices, backgroundOverlay, this.x, this.y, HALF_OVERLAY_HEIGHT, HALF_OVERLAY_WIDTH);

        blitk(matrices, pokeBallOverlay, this.x + 15, this.y + 25,
                PORTRAIT_POKE_BALL_HEIGHT, PORTRAIT_POKE_BALL_WIDTH, 0, (pokeBallBackgroundFrame * 109) + 20,
                1744);

        RenderHelperKt.drawScaledText(context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                TextKt.bold(this.speciesNumber.copy()),
                this.x + 3,
                this.y + 1,
                1.0F,
                1.0F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                false,
                true,
                null,
                null);

        if(hasKnowledge) {
            RenderHelperKt.drawScaledText(context,
                    CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                    TextKt.bold(this.speciesName.copy()),
                    this.x + 26,
                    this.y + 1,
                    1.0F,
                    1.0F,
                    Integer.MAX_VALUE,
                    0xFF606B6E,
                    false,
                    true,
                    null,
                    null);
        }

        // Caught icon
        if(this.isSelectedPokemonOwned()) {
            blitk(matrices, caughtIcon, (this.x + 129) / SCALE, (this.y + 2) / SCALE,
                    14, 14, 0, 0, 14, 14, 0,
                    1, 1, 1, 1, true, SCALE);
        }

        // Platform
        blitk(matrices, platformBase, this.x + 13, this.y + 69,
                24, 113, 0, 0, 113, 30);

        Identifier platformType = this.getPlatformResource();

        if(platformType != null && this.isSelectedPokemonOwned()) {
            blitk(matrices, platformType,
                    this.x + 13, this.y + 66, 27, 113,
                    0, 0, 113, 30);
        }

        blitk(matrices, platformShadow, (this.x + 47) / SCALE, (this.y + 76.5F) / SCALE,
                20, 90, 0, 0, 90, 20, 0,
                1, 1, 1, 1, true, SCALE);

        if(hasKnowledge && this.renderablePokemon != null) {
            context.enableScissor(this.x + 1, this.y + portraitStartY,
                    this.x + POKEMON_PORTRAIT_WIDTH + 1, this.y + portraitStartY + POKEMON_PORTRAIT_HEIGHT);

            matrices.push();

            matrices.translate(
                    (double)this.x + (POKEMON_PORTRAIT_WIDTH + 2.0D) / 2,
                    (double)this.y + portraitStartY - 12,
                    1000.0 // Prevent model from clipping into background
            );

            matrices.scale(scaleAmount, scaleAmount, scaleAmount);
            Vector3f rotationVector = new Vector3f(13F, this.rotationY, 0F);

            PokemonGuiUtilsKt.drawProfilePokemon(this.renderablePokemon, matrices,
                    QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), rotationVector),
                    this.poseList.get(this.selectedPoseIndex), this.state, delta, 20F,
                    true, false, 1F, 1F, 1F, 1F);

            matrices.pop();
            context.disableScissor();
        } else {
            // Render question mark
            blitk(matrices, platformUnknown, this.x + 50.5, this.y + 39, 45, 39);
        }

        // Ensure elements are not hidden behind Pokémon render
        matrices.push();
        matrices.translate(0.0, 0.0, 2000.0);

        if(this.isSelectedPokemonOwned()) {
            ElementalType primaryType = this.type[0];
            ElementalType secondaryType = this.type[1];

            blitk(matrices, secondaryType != null ? typeBarDouble : typeBar,
                    this.x, this.y + 14, 25, HALF_OVERLAY_WIDTH);

            if(primaryType != null) {
                new TypeIcon(this.x + 3, this.y + 17, primaryType, secondaryType, false, false,
                        15F, 7.5F, 1F).render(context);
            }
        } else {
            blitk(matrices, typeBar, this.x, this.y + 14, 25, HALF_OVERLAY_WIDTH);
        }

        if(hasKnowledge) {
            if(this.gender != Gender.GENDERLESS) {
                this.genderButton.render(context, mouseX, mouseY, delta);
            }

            this.shinyButton.render(context, mouseX, mouseY, delta);

            for(VariationButtonWrapper button : this.variationButtons) {
                button.getWidget().render(context, mouseX, mouseY, delta);

                // Tooltip
                if(button.isVisible() && button.getWidget().isButtonHovered(mouseX, mouseY)) {
                    MutableText variationText = TextKt.bold(MiscUtilsKt.asTranslated(
                            button.variation.getDisplayName()));

                    int variationTextWidth = MinecraftClient.getInstance().textRenderer.getWidth(TextKt
                            .font(variationText, CobblemonResources.INSTANCE.getDEFAULT_LARGE()));
                    int tooltipWidth = variationTextWidth + 6;

                    blitk(matrices, tooltipEdge, mouseX - (tooltipWidth / 2) - 1, mouseY + 8, 11, 1);
                    blitk(matrices, tooltipBackground, mouseX - (tooltipWidth / 2), mouseY + 8, 11, tooltipWidth);
                    blitk(matrices, tooltipEdge, mouseX + (tooltipWidth / 2), mouseY + 8, 11, 1);

                    RenderHelperKt.drawScaledText(context, CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                            variationText, mouseX, mouseY + 9, 1.0F, 1.0F,
                            Integer.MAX_VALUE, 0xFFFFFFFF,
                            true, true, null, null);
                }
            }

            // Forms
            List<PokedexForm> showableForms = this.pokedex.getEncounteredForms(this.currentEntry);

            if(showableForms.size() > 1 && showableForms.size() > this.selectedFormIndex) {
                this.formLeftButton.render(context,mouseX, mouseY, delta);
                this.formRightButton.render(context,mouseX, mouseY, delta);

                PokedexForm form = showableForms.get(this.selectedFormIndex);

                RenderHelperKt.drawScaledTextJustifiedRight(context, CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                        TextKt.bold(lang("ui.pokedex.info.form." + form.getDisplayForm().toLowerCase())),
                        this.x + 136, this.y + 15, 1F, 1F, Integer.MAX_VALUE,
                        0xFFFFFFFF, true);
            }

            // Cry
            blitk(matrices, buttonCryBase, (this.x + 114) / SCALE, (this.y + 81) / SCALE,
                    20, 44, 0, 0, 44, 20,
                    0, 1, 1, 1, 1, true, SCALE);

            this.cryButton.render(context, mouseX, mouseY, delta);

            // Animation
            blitk(matrices, buttonAnimationBase, (this.x + 3) / SCALE, (this.y + 81) / SCALE,
                    20, 44, 0, 0, 44, 20,
                    0, 1, 1, 1, 1, true, SCALE);

            this.animationLeftButton.render(context, mouseX, mouseY, delta);
            this.animationRightButton.render(context, mouseX, mouseY, delta);
        } else if(this.renderablePokemon == null) {
            // Render unimplemented label
            if (!species.getImplemented()) {
                RenderHelperKt.drawScaledTextJustifiedRight(context, CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                        TextKt.bold(lang("ui.pokedex.info.unimplemented")),
                        this.x + 136, this.y + 15, 1F, 1F, Integer.MAX_VALUE,
                        0xFFFFFFFF, true);
            }
        }

        matrices.pop();
    }

    public void setDexEntry(PokedexEntry pokedexEntry) {
        this.currentEntry = pokedexEntry;
        this.renderablePokemon = null;
        this.selectedPoseIndex = 0;

        Species species = PokemonSpecies.INSTANCE.getByIdentifier(pokedexEntry.getSpeciesId());
        List<PokedexForm> forms = pokedexEntry.getForms();

        if(species != null) {
            this.visibleForms = new ArrayList<>(this.pokedex.getEncounteredForms(pokedexEntry));
            this.speciesNumber = TextKt.text(StringsKt.padStart(
                    species.getNationalPokedexNumber() + "", 4, '0'));
            this.speciesName = species.getTranslatedName();

            if(!forms.isEmpty()) {
                this.formLeftButton.active = true;
                this.formRightButton.active = true;
            } else {
                this.formLeftButton.active = false;
                this.formRightButton.active = false;
            }

            this.selectedFormIndex = 0;

            if(!this.visibleForms.isEmpty()) {
                this.setupButtons(pokedexEntry, this.visibleForms.get(this.selectedFormIndex));
                this.updateAspects();
            } else {
                this.setupButtons(pokedexEntry, forms.get(this.selectedFormIndex));
                this.type = new ElementalType[] {null, null};
            }
        }
    }

    private void setupButtons(PokedexEntry pokedexEntry, PokedexForm pokedexForm) {
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(pokedexEntry.getSpeciesId());

        if(species == null) {
            return;
        }

        for(VariationButtonWrapper button : this.variationButtons) {
            this.removeWidget(button.getWidget());
        }

        this.variationButtons.clear();

        this.seenShinyStates = new ArrayList<>(this.pokedex.getSeenShinyStates(pokedexEntry, pokedexForm));
        this.shiny = this.seenShinyStates.size() == 1 && seenShinyStates.getFirst().equals("shiny");

        this.shinyButton.setResource(this.shiny ? buttonShiny : buttonNone);
        this.shinyButton.visible = shiny || seenShinyStates.size() > 1;
        this.shinyButton.active = seenShinyStates.size() > 1;

        FormData form = species.getForms().stream().filter(data -> {
            return data.getName().equalsIgnoreCase(pokedexForm.getDisplayForm());
        }).findFirst().orElseGet(species::getStandardForm);

        this.maleRatio = form.getMaleRatio();

        List<Gender> seenGenders = new ArrayList<>(this.pokedex.getSeenGenders(pokedexEntry, pokedexForm));

        if(seenGenders.isEmpty()) {
            this.genderButton.visible = false;
            this.genderButton.active = false;
        } else {
            this.gender = seenGenders.getFirst();
            this.genderButton.visible = true;
            this.genderButton.active = seenGenders.size() > 1;
        }

        this.genderButton.setButtonX(this.x + (this.shinyButton.visible ? 114F : 126F));

        if(this.pokedex.getHighestKnowledgeFor(pokedexEntry) == PokedexEntryProgress.NONE) {
            return;
        }

        int startPosition = this.shinyButton.visible ? 1 : 0;
        startPosition += (this.genderButton.visible || species.getMaleRatio() == -1F) ? 1 : 0;

        for(int i = 0; i < pokedexEntry.getVariations().size(); i++) {
            PokedexCosmeticVariation variation = pokedexEntry.getVariations().get(i);
            Pair<Float, Float> pos = this.possibleVariationButtonPositions.get(i + startPosition);
            VariationButtonWrapper button = new VariationButtonWrapper(this, this.x + pos.getFirst(), this.y + pos.getSecond());
            button.show(variation);
            addWidget(button.getWidget());
            this.variationButtons.add(button);
        }
    }

    private void updateType(Species species, FormData form) {
        this.type = new ElementalType[] { form.getPrimaryType(), form.getSecondaryType() };
    }

    private void playCry() {
        this.state.getActiveAnimations().clear();
        this.state.addFirstAnimation(new HashSet<>(List.of("cry")));
    }

    private void switchForm(boolean nextIndex) {
        this.selectedPoseIndex = 0;

        if(nextIndex) {
            if(this.selectedFormIndex < this.visibleForms.size() - 1) {
                this.selectedFormIndex++;
            } else {
                this.selectedFormIndex = 0;
            }
        } else if(this.selectedFormIndex > 0) {
            this.selectedFormIndex--;
        } else {
            selectedFormIndex = visibleForms.size() - 1;
        }

        this.setupButtons(this.currentEntry, this.visibleForms.get(this.selectedFormIndex));
        this.updateAspects();
    }

    private void switchPose(boolean nextIndex) {
        if(nextIndex) {
            if(this.selectedPoseIndex < this.poseList.size() - 1) {
                this.selectedPoseIndex++;
            } else {
                this.selectedPoseIndex = 0;
            }
        } else if(this.selectedPoseIndex > 0) {
            this.selectedPoseIndex--;
        } else {
            this.selectedPoseIndex = this.poseList.size() - 1;
        }

        this.updateAspects();
    }

    public void updateAspects() {
        if(this.visibleForms.isEmpty()) {
            return;
        }

        genderButton.setResource(this.gender == Gender.FEMALE ? buttonGenderFemale : buttonGenderMale);
        shinyButton.setResource(this.shiny ? buttonShiny : buttonNone);

        Species species = this.currentEntry != null ? PokemonSpecies.INSTANCE
                .getByIdentifier(this.currentEntry.getSpeciesId()) : null;

        if(species != null) {
            String formName = this.visibleForms.get(this.selectedFormIndex).getDisplayForm();
            FormData form = species.getForms().stream()
                    .filter(data -> data.getName().equalsIgnoreCase(formName))
                    .findFirst().orElseGet(species::getStandardForm);

            this.updateType(species, form);

            Set<String> aspects = new HashSet<>();

            if(this.shiny) {
                aspects.add(PokemonAspectsKt.getSHINY_ASPECT().getAspect());
            }

            if(this.gender == Gender.FEMALE) {
                aspects.add("female");
            } else if(this.gender == Gender.MALE) {
                aspects.add("male");
            }

            aspects.addAll(form.getAspects());
            aspects.addAll(this.variationButtons.stream().filter(VariationButtonWrapper::isVisible)
                    .map(VariationButtonWrapper::getAspect).toList());

            this.renderablePokemon = new RenderablePokemon(species, aspects);
            this.recalculatePoses(this.renderablePokemon);
            this.updateForm.invoke(this.visibleForms.get(this.selectedFormIndex));
        }
    }

    public void recalculatePoses(RenderablePokemon renderablePokemon) {
        FloatingState state = new FloatingState();
        state.setCurrentAspects(renderablePokemon.getAspects());

        PokemonPosableModel poser = PokemonModelRepository.INSTANCE.getPoser(renderablePokemon.getSpecies().resourceIdentifier, state);
        state.setCurrentModel(poser);

        this.poseList = poser.getPoses().values().stream()
                .map(pose -> Collections.min(pose.getPoseTypes(), Comparator.comparingInt(Enum::ordinal)))
                .distinct()
                .filter(type -> !PoseType.Companion.getSHOULDER_POSES().contains(type)) // Those don't play so goodly ykwim
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .collect(Collectors.toList());
    }

    public Identifier getPlatformResource() {
        ElementalType primaryType = this.type[0];

        if(primaryType != null) {
            try {
                return cobblemonResource("textures/gui/pokedex/platform_base_" + primaryType.getName() + ".png");
            } catch(Exception error) {
                return null;
            }
        }

        return null;
    }

    public void tick() {
        this.ticksElapsed++;

        // Calculate animation frame
        int delay = 3;

        if(this.ticksElapsed % delay == 0) {
            this.pokeBallBackgroundFrame++;
        }

        if(this.pokeBallBackgroundFrame == 16) {
            this.pokeBallBackgroundFrame = 0;
        }
    }

    public boolean isWithinPortraitSpace(double mouseX, double mouseY) {
        return mouseX >= this.x + 15 && mouseX <= this.x + 15 + PORTRAIT_POKE_BALL_WIDTH
                && mouseY >= this.y + 25 && mouseY <= this.y + 25 + PORTRAIT_POKE_BALL_HEIGHT
                && this.getChildren().stream().anyMatch(element -> {
                        return element.isMouseOver(mouseX, mouseY) && element instanceof ScaledButton;
                    });
    }

    private boolean isSelectedPokemonOwned() {
        if(this.currentEntry == null) {
            return false;
        }

        return this.pokedex.getKnowledgeForSpecies(this.currentEntry.getSpeciesId()) == PokedexEntryProgress.CAUGHT;
    }

    private void playSound(SoundEvent soundEvent) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1.0F));
    }

}
