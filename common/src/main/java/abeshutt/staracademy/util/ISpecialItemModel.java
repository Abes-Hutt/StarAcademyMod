package abeshutt.staracademy.util;

import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ISpecialItemModel {

    @Environment(EnvType.CLIENT)
    void loadModels(Stream<Identifier> unbakedModels, Consumer<ModelIdentifier> loader);

    @Environment(EnvType.CLIENT)
    SpecialItemRenderer getRenderer();

}
