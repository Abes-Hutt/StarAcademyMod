package abeshutt.staracademy.mixin.fixes;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlacedFeature.class)
public abstract class MixinPlacedFeature {

    @Shadow protected abstract boolean generate(FeaturePlacementContext context, Random random, BlockPos pos);

    @Redirect(method = "generateUnregistered", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/PlacedFeature;generate(Lnet/minecraft/world/gen/feature/FeaturePlacementContext;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean generateUnregistered(PlacedFeature feature, FeaturePlacementContext context, Random random, BlockPos pos) {
        return this.generate(context, random, pos);
    }

}
