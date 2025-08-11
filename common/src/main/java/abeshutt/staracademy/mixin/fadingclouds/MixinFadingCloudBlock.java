package abeshutt.staracademy.mixin.fadingclouds;

import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "me.lemo.fading_clouds.block.FadingCloudBlock" })
public abstract class MixinFadingCloudBlock extends TransparentBlock {

    public MixinFadingCloudBlock(Settings settings) {
        super(settings);
    }

    @Redirect(method = "fading_clouds$computeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;setBaseValue(D)V"), remap = false)
    private void computeFallDamage(EntityAttributeInstance instance, double baseValue) {

    }

    @Redirect(method = "onEntityLand", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;setBaseValue(D)V"))
    private void onEntityLand(EntityAttributeInstance instance, double baseValue) {

    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {

    }

}
