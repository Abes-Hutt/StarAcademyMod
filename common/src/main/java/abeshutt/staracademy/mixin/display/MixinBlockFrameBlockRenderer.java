package abeshutt.staracademy.mixin.display;

import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "com.ddmc.display.client.BlockFrameBlockRenderer" })
public class MixinBlockFrameBlockRenderer {

    @Redirect(method = "render(Lcom/ddmc/display/blockentity/BlockFrameBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;",
                    ordinal = 0))
    public Quaternionf rotationDegrees(RotationAxis instance, float degrees) {
        float delta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        return instance.rotationDegrees(ClientScheduler.getTick(delta) * 4.0F % 360.0F);
    }

}
