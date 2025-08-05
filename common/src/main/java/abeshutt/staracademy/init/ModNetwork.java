package abeshutt.staracademy.init;

import abeshutt.staracademy.net.*;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork extends ModRegistries {

    public static void register() {
        if(Platform.getEnvironment() == Env.CLIENT) {
            Client.register();
        } else {
            Server.register();
        }
    }

    public static class Client {
        public static final Function<NetworkManager.PacketContext, ClientPlayNetworkHandler> CLIENT_PLAY = context -> MinecraftClient.getInstance().getNetworkHandler();
        public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

        public static void register() {
            ModNetwork.register(NetworkManager.s2c(), UpdatePlayerProfileS2CPacket.ID, UpdatePlayerProfileS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateStarBadgeS2CPacket.ID, UpdateStarBadgeS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateStarterRaffleS2CPacket.ID, UpdateStarterRaffleS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateSafariS2CPacket.ID, UpdateSafariS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateSafariConfigS2CPacket.ID, UpdateSafariConfigS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateOutfitS2CPacket.ID, UpdateOutfitS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateShootingStarS2CPacket.ID, UpdateShootingStarS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.s2c(), UpdateHousesS2CPacket.ID, UpdateHousesS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(NetworkManager.c2s(), UpdateBetterStructureBlockC2SPacket.ID, UpdateBetterStructureBlockC2SPacket::new, SERVER_PLAY);
            ModNetwork.register(NetworkManager.c2s(), UpdateOutfitC2SPacket.ID, UpdateOutfitC2SPacket::new, SERVER_PLAY);
        }
    }

    public static class Server {
        public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

        public static void register() {
            ModNetwork.register(NetworkManager.s2c(), UpdatePlayerProfileS2CPacket.ID, UpdatePlayerProfileS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateStarBadgeS2CPacket.ID, UpdateStarBadgeS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateStarterRaffleS2CPacket.ID, UpdateStarterRaffleS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateSafariS2CPacket.ID, UpdateSafariS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateSafariConfigS2CPacket.ID, UpdateSafariConfigS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateOutfitS2CPacket.ID, UpdateOutfitS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateShootingStarS2CPacket.ID, UpdateShootingStarS2CPacket::new, null);
            ModNetwork.register(NetworkManager.s2c(), UpdateHousesS2CPacket.ID, UpdateHousesS2CPacket::new, null);
            ModNetwork.register(NetworkManager.c2s(), UpdateBetterStructureBlockC2SPacket.ID, UpdateBetterStructureBlockC2SPacket::new, SERVER_PLAY);
            ModNetwork.register(NetworkManager.c2s(), UpdateOutfitC2SPacket.ID, UpdateOutfitC2SPacket::new, SERVER_PLAY);
        }
    }

    public static <R extends PacketListener, T extends ModPacket<R>> void register(NetworkManager.Side side, CustomPayload.Id<T> id, Supplier<T> packetSupplier,
                                                                                   Function<NetworkManager.PacketContext, R> contextMapper) {
        if(Platform.getEnvironment() == Env.SERVER && side == NetworkManager.s2c()) {
            NetworkManager.registerS2CPayloadType(id, CustomPayload.codecOf(ModPacket::write, buf -> {
                T packet = packetSupplier.get();
                packet.read(buf);
                return packet;
            }));

            return;
        }

        NetworkManager.registerReceiver(side, id, CustomPayload.codecOf(ModPacket::write, buf -> {
            T packet = packetSupplier.get();
            packet.read(buf);
            return packet;
        }), (packet, context) -> {
            if (contextMapper != null) {
                packet.apply(contextMapper.apply(context));
            }
        });
    }

}
