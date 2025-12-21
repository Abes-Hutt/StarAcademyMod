package abeshutt.staracademy.init;

import abeshutt.staracademy.item.OutfitItem;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ModCreativeTabs extends ModRegistries {

    public static RegistrySupplier<ItemGroup> ACADEMY;

    public static void register() {
        ACADEMY = registerEntries(register(ITEM_GROUPS, "academy",
                () -> FabricItemGroup.builder()
                    .icon(() -> OutfitItem.create("classy1_shirt"))
                    .displayName(Text.translatable("item_group.academy"))
                    .build()), group -> {
                for (RegistrySupplier<Item> item : ModItems.ITEMS) {
                    group.add(new ItemStack(item.get()));
                }
        });
    }

    public static RegistrySupplier<ItemGroup> registerEntries(RegistrySupplier<ItemGroup> group, Consumer<FabricItemGroupEntries> event) {
        ItemGroupEvents.modifyEntriesEvent(((DeferredSupplier<ItemGroup>)group).getKey())
                .register(event::accept);
        return group;
    }

}
