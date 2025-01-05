package abeshutt.staracademy.outfit.models;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import abeshutt.staracademy.outfit.core.OutfitTexture;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

public class Comfy2Outfit {

    public static class Hat extends OutfitPiece {

        public Hat(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(40, 10).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 0).cuboid(-5.0F, -2.0F, -5.0F, 10.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.2243F, 0.3916F, -0.1231F, -0.0447F, -0.3463F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/comfy2_top.png"),
                    StarAcademyMod.mid("outfit/comfy2_hat", "inventory")
            );
        }

    }

    public static class Shirt extends OutfitPiece {

        public Shirt(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 14).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.75F))
                    .uv(40, 0).cuboid(-2.0F, 2.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r2 = body.addChild("cube_r2", ModelPartBuilder.create().uv(48, 4).cuboid(-2.0F, -4.25F, 0.0F, 4.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 7.0F, -3.5F, 0.0F, 0.0F, -0.3927F));

            ModelPartData cube_r3 = body.addChild("cube_r3", ModelPartBuilder.create().uv(40, 4).cuboid(-2.0F, -4.25F, 0.0F, 4.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 7.0F, -3.5F, 0.0F, 0.0F, 0.3927F));

            ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(0, 30).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.74F))
                    .uv(24, 14).cuboid(-4.5F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

            ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(16, 32).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.74F))
                    .uv(24, 23).cuboid(-0.5F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/comfy2_top.png"),
                    StarAcademyMod.mid("outfit/comfy2_shirt", "inventory")
            );
        }

    }

    public static class Skirt extends OutfitPiece {

        public Skirt(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.51F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(32, 22).cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.6861F, 16.5F, 0.0139F, 0.0F, 1.5708F, 0.3927F));

            ModelPartData cube_r2 = body.addChild("cube_r2", ModelPartBuilder.create().uv(14, 32).cuboid(-3.0F, -3.0F, -0.5F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 16.5F, 3.7F, 2.7489F, 0.0F, -3.1416F));

            ModelPartData cube_r3 = body.addChild("cube_r3", ModelPartBuilder.create().uv(32, 17).cuboid(-3.0F, -3.0F, -0.5F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 16.5F, -3.6722F, -0.3927F, 0.0F, 0.0F));

            ModelPartData cube_r4 = body.addChild("cube_r4", ModelPartBuilder.create().uv(32, 26).cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.6861F, 16.5F, 0.0139F, 0.0F, -1.5708F, -0.3927F));

            ModelPartData cube_r5 = body.addChild("cube_r5", ModelPartBuilder.create().uv(24, 6).cuboid(-4.0F, -3.0F, -0.5F, 8.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, -3.6722F, -0.3927F, 0.0F, 0.0F));

            ModelPartData cube_r6 = body.addChild("cube_r6", ModelPartBuilder.create().uv(32, 12).cuboid(-3.0F, -3.0F, -0.5F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.6861F, 14.0F, 0.0139F, 0.0F, 1.5708F, 0.3927F));

            ModelPartData cube_r7 = body.addChild("cube_r7", ModelPartBuilder.create().uv(0, 32).cuboid(-3.0F, -3.0F, -0.5F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.6861F, 14.0F, 0.0139F, 0.0F, -1.5708F, -0.3927F));

            ModelPartData cube_r8 = body.addChild("cube_r8", ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -3.0F, -0.5F, 8.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, 3.7F, 2.7489F, 0.0F, -3.1416F));

            ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

            ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/comfy2_bottom.png"),
                    StarAcademyMod.mid("outfit/comfy2_skirt", "inventory")
            );
        }

    }

    public static class Shoes extends OutfitPiece {

        public Shoes(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(32, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.75F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

            ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 46).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.75F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/comfy2_top.png"),
                    StarAcademyMod.mid("outfit/comfy2_shoes", "inventory")
            );
        }

    }

}
