package abeshutt.staracademy.compat.emi;

import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.init.ModBlocks;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.BoosterPackItem;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.item.SafariTicketItem;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.*;

import java.util.HashSet;
import java.util.Set;

@EmiEntrypoint
public class AcademyEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        this.registerSafariTickets(registry);
        this.registerBoosterPacks(registry);
        this.registerCardAlbums(registry);

        registry.removeEmiStacks(stack -> {
            return stack.getItemStack().getItem() == ModItems.LEGENDARY_PLACEHOLDER.get()
                    || stack.getItemStack().getItem() == ModBlocks.ERROR.get().asItem()
                    || stack.getItemStack().getItem() == ModBlocks.SAFARI_PORTAL.get().asItem()
                    || stack.getItemStack().getItem() == ModItems.ACCEPTANCE_LETTER.get()
                    || stack.getItemStack().getItem() == ModItems.OUTFIT.get();
        });

        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = minecraft.getNetworkHandler();

        if (handler == null) {
            return;
        }

        DynamicRegistryManager registries = handler.getRegistryManager();
        RecipeManager recipes = handler.getRecipeManager();

        Set<Item> serverItems = new HashSet<>();

        for (RecipeEntry<?> recipe : recipes.values()) {
            ItemStack out = recipe.value().getResult(registries);

            if (!out.isEmpty()) {
                serverItems.add(out.getItem());
            }

            for (Ingredient ingredient : recipe.value().getIngredients()) {
                for (ItemStack in : ingredient.getMatchingStacks()) {
                    if (!in.isEmpty()) {
                        serverItems.add(in.getItem());
                    }
                }
            }
        }

        registry.removeEmiStacks(stack -> {
            Item item = stack.getItemStack().getItem();
            return !serverItems.contains(item);
        });
    }

    public void registerSafariTickets(EmiRegistry registry) {
        SafariConfig.CLIENT.getTickets().forEach((id, entry) -> {
            registry.addEmiStackAfter(EmiStack.of(SafariTicketItem.create(id)), stack -> {
                return stack.getItemStack().getItem() == ModItems.SAFARI_TICKET.get();
            });
        });

        registry.removeEmiStacks(stack -> {
            return stack.getItemStack().getItem() == ModItems.SAFARI_TICKET.get()
                    && SafariTicketItem.getEntry(stack.getItemStack(), true).isEmpty();
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
