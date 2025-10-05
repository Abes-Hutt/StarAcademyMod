package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokeball.catching.modifiers.WorldStateModifier;
import com.cobblemon.mod.common.pokeball.PokeBall;

import java.util.ArrayList;
import java.util.List;

public class ModPokeBalls {

    public static final PokeBall GREAT_SAFARI_BALL = new PokeBall(StarAcademyMod.id("great_safari_ball"),
            new WorldStateModifier((thrower, entity) -> {
                if (!entity.isBattling()) {
                    return 4.0F;
                }

                return 1F;
            }), new ArrayList<>(), 0.8F, StarAcademyMod.id("great_safari_ball"),
            StarAcademyMod.id("item/great_safari_ball_model"), 1.25F, false);
    public static final PokeBall GOLDEN_SAFARI_BALL = new PokeBall(StarAcademyMod.id("golden_safari_ball"),
            new WorldStateModifier((thrower, entity) -> {
                if (!entity.isBattling()) {
                    return 8.0F;
                }

                return 1F;
            }),
            List.of((thrower, pokemon) -> pokemon.setShiny(true)),
            0.8F, StarAcademyMod.id("golden_safari_ball"),
            StarAcademyMod.id("item/golden_safari_ball_model"), 1.25F, false);
}
