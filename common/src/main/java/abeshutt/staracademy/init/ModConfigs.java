package abeshutt.staracademy.init;

import abeshutt.staracademy.config.*;
import dev.architectury.event.events.common.LifecycleEvent;

import java.util.ArrayList;
import java.util.List;

public class ModConfigs extends ModRegistries {

    public static List<Runnable> POST_LOAD = new ArrayList<>();

    public static TileGroupsConfig TILE_GROUPS;
    public static EntityGroupsConfig ENTITY_GROUPS;
    public static ItemGroupsConfig ITEM_GROUPS;
    public static BiomeGroupsConfig BIOME_GROUPS;

    public static StarterRaffleConfig STARTER_RAFFLE;
    public static PokemonSpawnConfig POKEMON_SPAWN;
    public static ECCobblemonConfig ENHANCED_CELESTIALS_COBBLEMON_CONFIG;
    public static SafariConfig SAFARI;
    public static WardrobeConfig WARDROBE;
    public static NPCConfig NPC;
    public static DuelingConfig DUELING;
    public static ItemLogicConfig ITEM_LOGIC;
    public static StarBadgeConfig STAR_BADGE;
    public static EntityYeeterConfig ENTITY_YEETER;
    public static ShopConfig SHOP;
    public static LegendaryItemsConfig LEGENDARY_ITEMS;
    public static GymCachesConfig GYM_CACHES;

    public static void register(boolean initialization) {
        TILE_GROUPS = new TileGroupsConfig().read();
        ENTITY_GROUPS = new EntityGroupsConfig().read();
        ITEM_GROUPS = new ItemGroupsConfig().read();
        BIOME_GROUPS = new BiomeGroupsConfig().read();

        STARTER_RAFFLE = new StarterRaffleConfig().read();
        POKEMON_SPAWN = new PokemonSpawnConfig().read();
        ENHANCED_CELESTIALS_COBBLEMON_CONFIG = new ECCobblemonConfig().read();
        SAFARI = new SafariConfig().read();
        WARDROBE = new WardrobeConfig().read();
        NPC = new NPCConfig().read();
        DUELING = new DuelingConfig().read();
        ITEM_LOGIC = new ItemLogicConfig().read();
        STAR_BADGE = new StarBadgeConfig().read();
        ENTITY_YEETER = new EntityYeeterConfig().read();
        SHOP = new ShopConfig().read();
        LEGENDARY_ITEMS = new LegendaryItemsConfig().read();
        GYM_CACHES = new GymCachesConfig().read();

        if(!initialization) {
            ArrayList<Runnable> actions = new ArrayList<>(POST_LOAD);
            POST_LOAD.clear();
            actions.forEach(Runnable::run);
        } else {
            LifecycleEvent.SETUP.register(() -> {
                ArrayList<Runnable> actions = new ArrayList<>(POST_LOAD);
                POST_LOAD.clear();
                actions.forEach(Runnable::run);
            });
        }
    }

}
