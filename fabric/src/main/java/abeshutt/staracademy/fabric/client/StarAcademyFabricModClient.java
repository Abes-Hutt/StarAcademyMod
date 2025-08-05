package abeshutt.staracademy.fabric.client;

import abeshutt.staracademy.init.ModRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

public class StarAcademyFabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModRenderers.Blocks.register(BlockRenderLayerMap.INSTANCE::putBlock);
    }
}
