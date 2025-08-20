package abeshutt.staracademy.entity;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.INbtSerializable;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.util.TextUtils;
import abeshutt.staracademy.world.data.PartnerData;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static net.minecraft.text.ClickEvent.Action.RUN_COMMAND;

public class PartnerNPCConversation implements INbtSerializable<NbtCompound> {

    public static final Function<RandomSource, String> INITIAL_GREETING = create(
        "Ah, a new trainer! Welcome to the academy, ${player}. I’m ${npc}. How wonderful to see a new fresh, eager face!"
    );

    public static final Function<RandomSource, String> SUBSEQUENT_GREETING = create(
        "Hello, ${player}!"
    );

    public static final Function<RandomSource, String> PROMPT_ACTIONS = create(
        "How can I help you?"
    );

    public static final Function<RandomSource, String> PARTNER_SUMMON = create(
        "Please summon the Pokemon you wish to partner with by throwing it on the ground.",
        "To set your partner, simply call out your chosen Pokemon and place it here."
    );

    public static final Function<RandomSource, String> PARTNER_SUMMON_EXPLANATION = create(
        "Once summoned, I'll ensure it's exclusively yours for battles.",
        "Once summoned, it becomes your trusted ally, unavailable for others to use in battles."
    );

    public static final Function<RandomSource, String> PARTNER_SUMMON_CONFIRMATION = create(
        "Are you certain you want to select ${pokemon} as your partner?",
        "You've chosen ${pokemon}! Would you like to confirm it as your partner?",
        "Do you want to make ${pokemon} your official partner?",
        "You've selected ${pokemon} as your partner. Do you want to proceed with this choice?"
    );

    public static final Function<RandomSource, String> PARTNER_SUMMON_SUCCESS = create(
        "Excellent choice! ${pokemon} will be your steadfast companion. Together, you'll make an unstoppable team!",
        "An intriguing selection. ${pokemon} possesses unique abilities that will aid your research.",
        "Excellent choice! ${pokemon} does align well with the Academy's standards.",
        "Your partnership with ${pokemon} is now official. May your bond lead you to remarkable discoveries and victories."
    );

    public static final Function<RandomSource, String> PARTNER_SUMMON_ALREADY_SET = create(
        "It looks like you've already chosen ${pokemon} as your partner."
    );

    private final UUID uuid;
    private Phase phase;
    private boolean initial;
    private Species selectedSpecies;

    public PartnerNPCConversation(UUID uuid) {
        this.uuid = uuid;
        this.phase = Phase.NONE;
        this.initial = true;
    }

    public void onTick(ServerPlayerEntity player, PartnerNPCEntity npc) {
        PartnerData data = ModWorldData.PARTNER.getGlobal(player.getWorld());

        if(this.phase == Phase.AWAIT_PARTNER_SUMMON && data.getSelection(this.uuid).isPresent()) {
            this.phase = Phase.GREETING;
        }

        if(player.distanceTo(npc) > 16.0D) {
            this.phase = Phase.NONE;
        }
    }

    public void onPokemonSent(ServerPlayerEntity player, PartnerNPCEntity npc, PokemonSentPostEvent event) {
        if(this.phase != Phase.AWAIT_PARTNER_SUMMON) {
            return;
        }

        this.selectedSpecies = event.getPokemon().getSpecies();
        RandomSource random = JavaRandom.ofNanoTime();

        MutableText text = Text.empty();
        text = text.append(Text.literal(PARTNER_SUMMON_CONFIRMATION.apply(random)).formatted(Formatting.GRAY));
        text = this.format(text, player, null, this.selectedSpecies);
        text = text.append(" ");
        text = text.append(Text.literal("[Yes]").formatted(Formatting.GREEN)
                .styled(style -> style.withClickEvent(new ClickEvent(RUN_COMMAND,
                        "/academy partner " + npc.getUuid() + " confirm_partner_selection"))));
        player.sendMessage(text, false);
    }

    public void onInteract(ServerPlayerEntity player, PartnerNPCEntity npc) {
        RandomSource random = JavaRandom.ofNanoTime();

        if(this.phase == Phase.NONE) {
            MutableText text = Text.empty();
            text = text.append(Text.literal((this.initial ? INITIAL_GREETING : SUBSEQUENT_GREETING).apply(random)).formatted(Formatting.GRAY));
            text = text.append(" ");
            text = text.append(Text.literal(PROMPT_ACTIONS.apply(random)).formatted(Formatting.GRAY));
            text = this.format(text, player, npc, null);

            text = text.append(" ");
            text = text.append(Text.literal("[Select Partner]").formatted(Formatting.AQUA)
                    .styled(style -> style.withClickEvent(new ClickEvent(RUN_COMMAND,
                            "/academy partner " + npc.getUuid() + " select_partner"))));

            player.sendMessage(text, false);
            this.phase = Phase.GREETING;
            this.initial = false;
        } else if(this.phase == Phase.GREETING) {
            MutableText text = Text.empty();
            text = text.append(Text.literal(PROMPT_ACTIONS.apply(random)).formatted(Formatting.GRAY));
            text = this.format(text, player, npc, null);

            text = text.append(" ");
            text = text.append(Text.literal("[Select Partner]").formatted(Formatting.AQUA)
                    .styled(style -> style.withClickEvent(new ClickEvent(RUN_COMMAND,
                            "/academy partner " + npc.getUuid() + " select_partner"))));

            player.sendMessage(text, false);
        } else if(this.phase == Phase.AWAIT_PARTNER_SUMMON) {
            MutableText text = Text.empty();
            text = text.append(Text.literal(PARTNER_SUMMON.apply(random)).formatted(Formatting.GRAY));
            text = this.format(text, player, npc, null);
            player.sendMessage(text, false);
        }
    }

    public void onSelectPartner(ServerPlayerEntity player, PartnerNPCEntity npc) {
        RandomSource random = JavaRandom.ofNanoTime();
        MutableText text = null;

        PartnerData data = ModWorldData.PARTNER.getGlobal(player.getWorld());

        if(data.getSelection(this.uuid).isPresent()) {
            text = Text.empty()
                    .append(Text.literal(PARTNER_SUMMON_ALREADY_SET.apply(random)).formatted(Formatting.GRAY));
        } else if(this.phase == Phase.GREETING) {
            text = Text.empty()
                    .append(Text.literal(PARTNER_SUMMON.apply(random)).formatted(Formatting.GRAY))
                    .append(" ")
                    .append(Text.literal(PARTNER_SUMMON_EXPLANATION.apply(random)).formatted(Formatting.GRAY));
            this.phase = Phase.AWAIT_PARTNER_SUMMON;
        } else if(this.phase == Phase.AWAIT_PARTNER_SUMMON) {
            text = Text.empty()
                    .append(Text.literal(PARTNER_SUMMON.apply(random)).formatted(Formatting.GRAY));
        }

        if(text != null) {
            text = this.format(text, player, npc, data.getSelection(this.uuid).orElse(null));
            player.sendMessage(text, false);
        }
    }

    public void onConfirmPartnerSelection(ServerPlayerEntity player, PartnerNPCEntity npc) {
        if(this.selectedSpecies == null) return;
        PartnerData data = ModWorldData.PARTNER.getGlobal(player.getWorld());
        if(data.getSelection(player.getUuid()).isPresent()) return;

        JavaRandom random = JavaRandom.ofNanoTime();
        MutableText text = Text.literal(PARTNER_SUMMON_SUCCESS.apply(random)).formatted(Formatting.GRAY);
        player.sendMessage(this.format(text, player, npc, this.selectedSpecies));
        data.setAndNotifySelection(player, this.selectedSpecies);
        this.phase = Phase.GREETING;
    }

    public MutableText format(MutableText text, ServerPlayerEntity player, PartnerNPCEntity npc, Species species) {
        MutableText result = text;

        if(player != null) {
            result = TextUtils.replace(result, "${player}", player.getName().copy().formatted(Formatting.WHITE));
        }

        if(npc != null) {
            result = TextUtils.replace(result, "${npc}", npc.getName().copy().formatted(Formatting.WHITE));
        }

        if(species != null) {
            int color = species.getPrimaryType().getHue();
            result = TextUtils.replace(result, "${pokemon}", species.getTranslatedName()
                    .setStyle(Style.EMPTY.withColor(color)));
        }

        return result;
    }

    public static Function<RandomSource, String> create(String... lines) {
        return random -> lines[random.nextInt(lines.length)];
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.BOOLEAN.writeNbt(this.initial).ifPresent(tag -> nbt.put("initial", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.initial = Adapters.BOOLEAN.readNbt(nbt.get("initial")).orElse(true);
    }

    public enum Phase {
        NONE, GREETING, AWAIT_PARTNER_SUMMON
    }

}
