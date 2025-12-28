package abeshutt.staracademy.fabric.client;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.fabric.network.S2CUpdateSafariTimer;
import abeshutt.staracademy.init.ModRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class StarAcademyFabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModRenderers.Blocks.register(BlockRenderLayerMap.INSTANCE::putBlock);

        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateSafariTimer.ID, (packet, handler) -> {
            if (packet.end == null) {
                System.out.println("Received safari update packet for time: null");
            }
            else {
                System.out.println("Received safari update packet for time: " + packet.end.toEpochMilli());
            }
            StarAcademyMod.SAFARI_TIMER = packet.end;
        });
    }
}
