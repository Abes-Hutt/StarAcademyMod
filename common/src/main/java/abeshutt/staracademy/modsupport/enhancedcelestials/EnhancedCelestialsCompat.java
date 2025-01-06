package abeshutt.staracademy.modsupport.enhancedcelestials;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.SpawnBucket;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.context.calculators.SpawningContextCalculator;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import corgitaco.enhancedcelestials.api.lunarevent.DefaultLunarEvents;
import corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import kotlin.Unit;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EnhancedCelestialsCompat {

    public static final RegistryKey<LunarEvent> AURORA_MOON = RegistryKey.of(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, StarAcademyMod.id("aurora_moon"));

    public static void init() {
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, experienceGainedPreEvent -> {
            if(experienceGainedPreEvent.getPokemon().heldItem().isOf(CobblemonItems.EXP_SHARE)) {
                PokemonEntity entity = experienceGainedPreEvent.getPokemon().getEntity();
                if(entity == null) return Unit.INSTANCE;
                World world = entity.getEntityWorld();
                if (!world.isClient && world instanceof EnhancedCelestialsWorldData celestialsContext) {
                    EnhancedCelestialsContext lunarContext = celestialsContext.getLunarContext();
                    if (lunarContext != null) {
                        if (lunarContext.getLunarForecast().getCurrentEventRaw().getKey().orElseThrow() == DefaultLunarEvents.HARVEST_MOON) {
                            experienceGainedPreEvent.setExperience((int) (experienceGainedPreEvent.getExperience() * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getHarvestMoonExpShareMultiplier()));
                        }
                    }
                }
            }

            return Unit.INSTANCE;
        });

        PlayerSpawnerFactory.INSTANCE.getInfluenceBuilders().add(serverPlayerEntity -> new SpawningInfluence() {
            @Override
            public boolean isAllowedPosition(@NotNull ServerWorld serverWorld, @NotNull BlockPos blockPos, @NotNull SpawningContextCalculator<?, ?> spawningContextCalculator) {
                return SpawningInfluence.DefaultImpls.isAllowedPosition(this, serverWorld, blockPos, spawningContextCalculator);
            }

            @Override
            public float affectBucketWeight(@NotNull SpawnBucket spawnBucket, float v) {
                return SpawningInfluence.DefaultImpls.affectBucketWeight(this, spawnBucket, v);
            }

            @Override
            public void affectSpawn(@NotNull Entity entity) {
                SpawningInfluence.DefaultImpls.affectSpawn(this, entity);
            }

            @Override
            public float affectWeight(@NotNull SpawnDetail spawnDetail, @NotNull SpawningContext spawningContext, float v) {
                if (spawnDetail instanceof PokemonSpawnDetail pokemonSpawnDetail) {
                    PokemonProperties pokemon = pokemonSpawnDetail.getPokemon();
                    Boolean shiny = pokemon.getShiny();
                    if (shiny != null && shiny) {
                        ServerWorld world = spawningContext.getWorld();
                        if (world instanceof EnhancedCelestialsWorldData celestialsContext) {
                            EnhancedCelestialsContext lunarContext = celestialsContext.getLunarContext();
                            if (lunarContext != null) {
                                if (lunarContext.getLunarForecast().getCurrentEventRaw().getKey().orElseThrow() == DefaultLunarEvents.BLUE_MOON) {
                                    return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getBlueMoonShinyMultiplier();
                                }
                            }
                        }
                    }

                    IVs ivs = pokemon.getIvs();
                    if (ivs != null && !ivs.getAcceptableRange().isEmpty()) {
                        ServerWorld world = spawningContext.getWorld();
                        if (world instanceof EnhancedCelestialsWorldData celestialsContext) {
                            EnhancedCelestialsContext lunarContext = celestialsContext.getLunarContext();
                            if (lunarContext != null) {
                                if (lunarContext.getLunarForecast().getCurrentEventRaw().getKey().orElseThrow() == DefaultLunarEvents.BLOOD_MOON) {
                                    return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getBloodMoonIVsMultiplier();
                                }
                            }
                        }
                    }

                    if (spawnDetail.getBucket().getWeight() < 5) {
                        ServerWorld world = spawningContext.getWorld();
                        if (world instanceof EnhancedCelestialsWorldData celestialsContext) {
                            EnhancedCelestialsContext lunarContext = celestialsContext.getLunarContext();
                            if (lunarContext != null) {
                                if (lunarContext.getLunarForecast().getCurrentEventRaw().getKey().orElseThrow() == AURORA_MOON) {
                                    return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getAuroraMoonRarePokemonSpawnMultiplier();
                                }
                            }
                        }

                    }
                }
                return SpawningInfluence.DefaultImpls.affectWeight(this, spawnDetail, spawningContext, v);
            }

            @Override
            public boolean affectSpawnable(@NotNull SpawnDetail spawnDetail, @NotNull SpawningContext spawningContext) {
                return SpawningInfluence.DefaultImpls.affectSpawnable(this, spawnDetail, spawningContext);
            }

            @Override
            public boolean isExpired() {
                return SpawningInfluence.DefaultImpls.isExpired(this);
            }

            @Override
            public void affectAction(@NotNull SpawnAction<?> spawnAction) {
                SpawningInfluence.DefaultImpls.affectAction(this, spawnAction);
            }
        });
    }

}
