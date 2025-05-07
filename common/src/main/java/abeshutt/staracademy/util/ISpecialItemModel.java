package abeshutt.staracademy.util;

import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ModelIdentifier;

import java.util.function.Consumer;

public interface ISpecialItemModel {

    @Environment(EnvType.CLIENT)
    void loadModels(Consumer<ModelIdentifier> consumer);

    @Environment(EnvType.CLIENT)
    SpecialItemRenderer getRenderer();

}
