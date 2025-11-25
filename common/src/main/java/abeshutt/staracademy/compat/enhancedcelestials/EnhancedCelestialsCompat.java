package abeshutt.staracademy.compat.enhancedcelestials;


import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.compat.enhancedcelestials.block.LunarForecastHologramBlock;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.LunarForecastHologramBlockEntity;
import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModConfigs;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.ECLunarEventTags;
import dev.corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import kotlin.Unit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

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
                            if(worldData.currentLunarEventHolder().isIn(ECLunarEventTags.SUPER_MOON)) {
                                experienceGainedPreEvent.setExperience((int) (experienceGainedPreEvent.getExperience()
                                        * ModConfigs.ENHANCED_CELESTIALS.getSuperHarvestMoonExpShareMultiplier()));
                            } else {
                                experienceGainedPreEvent.setExperience((int) (experienceGainedPreEvent.getExperience()
                                        * ModConfigs.ENHANCED_CELESTIALS.getHarvestMoonExpShareMultiplier()));
                            }

                        }
                    });
                }
            }

            return Unit.INSTANCE;
        });

        PlayerSpawnerFactory.INSTANCE.getInfluenceBuilders().add(serverPlayerEntity -> new SpawningInfluence() {
            /*
            @Override
            public float affectBucketWeight(@NotNull SpawnBucket spawnBucket, float v) {
                Optional<EnhancedCelestialsLunarForecastWorldData> enhancedCelestialsLunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(serverPlayerEntity.getWorld());

                if (enhancedCelestialsLunarForecastWorldData.isEmpty()) {
                    return SpawningInfluence.DefaultImpls.affectBucketWeight(this, spawnBucket, v);
                }

                EnhancedCelestialsLunarForecastWorldData worldData = enhancedCelestialsLunarForecastWorldData.orElseThrow();

                if (spawnBucket.getName().equals("uncommon") || spawnBucket.getName().equals("rare") || spawnBucket.getName().equals("ultra-rare")) {
                    if (worldData.currentLunarEventHolder().matchesKey(AURORA_MOON)) {
                        if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.SUPER_MOON)) {
                            return v * ModConfigs.ENHANCED_CELESTIALS.getSuperAuroraMoonRarePokemonSpawnMultiplier();
                        } else {
                            return v * ModConfigs.ENHANCED_CELESTIALS.getAuroraMoonRarePokemonSpawnMultiplier();
                        }
                    }
                }

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
                    IVs ivs = pokemon.getIvs();
                    if (ivs != null && !ivs.getAcceptableRange().isEmpty()) {
                        if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.BLOOD_MOON)) {
                            if (worldData.currentLunarEventHolder().isIn(ECLunarEventTags.SUPER_MOON)) {
                                return v * ModConfigs.ENHANCED_CELESTIALS.getSuperBloodMoonIVsMultiplier();
                            } else {
                                return v * ModConfigs.ENHANCED_CELESTIALS.getBloodMoonIVsMultiplier();
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
            }*/
        });
    }


    public static final RegistrySupplier<LunarForecastHologramBlock> LUNAR_FORECAST_HOLOGRAM_BLOCK = ModBlocks.register(
            "lunar_forecast_hologram",
            () -> new LunarForecastHologramBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).luminance(value -> value.get(LunarForecastHologramBlock.LIT) ? 5 : 0)),
            b -> new BlockItem(b.get(), new Item.Settings())
    );
    public static final RegistrySupplier<BlockEntityType<LunarForecastHologramBlockEntity>> LUNAR_FORECAST_HOLOGRAM_BLOCK_ENTITY = ModBlocks.Entities.register(
            "lunar_forecast_hologram",
            LunarForecastHologramBlockEntity::new,
            LUNAR_FORECAST_HOLOGRAM_BLOCK
    );
}
