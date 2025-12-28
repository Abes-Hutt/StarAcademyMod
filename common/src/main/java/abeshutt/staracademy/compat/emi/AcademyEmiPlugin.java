package abeshutt.staracademy.compat.emi;

import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.BoosterPackItem;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.proxy.ProxyItemRegistry;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@EmiEntrypoint
public class AcademyEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        this.registerBoosterPacks(registry);
        this.registerCardAlbums(registry);

        registry.removeEmiStacks(stack -> {
            return stack.getItemStack().getItem() == ModItems.LEGENDARY_PLACEHOLDER.get()
                    || stack.getItemStack().getItem() == ModBlocks.ERROR.get().asItem()
                    || stack.getItemStack().getItem() == ModItems.ACCEPTANCE_LETTER.get()
                    || stack.getItemStack().getItem() == ModItems.OUTFIT.get();
        });

        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = minecraft.getNetworkHandler();

        if (handler == null) {
            return;
        }

        registry.removeEmiStacks(stack -> {
            Identifier item = Registries.ITEM.getId(stack.getItemStack().getItem());
            return !ProxyItemRegistry.getItemRegistry(handler).contains(item);
        });
    }

    public void registerBoosterPacks(EmiRegistry registry) {
        ModConfigs.CARD_BOOSTERS.getValues().forEach((id, entry) -> {
            registry.addEmiStackAfter(EmiStack.of(BoosterPackItem.create(id)), stack -> {
                return stack.getItemStack().getItem() == ModItems.BOOSTER_PACK.get();
            });
        });

        registry.removeEmiStacks(stack -> {
            return stack.getItemStack().getItem() == ModItems.BOOSTER_PACK.get()
                    && BoosterPackItem.get(stack.getItemStack(), true).isEmpty();
        });
    }

    public void registerCardAlbums(EmiRegistry registry) {
        ModConfigs.CARD_ALBUMS.getValues().forEach((id, entry) -> {
            registry.addEmiStackAfter(EmiStack.of(CardAlbumItem.create(id)), stack -> {
                return stack.getItemStack().getItem() == ModItems.CARD_ALBUM.get();
            });
        });

        registry.removeEmiStacks(stack -> {
            return stack.getItemStack().getItem() == ModItems.CARD_ALBUM.get()
                    && CardAlbumItem.get(stack.getItemStack(), true).isEmpty();
        });
    }

}
