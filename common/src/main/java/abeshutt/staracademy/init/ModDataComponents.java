package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.item.OutfitEntry;
import abeshutt.staracademy.item.SafariTicketEntry;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponents extends ModRegistries {

    public static RegistrySupplier<ComponentType<String>> SAFARI_TICKET_ENTRY;
    public static RegistrySupplier<ComponentType<OutfitEntry>> OUTFIT_ENTRY;

    public static void register() {
        SAFARI_TICKET_ENTRY = register(StarAcademyMod.id("safari_ticket_entry"), builder -> builder
                .codec(Codec.STRING).packetCodec(STRING_PACKET_CODEC));

        OUTFIT_ENTRY = register(StarAcademyMod.id("outfit_entry"), builder -> builder
                .codec(Adapters.OUTFIT_ENTRY.codecNbt()).packetCodec(Adapters.OUTFIT_ENTRY));
    }

    public static <T> RegistrySupplier<ComponentType<T>> register(Identifier id, UnaryOperator<ComponentType.Builder<T>> item) {
        return register(DATA_COMPONENTS, id, () -> item.apply(ComponentType.builder()).build());
    }

    public static final PacketCodec<RegistryByteBuf, String> STRING_PACKET_CODEC = new PacketCodec<>() {
        @Override
        public String decode(RegistryByteBuf buf) {
            return buf.readString();
        }

        @Override
        public void encode(RegistryByteBuf buf, String value) {
            buf.writeString(value);
        }
    };

}
