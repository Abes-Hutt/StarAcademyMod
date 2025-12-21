package abeshutt.staracademy.init;

import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.item.BoosterPackItem;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.item.OutfitItem;
import abeshutt.staracademy.item.SafariTicketItem;
import abeshutt.staracademy.proxy.ProxyItemRegistry;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModCreativeTabs extends ModRegistries {

    public static RegistrySupplier<ItemGroup> ACADEMY;

    public static void register() {
        ACADEMY = registerEntries(register(ITEM_GROUPS, "academy",
                () -> FabricItemGroup.builder()
                    .icon(() -> OutfitItem.create("classy1_shirt"))
                    .displayName(Text.translatable("item_group.academy"))
                    .build()), group -> {
                for (RegistrySupplier<Item> supplier : ModItems.ITEMS) {
                    Item item = supplier.get();

                    if (item == ModItems.SAFARI_TICKET.get()) {
                        SafariConfig.CLIENT.getTickets().forEach((id, entry) -> {
                            group.add(SafariTicketItem.create(id));
                        });
                    } else if(item == ModItems.BOOSTER_PACK.get()) {
                        ModConfigs.CARD_BOOSTERS.getValues().forEach((id, entry) -> {
                            group.add(BoosterPackItem.create(id));
                        });
                    } else if(item == ModItems.CARD_ALBUM.get()) {
                        ModConfigs.CARD_ALBUMS.getValues().forEach((id, entry) -> {
                            group.add(CardAlbumItem.create(id));
                        });
                    } else if(item == ModItems.LEGENDARY_PLACEHOLDER.get()
                            || item == ModBlocks.ERROR.get().asItem()
                            || item == ModBlocks.SAFARI_PORTAL.get().asItem()
                            || item == ModItems.ACCEPTANCE_LETTER.get()
                            || item == ModItems.OUTFIT.get()) {

                    } else {
                        group.add(new ItemStack(item));
                    }
                }
        });
    }

    public static RegistrySupplier<ItemGroup> registerEntries(RegistrySupplier<ItemGroup> group, Consumer<FabricItemGroupEntries> event) {
        ItemGroupEvents.modifyEntriesEvent(((DeferredSupplier<ItemGroup>)group).getKey())
                .register(event::accept);
        return group;
    }

}
