package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.stats.EvCalculator;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

import static abeshutt.staracademy.attribute.Attributes.ofEVYield;

@Mixin(PokemonBattle.class)
public class MixinPokemonBattle {

    @Redirect(method = "end", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/pokemon/stats/EvCalculator;calculate(Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;)Ljava/util/Map;"), remap = false)
    private Map<Stat, Integer> end(EvCalculator instance, BattlePokemon battlePokemon, BattlePokemon opponentPokemon) {
        Map<Stat, Integer> evs = instance.calculate(battlePokemon, opponentPokemon);
        ServerPlayerEntity player = battlePokemon.getEffectedPokemon().getOwnerPlayer();

        if(player == null) {
            return evs;
        }

        for(Stats stat : Stats.values()) {
            int yield = evs.getOrDefault(stat, 0);
            evs.put(stat, AttributeHolder.getRoot(player).path(ofEVYield(stat))
                    .map(attribute -> {
                        Option<Rational> result = attribute.get(Option.present(Rational.of(yield)),
                                AttributeContext.random());

                        if(result.isPresent()) {
                            RandomSource random = JavaRandom.ofNanoTime();
                            double raw = result.get().doubleValue();
                            int floored = (int)raw;
                            return floored + (random.nextDouble() < raw - floored ? 1 : 0);
                        }

                        return yield;
                    }).orElse(yield));
        }

        evs.entrySet().removeIf(entry -> entry.getValue() == 0);
        return evs;
    }

}
