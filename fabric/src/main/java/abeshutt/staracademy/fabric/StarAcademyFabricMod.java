package abeshutt.staracademy.fabric;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.fabric.network.S2CUpdateSafariTimer;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class StarAcademyFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        if (!Platform.isModLoaded("safari")) {
            PayloadTypeRegistry.playS2C().register(S2CUpdateSafariTimer.ID, S2CUpdateSafariTimer.CODEC);
        }

        StarAcademyMod.init();
    }

}
