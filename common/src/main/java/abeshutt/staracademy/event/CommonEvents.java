package abeshutt.staracademy.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPreEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class CommonEvents {

    public static final CobblemonEvent<SpawnEvent<PokemonEntity>> POKEMON_ENTITY_SPAWN = CobblemonEvent.of(CobblemonEvents.POKEMON_ENTITY_SPAWN);
    public static final CobblemonEvent<PokemonSentPreEvent> POKEMON_SENT_PRE = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_PRE);
    public static final CobblemonEvent<PokemonSentPostEvent> POKEMON_SENT_POST = CobblemonEvent.of(CobblemonEvents.POKEMON_SENT_POST);
    public static final CobblemonEvent<PokemonCatchRateEvent> POKEMON_CATCH_RATE = CobblemonEvent.of(CobblemonEvents.POKEMON_CATCH_RATE);

}
