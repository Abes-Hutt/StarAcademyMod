package abeshutt.staracademy.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardAlbumEntry;
import abeshutt.staracademy.data.component.CardAlbumInventory;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.renderer.CardAlbumItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import abeshutt.staracademy.util.ISpecialItemModel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CardAlbumItem extends TrinketItem implements ISpecialItemModel {

    public static final Object REFERENCE = new Object();

    public CardAlbumItem() {
        super(new Settings().fireproof().maxCount(1));
    }

    public static Optional<CardAlbumEntry> get(ItemStack stack, boolean client) {
        return Optional.ofNullable(stack.getOrDefault(ModDataComponents.CARD_ALBUM.get(), null))
                .flatMap(id -> ModConfigs.CARD_ALBUMS.get(id));
    }

    public static void set(ItemStack stack, String id) {
        stack.set(ModDataComponents.CARD_ALBUM.get(), id);
    }

    public static ItemStack create(String id) {
        ItemStack stack = new ItemStack(ModItems.CARD_ALBUM.get());
        stack.set(ModDataComponents.CARD_ALBUM.get(), id);
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        Text text = super.getName(stack);

        Integer color = CardAlbumItem.get(stack, Platform.getEnv() == EnvType.CLIENT)
                .map(CardAlbumEntry::getColor)
                .orElse(null);

        if(color != null) {
            text = text.copy().setStyle(Style.EMPTY.withColor(color));
        }

        return text;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String id = stack.getOrDefault(ModDataComponents.CARD_ALBUM.get(), null);
        return super.getTranslationKey(stack) + (id == null ? "" : "." + id);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(hand == Hand.MAIN_HAND && !user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);

            if(user instanceof ServerPlayerEntity player) {
                if(!stack.contains(ModDataComponents.CARD_ALBUM_CONTAINER.get())) {
                    stack.set(ModDataComponents.CARD_ALBUM_CONTAINER.get(), new CardAlbumInventory());
                }

                MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
                    @Override
                    public void saveExtraData(PacketByteBuf buf) {

                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.empty();
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity _player) {
                        return new CardAlbumScreenHandler(syncId, player, stack, stack.get(ModDataComponents.CARD_ALBUM_CONTAINER.get()).copy());
                    }
                });
            }

            return TypedActionResult.success(stack, false);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void loadModels(Stream<Identifier> unbakedModels, Consumer<ModelIdentifier> loader) {
        unbakedModels.forEach(id -> {
            if(id.getNamespace().equals(StarAcademyMod.ID) && id.getPath().startsWith("card_album")) {
                loader.accept(StarAcademyMod.mid(id, "inventory"));
            }
        });
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return CardAlbumItemRenderer.INSTANCE;
    }

}
