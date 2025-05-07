package abeshutt.staracademy.item;

import abeshutt.staracademy.item.renderer.BoosterPackItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ISpecialItemModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class BoosterPackItem extends Item implements ISpecialItemModel {

    public BoosterPackItem() {
        super(new Settings().fireproof());
    }

    @Override
    public void loadModels(Consumer<ModelIdentifier> consumer) {
        //TODO: load models based on booster pack config
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return BoosterPackItemRenderer.INSTANCE;
    }

}
