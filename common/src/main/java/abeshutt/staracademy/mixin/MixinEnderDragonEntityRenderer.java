package abeshutt.staracademy.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonEntityRenderer.class)
public class MixinEnderDragonEntityRenderer {

    @Inject(method = "getTexturedModelData", at = @At("HEAD"), cancellable = true)
    private static void getTexturedModelData(CallbackInfoReturnable<TexturedModelData> ci) {
        ModelData modelData = new ModelData();
        ModelPartData partdefinition = modelData.getRoot();

        ModelPartData head = partdefinition.addChild("head", ModelPartBuilder.create().uv(138, 290).cuboid(-11.0F, -8.0F, -27.0F, 21.0F, 13.0F, 27.0F, new Dilation(0.0F))
                .uv(186, 330).cuboid(-18.0F, -5.0F, -16.0F, 7.0F, 7.0F, 12.0F, new Dilation(0.0F))
                .uv(292, 417).cuboid(9.0F, -5.0F, -16.0F, 7.0F, 7.0F, 12.0F, new Dilation(0.0F))
                .uv(138, 163).cuboid(-5.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(138, 169).cuboid(3.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 17.0F, -58.0F));

        ModelPartData head_r1 = head.addChild("head_r1", ModelPartBuilder.create().uv(152, 161).cuboid(-10.5F, -2.5F, -26.25F, 21.0F, 5.0F, 54.0F, new Dilation(0.0F)), ModelTransform.of(11.5F, -14.1994F, -12.5806F, 0.6682F, 0.3879F, 0.2902F));

        ModelPartData head_r2 = head.addChild("head_r2", ModelPartBuilder.create().uv(152, 102).cuboid(-10.5F, -2.5F, -25.75F, 21.0F, 5.0F, 54.0F, new Dilation(0.0F)), ModelTransform.of(-12.5F, -14.1994F, -12.5806F, 0.6682F, -0.3879F, -0.2902F));

        ModelPartData head_r3 = head.addChild("head_r3", ModelPartBuilder.create().uv(0, 233).cuboid(2.5F, -3.0F, -29.0F, 5.0F, 6.0F, 64.0F, new Dilation(0.0F)), ModelTransform.of(11.5F, -39.0255F, 11.5423F, 0.6181F, 0.1427F, 0.1008F));

        ModelPartData head_r4 = head.addChild("head_r4", ModelPartBuilder.create().uv(276, 220).cuboid(-0.5F, -3.0F, -32.0F, 5.0F, 6.0F, 64.0F, new Dilation(0.0F)), ModelTransform.of(34.5866F, -36.3915F, 7.7806F, 0.6578F, 0.3535F, 0.2613F));

        ModelPartData head_r5 = head.addChild("head_r5", ModelPartBuilder.create().uv(204, 0).cuboid(-2.5F, -3.0F, -32.0F, 5.0F, 6.0F, 64.0F, new Dilation(0.0F)), ModelTransform.of(-37.5104F, -36.172F, 7.4671F, 0.6578F, -0.3535F, -0.2613F));

        ModelPartData head_r6 = head.addChild("head_r6", ModelPartBuilder.create().uv(0, 163).cuboid(-7.5F, -3.0F, -29.0F, 5.0F, 6.0F, 64.0F, new Dilation(0.0F)), ModelTransform.of(-13.5F, -39.0255F, 11.5423F, 0.6222F, -0.1782F, -0.1265F));

        ModelPartData head_r7 = head.addChild("head_r7", ModelPartBuilder.create().uv(428, 92).cuboid(-1.0F, -3.0F, -13.75F, 6.0F, 7.0F, 11.0F, new Dilation(0.0F))
                .uv(0, 337).cuboid(-2.0F, -5.0F, -2.75F, 8.0F, 9.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(-25.1681F, -19.3632F, -16.5383F, 0.6682F, -0.3879F, -0.2902F));

        ModelPartData head_r8 = head.addChild("head_r8", ModelPartBuilder.create().uv(426, 363).cuboid(-3.0F, -2.0F, -13.75F, 6.0F, 7.0F, 11.0F, new Dilation(0.0F))
                .uv(332, 70).cuboid(-4.0F, -4.0F, -2.75F, 8.0F, 9.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -21.9204F, -14.6298F, 0.6222F, -0.1782F, -0.1265F));

        ModelPartData head_r9 = head.addChild("head_r9", ModelPartBuilder.create().uv(430, 20).cuboid(-4.0F, -3.0F, -13.75F, 6.0F, 7.0F, 11.0F, new Dilation(0.0F))
                .uv(296, 346).cuboid(-5.0F, -5.0F, -2.75F, 8.0F, 9.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(22.2497F, -19.2257F, -16.7347F, 0.6682F, 0.3879F, 0.2902F));

        ModelPartData head_r10 = head.addChild("head_r10", ModelPartBuilder.create().uv(428, 291).cuboid(-3.75F, -2.0F, -13.75F, 6.0F, 7.0F, 11.0F, new Dilation(0.0F))
                .uv(342, 0).cuboid(-4.75F, -4.0F, -2.75F, 8.0F, 9.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(10.5F, -21.9204F, -14.6298F, 0.6222F, 0.1782F, 0.1265F));

        ModelPartData head_r11 = head.addChild("head_r11", ModelPartBuilder.create().uv(342, 31).cuboid(-4.5F, -17.5F, 15.0F, 8.0F, 9.0F, 22.0F, new Dilation(0.0F))
                .uv(428, 273).cuboid(-3.5F, -15.5F, 4.0F, 6.0F, 7.0F, 11.0F, new Dilation(0.0F))
                .uv(138, 220).cuboid(-3.0F, -14.5F, 21.0F, 5.0F, 6.0F, 64.0F, new Dilation(0.0F))
                .uv(0, 102).cuboid(-11.0F, -8.5F, -13.0F, 21.0F, 6.0F, 55.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, -22.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create().uv(28, 368).cuboid(5.75F, 0.75F, -12.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F))
                .uv(428, 110).cuboid(-11.0F, 0.5F, -12.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, -13.0F));

        ModelPartData jaw_r1 = jaw.addChild("jaw_r1", ModelPartBuilder.create().uv(434, 196).cuboid(-7.0F, -2.0F, -3.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(7.75F, 2.75F, -20.0F, 0.0F, 0.6109F, 0.0F));

        ModelPartData jaw_r2 = jaw.addChild("jaw_r2", ModelPartBuilder.create().uv(170, 441).cuboid(3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 2.5F, -20.0F, 0.0F, -0.6109F, 0.0F));

        ModelPartData neck = partdefinition.addChild("neck", ModelPartBuilder.create().uv(256, 349).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(426, 430).cuboid(-1.0F, -12.0F, -8.0F, 2.0F, 23.0F, 6.0F, new Dilation(0.0F))
                .uv(446, 319).cuboid(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(448, 125).cuboid(-1.0F, 11.0F, -10.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 17.0F, -8.0F));

        /*
        ModelPartData neck2 = neck.addChild("neck2", ModelPartBuilder.create().uv(388, 290).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(432, 381).cuboid(-1.0F, -12.0F, -8.0F, 2.0F, 23.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 444).cuboid(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(446, 309).cuboid(-1.0F, 11.0F, -10.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -10.0F));

        ModelPartData neck3 = neck2.addChild("neck3", ModelPartBuilder.create().uv(252, 397).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(108, 433).cuboid(-1.0F, -12.0F, -8.0F, 2.0F, 23.0F, 6.0F, new Dilation(0.0F))
                .uv(442, 440).cuboid(-1.0F, 11.0F, -10.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(442, 430).cuboid(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -10.0F));

        ModelPartData neck4 = neck3.addChild("neck4", ModelPartBuilder.create().uv(292, 397).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(124, 433).cuboid(-1.0F, -12.0F, -8.0F, 2.0F, 23.0F, 6.0F, new Dilation(0.0F))
                .uv(442, 10).cuboid(-1.0F, 11.0F, -10.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(442, 0).cuboid(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -10.0F));

        ModelPartData neck5 = neck4.addChild("neck5", ModelPartBuilder.create().uv(402, 0).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(434, 167).cuboid(-1.0F, -12.0F, -8.0F, 2.0F, 23.0F, 6.0F, new Dilation(0.0F))
                .uv(432, 332).cuboid(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(198, 441).cuboid(-1.0F, 11.0F, -10.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
*/
        ModelPartData body = partdefinition.addChild("body", ModelPartBuilder.create().uv(402, 20).cuboid(-1.0F, -31.0F, -10.0F, 2.0F, 20.0F, 12.0F, new Dilation(0.0F))
                .uv(148, 421).cuboid(-1.0F, -35.0F, -10.0F, 2.0F, 4.0F, 16.0F, new Dilation(0.0F))
                .uv(184, 421).cuboid(-1.0F, -45.0F, 10.0F, 2.0F, 4.0F, 16.0F, new Dilation(0.0F))
                .uv(426, 343).cuboid(-1.0F, -34.0F, 30.0F, 2.0F, 4.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 368).cuboid(-1.0F, -41.0F, 10.0F, 2.0F, 33.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 413).cuboid(-1.0F, -30.0F, 30.0F, 2.0F, 19.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, 8.0F));

        ModelPartData body_r1 = body.addChild("body_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-19.0F, -19.0F, -32.0F, 38.0F, 38.0F, 64.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, 16.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData right_wing = partdefinition.addChild("right_wing", ModelPartBuilder.create().uv(204, 70).cuboid(-56.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(252, 437).cuboid(-13.0F, -2.0F, 4.0F, 7.0F, 5.0F, 8.0F, new Dilation(0.0F))
                .uv(140, 441).cuboid(-24.0F, -2.0F, 4.0F, 7.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-12.0F, 5.0F, 2.0F));

        ModelPartData right_wing_tip = right_wing.addChild("right_wing_tip", ModelPartBuilder.create(), ModelTransform.pivot(-56.0F, 0.0F, -2.0F));

        ModelPartData wingtip_r1 = right_wing_tip.addChild("wingtip_r1", ModelPartBuilder.create().uv(62, 303).cuboid(-23.1816F, -2.3681F, -11.2853F, 23.0F, 8.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-14.5722F, -10.7433F, 13.3344F, 0.4783F, 0.9143F, 1.0498F));

        ModelPartData wingtip_r2 = right_wing_tip.addChild("wingtip_r2", ModelPartBuilder.create().uv(356, 366).cuboid(-5.5F, -4.0F, -8.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(376, 332).cuboid(-76.5F, -3.25F, 23.5F, 23.0F, 6.0F, 5.0F, new Dilation(0.0F))
                .uv(186, 349).cuboid(-53.5F, -4.0F, 20.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(270, 377).cuboid(-41.5F, -4.0F, 12.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(200, 369).cuboid(-29.5F, -4.0F, 5.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(364, 173).cuboid(-17.5F, -4.0F, -1.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-17.3684F, -36.7457F, 51.3723F, 0.6709F, 0.6978F, 1.2987F));

        ModelPartData wingtip_r3 = right_wing_tip.addChild("wingtip_r3", ModelPartBuilder.create().uv(60, 364).cuboid(-6.5F, -7.0F, -7.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-20.7822F, -24.7363F, 38.0262F, 1.0514F, 0.9899F, 1.798F));

        ModelPartData wingtip_r4 = right_wing_tip.addChild("wingtip_r4", ModelPartBuilder.create().uv(302, 127).cuboid(-12.506F, -2.711F, -12.1668F, 23.0F, 8.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(-14.5722F, -10.7433F, 13.3344F, 0.1797F, 0.6784F, 0.6744F));

        ModelPartData wingtip_r5 = right_wing_tip.addChild("wingtip_r5", ModelPartBuilder.create().uv(234, 290).cuboid(-1.3468F, -1.0067F, -17.6832F, 23.0F, 8.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(-14.5722F, -10.7433F, 13.3344F, 0.0F, 0.3054F, 0.3927F));

        ModelPartData left_wing = partdefinition.addChild("left_wing", ModelPartBuilder.create().uv(204, 86).cuboid(0.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(430, 38).cuboid(8.0F, -2.0F, 4.0F, 7.0F, 5.0F, 8.0F, new Dilation(0.0F))
                .uv(292, 436).cuboid(19.0F, -2.0F, 4.0F, 7.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(12.0F, 5.0F, 2.0F));

        ModelPartData left_wing_tip = left_wing.addChild("left_wing_tip", ModelPartBuilder.create(), ModelTransform.pivot(56.0F, 0.0F, -2.0F));

        ModelPartData wingtip_r6 = left_wing_tip.addChild("wingtip_r6", ModelPartBuilder.create().uv(356, 346).cuboid(30.5F, -4.0F, 20.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(378, 147).cuboid(18.5F, -4.0F, 12.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(378, 127).cuboid(6.5F, -4.0F, 5.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(376, 312).cuboid(-5.5F, -4.0F, -1.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(130, 369).cuboid(-17.5F, -4.0F, -8.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F))
                .uv(302, 207).cuboid(53.5F, -3.25F, 23.5F, 23.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(17.3684F, -36.7457F, 51.3723F, 0.6709F, -0.6978F, -1.2987F));

        ModelPartData wingtip_r7 = left_wing_tip.addChild("wingtip_r7", ModelPartBuilder.create().uv(364, 193).cuboid(-16.5F, -7.0F, -7.0F, 23.0F, 8.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(20.7822F, -24.7363F, 38.0262F, 1.0514F, -0.9899F, -1.798F));

        ModelPartData wingtip_r8 = left_wing_tip.addChild("wingtip_r8", ModelPartBuilder.create().uv(314, 290).cuboid(0.1816F, -2.3681F, -11.2853F, 23.0F, 8.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(14.5722F, -10.7433F, 13.3344F, 0.4783F, -0.9143F, -1.0498F));

        ModelPartData wingtip_r9 = left_wing_tip.addChild("wingtip_r9", ModelPartBuilder.create().uv(302, 102).cuboid(-21.6532F, -1.0067F, -17.6832F, 23.0F, 8.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(14.5722F, -10.7433F, 13.3344F, 0.0F, -0.3054F, -0.3927F));

        ModelPartData wingtip_r10 = left_wing_tip.addChild("wingtip_r10", ModelPartBuilder.create().uv(302, 150).cuboid(-10.494F, -2.711F, -12.1668F, 23.0F, 8.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(14.5722F, -10.7433F, 13.3344F, 0.1797F, -0.6784F, -0.6744F));

        ModelPartData right_hind_leg = partdefinition.addChild("right_hind_leg", ModelPartBuilder.create().uv(230, 389).cuboid(-3.0F, -4.0F, -3.0F, 4.0F, 32.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-16.0F, 16.0F, 42.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData rearlegtip = right_hind_leg.addChild("right_hind_leg_tip", ModelPartBuilder.create().uv(76, 428).cuboid(-3.0F, 0.0F, -1.0F, 4.0F, 32.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 28.0F, 1.0F, 0.4363F, 0.0F, 0.0F));

        ModelPartData rearfoot = rearlegtip.addChild("right_hind_foot", ModelPartBuilder.create().uv(302, 173).cuboid(-9.0F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F))
                .uv(270, 369).cuboid(-6.0F, 0.5F, -1.0F, 10.0F, 5.0F, 3.0F, new Dilation(0.0F))
                .uv(62, 325).cuboid(-2.25F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F))
                .uv(314, 312).cuboid(4.0F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 32.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData left_hind_leg = partdefinition.addChild("left_hind_leg", ModelPartBuilder.create().uv(392, 62).cuboid(-1.0F, -4.0F, -3.0F, 4.0F, 32.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(16.0F, 16.0F, 42.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData rearlegtip1 = left_hind_leg.addChild("left_hind_leg_tip", ModelPartBuilder.create().uv(92, 428).cuboid(-1.0F, 0.0F, -1.0F, 4.0F, 32.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 28.0F, 1.0F, 0.4363F, 0.0F, 0.0F));

        ModelPartData rearfoot1 = rearlegtip1.addChild("left_hind_foot", ModelPartBuilder.create().uv(0, 303).cuboid(-7.0F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F))
                .uv(234, 315).cuboid(-0.75F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F))
                .uv(342, 62).cuboid(-4.0F, 0.5F, -1.0F, 10.0F, 5.0F, 3.0F, new Dilation(0.0F))
                .uv(124, 330).cuboid(6.0F, 0.0F, -20.0F, 3.0F, 6.0F, 28.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 32.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData right_front_leg = partdefinition.addChild("right_front_leg", ModelPartBuilder.create().uv(166, 389).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 20.0F, 2.0F, 1.1345F, 0.0F, 0.0F));

        ModelPartData frontlegtip = right_front_leg.addChild("right_front_leg_tip", ModelPartBuilder.create().uv(28, 428).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 20.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData frontfoot = frontlegtip.addChild("right_front_foot", ModelPartBuilder.create().uv(382, 101).cuboid(-6.0F, 0.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F))
                .uv(386, 386).cuboid(-1.0F, -7.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F))
                .uv(74, 384).cuboid(3.0F, 0.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 22.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData left_front_leg = partdefinition.addChild("left_front_leg", ModelPartBuilder.create().uv(198, 389).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 20.0F, 2.0F, 1.1345F, 0.0F, 0.0F));

        ModelPartData frontlegtip1 = left_front_leg.addChild("left_front_leg_tip", ModelPartBuilder.create().uv(52, 428).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 20.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData frontfoot1 = frontlegtip1.addChild("left_front_foot", ModelPartBuilder.create().uv(28, 384).cuboid(-6.0F, 0.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F))
                .uv(340, 386).cuboid(-2.0F, -7.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F))
                .uv(120, 389).cuboid(3.0F, 0.0F, -16.0F, 3.0F, 4.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 22.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        /*
        ModelPartData tail = partdefinition.addChild("tail", ModelPartBuilder.create().uv(28, 408).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(296, 315).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(448, 135).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(448, 145).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, 56.0F));

        ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create().uv(68, 408).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(220, 428).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(448, 155).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(448, 381).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail3 = tail2.addChild("tail3", ModelPartBuilder.create().uv(332, 410).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(282, 449).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(448, 391).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(236, 428).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail4 = tail3.addChild("tail4", ModelPartBuilder.create().uv(372, 410).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(330, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(302, 449).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(450, 165).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail5 = tail4.addChild("tail5", ModelPartBuilder.create().uv(412, 410).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(346, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(450, 175).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(450, 185).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail6 = tail5.addChild("tail6", ModelPartBuilder.create().uv(108, 413).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(362, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(252, 450).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(442, 450).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail7 = tail6.addChild("tail7", ModelPartBuilder.create().uv(414, 52).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(378, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F))
                .uv(198, 451).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(452, 329).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail8 = tail7.addChild("tail8", ModelPartBuilder.create().uv(452, 411).cuboid(-1.0F, 11.0F, 0.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(452, 401).cuboid(-1.0F, -15.0F, 2.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(414, 72).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(394, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail9 = tail8.addChild("tail9", ModelPartBuilder.create().uv(414, 213).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(410, 430).cuboid(-1.0F, -13.0F, 2.0F, 2.0F, 24.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail10 = tail9.addChild("tail10", ModelPartBuilder.create().uv(414, 233).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail11 = tail10.addChild("tail11", ModelPartBuilder.create().uv(414, 253).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));

        ModelPartData tail12 = tail11.addChild("tail12", ModelPartBuilder.create().uv(252, 417).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 10.0F));*/
        ci.setReturnValue(TexturedModelData.of(modelData, 512, 512));
    }

}
