package abeshutt.staracademy.init;

import abeshutt.staracademy.item.*;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModItems extends ModRegistries {

    public static RegistrySupplier<Item> STAR_BADGE;
    public static RegistrySupplier<Item> HUNT;
    public static RegistrySupplier<DuelingGloveItem> DUELING_GLOVE;
    public static RegistrySupplier<OutfitItem> OUTFIT;
    public static RegistrySupplier<SafariTicketItem> SAFARI_TICKET;
    public static RegistrySupplier<SlingshotItem> SLINGSHOT;
    public static RegistrySupplier<Item> HA_FOSSIL;
    public static RegistrySupplier<Item> MAX_IV_FOSSIL;
    public static RegistrySupplier<Item> SHINY_FOSSIL;
    public static RegistrySupplier<Item> SHINY_INCENSE;
    public static RegistrySupplier<Item> STRONG_SHINY_INCENSE;
    public static RegistrySupplier<Item> UBER_SHINY_INCENSE;
    public static RegistrySupplier<Item> CARD;
    public static RegistrySupplier<Item> BOOSTER_PACK;
    public static RegistrySupplier<Item> ALBUM;

    public static void register() {
        STAR_BADGE = register("star_badge", StarBadgeItem::new);
        HUNT = register("hunt", () -> new Item(new Item.Settings().maxCount(1).fireproof()));
        DUELING_GLOVE = register("dueling_glove", DuelingGloveItem::new);
        OUTFIT = register("outfit", OutfitItem::new);
        SAFARI_TICKET = register("safari_ticket", SafariTicketItem::new);
        SLINGSHOT = register("slingshot", SlingshotItem::new);
        HA_FOSSIL = register("ha_fossil", () -> new Item(new Item.Settings().maxCount(1)));
        MAX_IV_FOSSIL = register("max_iv_fossil", () -> new Item(new Item.Settings().maxCount(1)));
        SHINY_FOSSIL = register("shiny_fossil", () -> new Item(new Item.Settings().maxCount(1)));
        SHINY_INCENSE = register("shiny_incense", () -> new Item(new Item.Settings().maxCount(1)));
        STRONG_SHINY_INCENSE = register("strong_shiny_incense", () -> new Item(new Item.Settings().maxCount(1)));
        UBER_SHINY_INCENSE = register("uber_shiny_incense", () -> new Item(new Item.Settings().maxCount(1)));
        CARD = register("card", CardItem::new);
        BOOSTER_PACK = register("booster_pack", BoosterPackItem::new);
        ALBUM = register("album", AlbumItem::new);
    }

    public static <V extends Item> RegistrySupplier<V> register(Identifier id, Supplier<V> item) {
        return register(ITEMS, id, item);
    }

    public static <V extends Item> RegistrySupplier<V> register(String name, Supplier<V> item) {
        return register(ITEMS, name, item);
    }

}

