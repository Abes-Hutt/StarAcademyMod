package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardData;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.component.CardAlbumInventory;
import abeshutt.staracademy.item.OutfitEntry;
import abeshutt.staracademy.world.StarOwnership;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponents extends ModRegistries {

    public static RegistrySupplier<ComponentType<String>> SAFARI_TICKET_ENTRY;
    public static RegistrySupplier<ComponentType<OutfitEntry>> OUTFIT_ENTRY;
    public static RegistrySupplier<ComponentType<StarOwnership>> STAR_OWNERSHIP;
    public static RegistrySupplier<ComponentType<CardData>> CARD;
    public static RegistrySupplier<ComponentType<String>> BOOSTER_PACK;
    public static RegistrySupplier<ComponentType<String>> CARD_ALBUM;
    public static RegistrySupplier<ComponentType<CardAlbumInventory>> CARD_ALBUM_CONTAINER;
    public static RegistrySupplier<ComponentType<UUID>> ACCEPTANCE_LETTER_OWNER;
    public static RegistrySupplier<ComponentType<Boolean>> ACCEPTANCE_LETTER_OPEN;
    public static RegistrySupplier<ComponentType<Boolean>> ACCEPTANCE_LETTER_ENROLLED;
    public static RegistrySupplier<ComponentType<String>> ACCEPTANCE_LETTER_HOUSE;

    public static void register() {
        SAFARI_TICKET_ENTRY = register(StarAcademyMod.id("safari_ticket_entry"), builder -> builder
                .codec(Adapters.UTF_8.codecNbt()).packetCodec(Adapters.UTF_8));

        OUTFIT_ENTRY = register(StarAcademyMod.id("outfit_entry"), builder -> builder
                .codec(Adapters.OUTFIT_ENTRY.codecNbt()).packetCodec(Adapters.OUTFIT_ENTRY));

        STAR_OWNERSHIP = register(StarAcademyMod.id("star_ownership"), builder -> builder
                .codec(Adapters.STAR_OWNERSHIP.codecNbt()).packetCodec(Adapters.STAR_OWNERSHIP));

        CARD = register(StarAcademyMod.id("card"), builder -> builder
                .codec(Adapters.CARD.codecNbt()).packetCodec(new PacketCodec<>() {
                    @Override
                    public CardData decode(RegistryByteBuf buf) {
                        return Adapters.CARD.readNbt(buf.readNbt()).orElseThrow();
                    }

                    @Override
                    public void encode(RegistryByteBuf buf, CardData value) {
                        buf.writeNbt(Adapters.CARD.writeNbt(value).orElseThrow());
                    }
                }));

        BOOSTER_PACK = register(StarAcademyMod.id("booster_pack"), builder -> builder
                .codec(Adapters.UTF_8.codecNbt()).packetCodec(Adapters.UTF_8));

        CARD_ALBUM = register(StarAcademyMod.id("card_album"), builder -> builder
                .codec(Adapters.UTF_8.codecNbt()).packetCodec(Adapters.UTF_8));

        CARD_ALBUM_CONTAINER = register(StarAcademyMod.id("card_album_container"), builder -> builder
                .codec(CardAlbumInventory.ADAPTER.codecNbt()).packetCodec(new PacketCodec<>() {
                    @Override
                    public CardAlbumInventory decode(RegistryByteBuf buf) {
                        return CardAlbumInventory.ADAPTER.readNbt(buf.readNbt()).orElseThrow();
                    }

                    @Override
                    public void encode(RegistryByteBuf buf, CardAlbumInventory value) {
                        buf.writeNbt(CardAlbumInventory.ADAPTER.writeNbt(value).orElseThrow());
                    }
                }));

        ACCEPTANCE_LETTER_OWNER = register(StarAcademyMod.id("acceptance_letter_owner"), builder -> builder
                .codec(Adapters.UUID.codecNbt()).packetCodec(Adapters.UUID));

        ACCEPTANCE_LETTER_OPEN = register(StarAcademyMod.id("acceptance_letter_open"), builder -> builder
                .codec(Adapters.BOOLEAN.codecNbt()).packetCodec(Adapters.BOOLEAN));

        ACCEPTANCE_LETTER_ENROLLED = register(StarAcademyMod.id("acceptance_letter_enrolled"), builder -> builder
                .codec(Adapters.BOOLEAN.codecNbt()).packetCodec(Adapters.BOOLEAN));

        ACCEPTANCE_LETTER_HOUSE = register(StarAcademyMod.id("acceptance_letter_house"), builder -> builder
                .codec(Adapters.UTF_8.codecNbt()).packetCodec(Adapters.UTF_8));
    }

    public static <T> RegistrySupplier<ComponentType<T>> register(Identifier id, UnaryOperator<ComponentType.Builder<T>> item) {
        return register(DATA_COMPONENTS, id, () -> item.apply(ComponentType.builder()).build());
    }

}
