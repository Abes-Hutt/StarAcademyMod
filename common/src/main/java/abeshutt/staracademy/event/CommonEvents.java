package abeshutt.staracademy.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CommonEvents {

    public static final CobblemonEvent<SpawnEvent<PokemonEntity>> POKEMON_ENTITY_SPAWN = CobblemonEvent.of(CobblemonEvents.POKEMON_ENTITY_SPAWN);
    public static final CobblemonEvent<PokemonSentEvent.Pre> POKEMON_SENT_PRE = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_PRE);
    public static final CobblemonEvent<PokemonSentEvent.Post> POKEMON_SENT_POST = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_POST);
    public static final CobblemonEvent<BattleStartedEvent.Pre> BATTLE_STARTED_PRE = CobblemonEvent.of(CobblemonEvents.BATTLE_STARTED_PRE);
    public static final CobblemonEvent<PokemonCatchRateEvent> POKEMON_CATCH_RATE = CobblemonEvent.of(CobblemonEvents.POKEMON_CATCH_RATE);
    public static final CobblemonEvent<PokemonCapturedEvent> POKEMON_CAPTURED = CobblemonEvent.of(CobblemonEvents.POKEMON_CAPTURED);
    public static final CobblemonEvent<ExperienceGainedEvent.Pre> POKEMON_EXPERIENCE_GAINED_PRE = CobblemonEvent.of(CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE);
    public static final CallbackEvent<PlayerTick> PLAYER_TICK = CallbackEvent.ofVoid(PlayerTick.class);

    public interface PlayerTick {
        void tick(PlayerEntity player);
    }

}
