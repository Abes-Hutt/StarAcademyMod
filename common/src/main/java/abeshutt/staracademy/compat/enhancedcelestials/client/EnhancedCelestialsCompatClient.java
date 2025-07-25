package abeshutt.staracademy.compat.enhancedcelestials.client;

import abeshutt.staracademy.compat.enhancedcelestials.EnhancedCelestialsCompat;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.renderer.LunarForecastHologramBlockEntityRenderer;
import abeshutt.staracademy.init.ModRenderers;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.Map;

public class EnhancedCelestialsCompatClient {


    public static void registerBlockEntityRenderers(Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry) {
        ModRenderers.BlockEntities.register(registry, EnhancedCelestialsCompat.LUNAR_FORECAST_HOLOGRAM_BLOCK_ENTITY.get(), LunarForecastHologramBlockEntityRenderer::new);
    }
}
