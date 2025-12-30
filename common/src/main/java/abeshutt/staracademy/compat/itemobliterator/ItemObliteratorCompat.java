package abeshutt.staracademy.compat.itemobliterator;

import elocindev.item_obliterator.fabric_quilt.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

import java.util.HashSet;
import java.util.Set;

public class ItemObliteratorCompat {

    public static boolean COMPUTING = false;
    private static Set<String> BLACKLISTED_ITEMS = null;

    public static Boolean isDisabled(String itemid) {
        if (BLACKLISTED_ITEMS == null) {
            COMPUTING = true;
            BLACKLISTED_ITEMS = new HashSet<>();

            for (RegistryKey<Item> key : Registries.ITEM.getKeys()) {
                String itemId = key.getValue().toString();

                if (Utils.isDisabled(itemId)) {
                    BLACKLISTED_ITEMS.add(itemId);
                }
            }

            COMPUTING = false;
        }

        return BLACKLISTED_ITEMS.contains(itemid);
    }

}
