package abeshutt.staracademy.outfit.models;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.outfit.core.OutfitTexture;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

public class MimikyuOutfit {

    public static class Hat extends OutfitPiece {

        public Hat(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(52, 54).cuboid(-3.6175F, -6.9199F, 0.0F, 10.0F, 12.0F, 0.0F, new Dilation(0.0F))
                    .uv(12, 57).cuboid(-0.6175F, 2.0801F, -1.5F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(5.6175F, -15.0801F, 0.5F, 0.0F, 0.0F, 0.4363F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 54).cuboid(-4.6175F, -6.9199F, 0.0F, 10.0F, 12.0F, 0.0F, new Dilation(0.0F))
                    .uv(0, 57).cuboid(-0.6175F, 2.0801F, -1.5F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-7.3825F, -14.0801F, 0.5F, 0.0F, 0.0F, -0.48F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(128, 128,
                    StarAcademyMod.id("textures/entity/outfit/mimikyu_top.png"),
                    StarAcademyMod.mid("outfit/mimikyu_hat", "inventory")
            );
        }

    }

    public static class Chestplate extends OutfitPiece {

        public Chestplate(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 16.0F, 6.0F, new Dilation(1.01F))
                    .uv(58, 28).cuboid(-2.0F, 0.0F, -5.3F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData RightArmArmor_r1 = body.addChild("RightArmArmor_r1", ModelPartBuilder.create().uv(32, 16).cuboid(-2.75F, -4.75F, -3.0F, 7.0F, 13.0F, 6.0F, new Dilation(1.0F)), ModelTransform.of(-6.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

            ModelPartData LeftArmArmor_r1 = body.addChild("LeftArmArmor_r1", ModelPartBuilder.create().uv(32, 35).cuboid(-4.5F, -5.0F, -3.0F, 7.0F, 13.0F, 6.0F, new Dilation(1.0F)), ModelTransform.of(6.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

            ModelPartData cube_r3 = body.addChild("cube_r3", ModelPartBuilder.create().uv(58, 22).cuboid(-7.0F, -3.0F, -1.0F, 6.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 1.0F, -5.3F, 0.0F, 0.0F, -0.2182F));

            ModelPartData cube_r4 = body.addChild("cube_r4", ModelPartBuilder.create().uv(58, 33).cuboid(-1.0F, -3.5F, -0.5F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 5.5F, -4.3F, 0.0F, 0.0F, -0.5672F));

            ModelPartData cube_r5 = body.addChild("cube_r5", ModelPartBuilder.create().uv(24, 57).cuboid(-1.0F, -3.5F, -0.5F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 5.5F, -4.3F, 0.0F, 0.0F, 0.5672F));

            ModelPartData cube_r6 = body.addChild("cube_r6", ModelPartBuilder.create().uv(58, 16).cuboid(-7.0F, -1.0F, -1.0F, 6.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -5.3F, 0.0F, 0.0F, 0.3054F));

            ModelPartData Chestplate_r1 = body.addChild("Chestplate_r1", ModelPartBuilder.create().uv(0, 22).cuboid(-5.0F, -5.0F, 1.0F, 10.0F, 13.0F, 6.0F, new Dilation(1.01F)), ModelTransform.of(0.0F, 8.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(128, 128,
                    StarAcademyMod.id("textures/entity/outfit/mimikyu_top.png"),
                    StarAcademyMod.mid("outfit/mimikyu_chestplate", "inventory")
            );
        }

    }

    public static class Tail extends OutfitPiece {

        public Tail(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 17).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.51F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -5.25F, -1.5F, 2.0F, 5.0F, 12.0F, new Dilation(0.0F))
                    .uv(20, 33).cuboid(-1.0F, -0.25F, -4.5F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                    .uv(0, 33).cuboid(-1.0F, 1.75F, -8.5F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 5.4784F, 6.352F, 0.4812F, 0.5973F, 0.2856F));

            ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(24, 17).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

            ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(28, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/mimikyu_bottom.png"),
                    StarAcademyMod.mid("outfit/mimikyu_tail", "inventory")
            );
        }

    }

    public static class Shoes extends OutfitPiece {

        public Shoes(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 41).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

            ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 41).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(128, 128,
                    StarAcademyMod.id("textures/entity/outfit/mimikyu_top.png"),
                    StarAcademyMod.mid("outfit/mimikyu_shoes", "inventory")
            );
        }

    }

}
