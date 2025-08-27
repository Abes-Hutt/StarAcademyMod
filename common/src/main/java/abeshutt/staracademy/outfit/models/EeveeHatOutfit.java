package abeshutt.staracademy.outfit.models;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.outfit.core.OutfitTexture;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

public class EeveeHatOutfit {

    public static class Vaporeon extends OutfitPiece {

        public Vaporeon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 19).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(0, 0).cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 0.0F, 19.0F, new Dilation(0.0F))
                    .uv(32, 19).cuboid(-1.5F, -1.25F, -14.0F, 3.0F, 3.0F, 7.0F, new Dilation(0.0F))
                    .uv(44, 38).cuboid(0.0F, -2.25F, -14.0F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F))
                    .uv(28, 44).cuboid(-0.5F, -17.0F, -1.0F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F))
                    .uv(8, 41).cuboid(0.0F, -17.0F, -5.0F, 0.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(22, 44).cuboid(-0.5F, -5.0F, 0.5F, 1.0F, 10.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 41).cuboid(0.0F, -5.0F, -3.5F, 0.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-6.9497F, -9.411F, -1.8421F, 0.5672F, 0.3927F, 0.0F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(36, 38).cuboid(-0.5F, -10.0F, -5.0F, 0.0F, 10.0F, 4.0F, new Dilation(0.0F))
                    .uv(16, 44).cuboid(-1.0F, -10.0F, -1.0F, 1.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.0F, 2.0F, 0.5672F, -0.3927F, 0.0F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(18, 38).cuboid(-0.9667F, -0.5F, -3.5189F, 4.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0489F, -0.75F, -16.4244F, -0.5299F, -0.7119F, 0.3655F));

            ModelPartData cube_r4 = head.addChild("cube_r4", ModelPartBuilder.create().uv(0, 35).cuboid(-3.145F, -0.5F, -3.3914F, 4.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0489F, -0.75F, -16.4244F, -0.4812F, 0.5973F, -0.2856F));

            ModelPartData cube_r5 = head.addChild("cube_r5", ModelPartBuilder.create().uv(44, 44).cuboid(0.0F, -3.0F, -4.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                    .uv(32, 29).cuboid(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.75F, -5.0F, 0.6109F, 0.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(128, 128,
                    StarAcademyMod.id("textures/entity/outfit/vaporeon_hat.png"),
                    StarAcademyMod.mid("outfit/vaporeon_hat", "inventory")
            );
        }

    }

    public static class Umbreon extends OutfitPiece {

        public Umbreon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(12, 31).cuboid(-1.0F, -10.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(20, 30).cuboid(-2.0F, -5.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-5.5444F, -11.1121F, 4.0F, -1.7666F, 0.7439F, -2.1691F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(0, 31).cuboid(-1.5F, -6.25F, -1.5F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F))
                    .uv(0, 16).cuboid(-2.5F, -1.25F, -2.5F, 5.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0889F, -6.8021F, 13.3117F, -1.5651F, 0.2528F, -1.606F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -10.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(20, 16).cuboid(-2.0F, -5.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.4556F, -12.1121F, 3.0F, -2.4279F, 0.3614F, -2.4384F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/umbreon_hat.png"),
                    StarAcademyMod.mid("outfit/umbreon_hat", "inventory")
            );
        }

    }

}
