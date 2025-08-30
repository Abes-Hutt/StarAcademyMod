package abeshutt.staracademy.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModOutfits;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.item.renderer.OutfitItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.util.ISpecialItemModel;
import abeshutt.staracademy.world.data.WardrobeData;
import abeshutt.staracademy.world.random.JavaRandom;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class OutfitItem extends Item implements ISpecialItemModel {

    public OutfitItem() {
        super(new Settings());
    }

    @Override
    public Text getName(ItemStack stack) {
        return getEntry(stack).flatMap(entry -> {
            if(entry.getNameKey() == null) {
                return Optional.empty();
            }

            return Optional.of((Text)Text.translatable(this.getTranslationKey(stack) + "." + entry.getNameKey()));
        }).orElseGet(() -> super.getName(stack));
    }

    public static Optional<OutfitEntry> getEntry(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponents.OUTFIT_ENTRY.get()));
    }

    public static void setEntry(ItemStack stack, OutfitEntry entry) {
        stack.set(ModDataComponents.OUTFIT_ENTRY.get(), entry);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if(!world.isClient() && user instanceof ServerPlayerEntity player) {
            WardrobeData data = ModWorldData.WARDROBE.getGlobal(world);

            OutfitItem.getEntry(stack).ifPresent(entry -> entry.generate().forEach(outfit -> {
                if(data.setUnlocked(player, outfit, true) && !user.isCreative()) {
                    stack.decrement(1);
                }
            }));
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        getEntry(stack).ifPresent(entry -> {
            entry.flatten(JavaRandom.ofNanoTime()).ifPresent(flattened -> {
                if(entry != flattened) {
                    setEntry(stack, entry);
                }
            });
        });
    }

    @Override
    public void loadModels(Stream<Identifier> unbakedModels, Consumer<ModelIdentifier> loader) {
        unbakedModels.forEach(id -> {
            if(id.getNamespace().equals(StarAcademyMod.ID) && id.getPath().startsWith("outfit")) {
                loader.accept(StarAcademyMod.mid(id, "inventory"));
            }
        });
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return OutfitItemRenderer.INSTANCE;
    }

}
