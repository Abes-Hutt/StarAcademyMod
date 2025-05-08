package abeshutt.staracademy.item;

import abeshutt.staracademy.item.renderer.AlbumItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ISpecialItemModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class AlbumItem extends Item implements ISpecialItemModel {

    public AlbumItem() {
        super(new Settings().fireproof());
    }

    @Override
    public void loadModels(Consumer<ModelIdentifier> consumer) {
        //TODO: load models based on album config
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return AlbumItemRenderer.INSTANCE;
    }

}
