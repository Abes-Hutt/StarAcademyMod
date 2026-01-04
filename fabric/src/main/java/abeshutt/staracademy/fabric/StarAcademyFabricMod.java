package abeshutt.staracademy.fabric;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.cosmetic.CosmeticsResourceReloadListener;
import abeshutt.staracademy.fabric.resource.FabricResourceReloadListener;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
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
    }

}
