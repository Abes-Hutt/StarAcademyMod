package abeshutt.staracademy.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;
import com.cobblemon.mod.common.api.events.pokemon.*;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.player.PlayerEntity;

public class CommonEvents {

    public static final CobblemonEvent<SpawnEvent<PokemonEntity>> POKEMON_ENTITY_SPAWN = CobblemonEvent.of(CobblemonEvents.POKEMON_ENTITY_SPAWN);
    public static final CobblemonEvent<PokemonSentPreEvent> POKEMON_SENT_PRE = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_PRE);
    public static final CobblemonEvent<PokemonSentPostEvent> POKEMON_SENT_POST = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_POST);
    public static final CobblemonEvent<PokemonCatchRateEvent> POKEMON_CATCH_RATE = CobblemonEvent.of(CobblemonEvents.POKEMON_CATCH_RATE);
    public static final CobblemonEvent<PokemonCapturedEvent> POKEMON_CAPTURED = CobblemonEvent.of(CobblemonEvents.POKEMON_CAPTURED);
    public static final CobblemonEvent<ExperienceGainedPreEvent> POKEMON_EXPERIENCE_GAINED_PRE = CobblemonEvent.of(CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE);
    public static final CallbackEvent<PlayerTick> PLAYER_TICK = CallbackEvent.ofVoid(PlayerTick.class);

    public interface PlayerTick {
        void tick(PlayerEntity player);
    }

}
