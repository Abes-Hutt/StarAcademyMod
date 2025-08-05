package abeshutt.staracademy.compat.enhancedcelestials;


import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.compat.enhancedcelestials.block.LunarForecastHologramBlock;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.LunarForecastHologramBlockEntity;
import abeshutt.staracademy.init.ModBlocks;
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
import dev.architectury.registry.registries.RegistrySupplier;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.ECLunarEventTags;
import dev.corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials.lunarevent.EnhancedCelestialsLunarForecastWorldData;
import kotlin.Unit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnhancedCelestialsCompat {


    public static final RegistryKey<LunarEvent> AURORA_MOON = RegistryKey.of(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, StarAcademyMod.id("aurora_moon"));

    public static void init() {
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, experienceGainedPreEvent -> {
            if (experienceGainedPreEvent.getPokemon().heldItem().isOf(CobblemonItems.EXP_SHARE)) {
                PokemonEntity entity = experienceGainedPreEvent.getPokemon().getEntity();
                if (entity == null) return Unit.INSTANCE;
                World world = entity.getEntityWorld();
                if (!world.isClient) {
                    EnhancedCelestials.lunarForecastWorldData(world).ifPresent(worldData -> {
                        if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.HARVEST_MOON)) {
                            experienceGainedPreEvent.setExperience((int) (experienceGainedPreEvent.getExperience() * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getHarvestMoonExpShareMultiplier()));
                        }
                    });
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
                    Optional<EnhancedCelestialsLunarForecastWorldData> enhancedCelestialsLunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(spawningContext.getWorld());
                    if (enhancedCelestialsLunarForecastWorldData.isEmpty()) {
                        return SpawningInfluence.DefaultImpls.affectWeight(this, spawnDetail, spawningContext, v);
                    }
                    EnhancedCelestialsLunarForecastWorldData worldData = enhancedCelestialsLunarForecastWorldData.orElseThrow();


                    PokemonProperties pokemon = pokemonSpawnDetail.getPokemon();
                    Boolean shiny = pokemon.getShiny();
                    if (shiny != null && shiny) {
                        if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.BLUE_MOON)) {
                            return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getBlueMoonShinyMultiplier();
                        }
                    }

                    IVs ivs = pokemon.getIvs();
                    if (ivs != null && !ivs.getAcceptableRange().isEmpty()) {
                        if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.BLOOD_MOON)) {
                            return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getBloodMoonIVsMultiplier();
                        }
                    }

                    if (spawnDetail.getBucket().getWeight() < 5) {
                        if (worldData.currentLunarEventHolder().matchesKey(AURORA_MOON)) {
                            return v * ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getAuroraMoonRarePokemonSpawnMultiplier();
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


    public static final RegistrySupplier<LunarForecastHologramBlock> LUNAR_FORECAST_HOLOGRAM_BLOCK = ModBlocks.register(
            "lunar_forecast_hologram",
            () -> new LunarForecastHologramBlock(AbstractBlock.Settings.copy(Blocks.ANVIL).luminance(value -> value.get(LunarForecastHologramBlock.LIT) ? 5 : 0)),
            b -> new BlockItem(b.get(), new Item.Settings())
    );
    public static final RegistrySupplier<BlockEntityType<LunarForecastHologramBlockEntity>> LUNAR_FORECAST_HOLOGRAM_BLOCK_ENTITY = ModBlocks.Entities.register(
            "lunar_forecast_hologram",
            LunarForecastHologramBlockEntity::new,
            LUNAR_FORECAST_HOLOGRAM_BLOCK
    );
}
