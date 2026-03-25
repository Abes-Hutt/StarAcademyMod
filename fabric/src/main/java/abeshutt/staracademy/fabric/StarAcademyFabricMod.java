package abeshutt.staracademy.fabric;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.cosmetic.CosmeticsResourceReloadListener;
import abeshutt.staracademy.fabric.resource.FabricResourceReloadListener;
import abeshutt.staracademy.net.UpdateCardGradingS2CPacket;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public final class StarAcademyFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        StarAcademyMod.init();

        if (Platform.getEnv() == EnvType.CLIENT) {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                    new FabricResourceReloadListener(StarAcademyMod.id("cosmetics"),
                            new CosmeticsResourceReloadListener()));
        }

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (StarAcademyMod.cardGradingService == null) return; // TODO: Remove this check, make flatfile fallback.
            var player = handler.player;
            StarAcademyMod.cardGradingService.getCardData(player).thenAccept(data -> {
                NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(data.orElse(null)));
            });
        });
    }

}
