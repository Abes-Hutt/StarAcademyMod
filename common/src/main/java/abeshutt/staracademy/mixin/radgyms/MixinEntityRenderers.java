package abeshutt.staracademy.mixin.radgyms;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.entity.HumanData;
import abeshutt.staracademy.entity.renderer.HumanEntityRenderer;
import lol.gito.radgyms.common.entity.EntityManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderers.class)
public abstract class MixinEntityRenderers {

    @Shadow
    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
    }

    @Inject(method = "reloadEntityRenderers", at = @At("HEAD"))
    private static void reloadEntityRenderers(EntityRendererFactory.Context ctx,
                                              CallbackInfoReturnable<Map<EntityType<?>, EntityRenderer<?>>> ci) {
        register(EntityManager.INSTANCE.getGYM_TRAINER(), factory -> new HumanEntityRenderer<>(factory, false, e -> new HumanData() {
            @Override
            public Identifier getSkinTexture() {
                return StarAcademyMod.id("textures/entity/partner_npc.png");
            }

            @Override
            public Vec3d lerpVelocity(float tickDelta) {
                return Vec3d.ZERO;
            }
        }));
    }

}
