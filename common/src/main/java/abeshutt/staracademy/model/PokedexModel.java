package abeshutt.staracademy.model;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.block.entity.HousePokedexBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedexModel extends GeoModel<HousePokedexBlockEntity> {

    @Override
    public Identifier getModelResource(HousePokedexBlockEntity animatable) {
        return StarAcademyMod.id("geo/pokedex.geo.json");
    }

    @Override
    public Identifier getAnimationResource(HousePokedexBlockEntity animatable) {
        return StarAcademyMod.id("animations/pokedex.animation.json");
    }

    @Override
    public Identifier getTextureResource(HousePokedexBlockEntity animatable) {
        return StarAcademyMod.id("textures/pokedex.png");
    }

}
