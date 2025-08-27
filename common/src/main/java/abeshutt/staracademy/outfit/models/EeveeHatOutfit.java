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

    public static class Glaceon extends OutfitPiece {

        public Glaceon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(24, 32).cuboid(-2.0F, -11.0F, -6.0F, 4.0F, 7.0F, 2.0F, new Dilation(0.0F))
                    .uv(36, 16).cuboid(-4.0F, -10.0F, -6.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 16).cuboid(-7.0F, -10.0F, -5.5F, 6.0F, 24.0F, 0.0F, new Dilation(0.0F))
                    .uv(12, 16).cuboid(1.0F, -10.0F, -5.5F, 6.0F, 24.0F, 0.0F, new Dilation(0.0F))
                    .uv(36, 23).cuboid(2.0F, -10.0F, -6.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(24, 16).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.5F, -16.0F, -0.5F, 0.0F, 0.0F, 0.3927F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 0).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -16.0F, -0.5F, 0.0F, 0.0F, -0.3927F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/glaceon_hat.png"),
                    StarAcademyMod.mid("outfit/glaceon_hat", "inventory")
            );
        }

    }

    public static class Eevee extends OutfitPiece {

        public Eevee(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(0, 0).cuboid(-6.0F, -7.0F, -6.0F, 12.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(24, 32).cuboid(-3.0F, -6.0F, 0.0F, 6.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 16).cuboid(-3.0F, -6.0F, 0.0F, 6.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -13.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(36, 28).cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 32).cuboid(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.0F, 9.5F, 0.6981F, 0.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/eevee_hat.png"),
                    StarAcademyMod.mid("outfit/eevee_hat", "inventory")
            );
        }

    }

    public static class Espeon extends OutfitPiece {

        public Espeon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(28, 28).cuboid(-1.0F, -8.0F, -6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(14, 28).cuboid(-0.1537F, -5.1526F, -1.0F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F))
                    .uv(14, 16).cuboid(-1.1537F, -4.1526F, -1.0F, 5.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(10.0598F, -7.8657F, 0.0F, 3.1416F, 0.0F, -2.0508F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(28, 22).cuboid(-5.4482F, 0.9005F, -0.5F, 3.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.0598F, -7.8657F, 0.0F, -3.1416F, 0.0F, -1.6581F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(28, 16).cuboid(-4.9139F, 0.622F, -0.5F, 3.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.4593F, -7.3092F, 0.0F, 0.0F, 0.0F, -1.4835F));

            ModelPartData cube_r4 = head.addChild("cube_r4", ModelPartBuilder.create().uv(0, 28).cuboid(-1.5407F, -4.6908F, -1.0F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 16).cuboid(-2.5407F, -3.6908F, -1.0F, 5.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.4593F, -9.3092F, 0.0F, 0.0F, 0.0F, -1.0908F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/espeon_hat.png"),
                    StarAcademyMod.mid("outfit/espeon_hat", "inventory")
            );
        }

    }

    public static class Flareon extends OutfitPiece {

        public Flareon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(0, 0).cuboid(-5.5F, -7.0F, -5.5F, 11.0F, 3.0F, 11.0F, new Dilation(0.0F))
                    .uv(32, 32).cuboid(-3.0F, -11.0F, -4.0F, 6.0F, 2.0F, 8.0F, new Dilation(0.0F))
                    .uv(36, 26).cuboid(-1.0F, -12.0F, 1.0F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(44, 42).cuboid(-3.0F, -8.5F, 0.0F, 6.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -16.5F, 0.0F, 0.0F, 0.0F, 0.3054F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 42).cuboid(-3.0F, -8.5F, 0.0F, 6.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, -16.5F, 0.0F, 0.0F, 0.0F, -0.3054F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(44, 0).cuboid(-1.5F, -5.0F, 1.0F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
                    .uv(36, 14).cuboid(-2.5F, -2.0F, 1.0F, 5.0F, 7.0F, 5.0F, new Dilation(0.0F))
                    .uv(0, 14).cuboid(-4.5F, -1.0F, -8.0F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 12.5F, 0.2618F, 0.0F, 0.0F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/flareon_hat.png"),
                    StarAcademyMod.mid("outfit/flareon_hat", "inventory")
            );
        }

    }

    public static class Jolteon extends OutfitPiece {

        public Jolteon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(0, 0).cuboid(-9.0F, -5.25F, -9.0F, 18.0F, 0.0F, 18.0F, new Dilation(0.0F))
                    .uv(0, 18).cuboid(-7.0F, -6.0F, -7.0F, 14.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(40, 32).cuboid(-2.0F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -15.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 32).cuboid(-2.0F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, -15.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(128, 128,
                    StarAcademyMod.id("textures/entity/outfit/jolteon_hat.png"),
                    StarAcademyMod.mid("outfit/jolteon_hat", "inventory")
            );
        }

    }

    public static class Leafeon extends OutfitPiece {

        public Leafeon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(24, 16).cuboid(-3.0F, -17.0F, -5.25F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F))
                    .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(32, 0).cuboid(2.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(32, 11).cuboid(2.5F, -4.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(8, 32).cuboid(-3.5F, -4.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(0, 32).cuboid(-4.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                    .uv(32, 7).cuboid(-1.0F, -5.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(24, 27).cuboid(-2.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.75F, -1.5F, -1.0036F, 0.0F, 0.0F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.5F, -16.0F, -0.5F, 0.0F, 0.0F, 0.3927F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(12, 16).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -16.0F, -0.5F, 0.0F, 0.0F, -0.3927F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/leafeon_hat.png"),
                    StarAcademyMod.mid("outfit/leafeon_hat", "inventory")
            );
        }

    }

    public static class Sylveon extends OutfitPiece {

        public Sylveon(String id) {
            super(id);
        }

        @Override
        protected void buildMesh(ModelPartData modelPartData) {
            ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                    .uv(0, 16).cuboid(-13.0F, -14.0F, 0.0F, 18.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

            ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(32, 11).cuboid(1.0F, -5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                    .uv(32, 0).cuboid(-4.0F, -5.0F, -1.0F, 5.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(9.0F, -6.0F, -2.0F, 0.0F, 0.0F, -0.0873F));

            ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(32, 7).cuboid(-2.0F, -7.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(24, 28).cuboid(-4.0F, -5.0F, -1.0F, 5.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -9.0F, -2.0F, 0.0F, 0.0F, -0.0873F));

            ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(0, 22).cuboid(-3.5F, -1.5F, 0.0F, 16.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-10.7975F, -13.5F, -1.9182F, 0.0F, 0.6109F, -0.6109F));

            ModelPartData cube_r4 = head.addChild("cube_r4", ModelPartBuilder.create().uv(0, 19).cuboid(-7.25F, -1.5F, 2.25F, 18.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(9.0F, -9.0F, 6.5F, 0.0F, 0.829F, 0.6981F));

            ModelPartData cube_r5 = head.addChild("cube_r5", ModelPartBuilder.create().uv(0, 25).cuboid(-9.0F, -1.5F, 0.0F, 14.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, -6.5F, 0.0F, 0.0F, 0.0F, 0.6981F));

            ModelPartData cube_r6 = head.addChild("cube_r6", ModelPartBuilder.create().uv(0, 28).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.5F, -16.0F, -0.5F, 0.0F, 0.0F, 0.3927F));

            ModelPartData cube_r7 = head.addChild("cube_r7", ModelPartBuilder.create().uv(12, 28).cuboid(-3.0F, -8.0F, 0.0F, 6.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -16.0F, -0.5F, 0.0F, 0.0F, -0.3927F));
        }

        @Override
        protected OutfitTexture buildTexture() {
            return new OutfitTexture(64, 64,
                    StarAcademyMod.id("textures/entity/outfit/sylveon_hat.png"),
                    StarAcademyMod.mid("outfit/sylveon_hat", "inventory")
            );
        }

    }

}
