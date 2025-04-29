package abeshutt.staracademy.init;

import abeshutt.staracademy.block.entity.BetterStructureBlockEntity;
import abeshutt.staracademy.block.entity.renderer.BetterStructureBlockEntityRenderer;
import abeshutt.staracademy.entity.renderer.HumanEntityRenderer;
import abeshutt.staracademy.entity.renderer.ShootingStarRenderer;
import abeshutt.staracademy.mixin.ProxyModelPredicateProviderRegistry;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModRenderers extends ModRegistries {

    public static class ItemModels extends ModRenderers {
        public static void register() {
            ProxyModelPredicateProviderRegistry.register(ModItems.DUELING_GLOVE.get(), Identifier.of("pull"), (stack, world, entity, seed) -> {
                if(entity == null) {
                    return 0.0F;
                }

                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
            });

            ProxyModelPredicateProviderRegistry.register(ModItems.DUELING_GLOVE.get(), Identifier.of("pulling"), (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
            });

            ProxyModelPredicateProviderRegistry.register(ModItems.SLINGSHOT.get(), Identifier.of("pull"), (stack, world, entity, seed) -> {
                if(entity == null) {
                    return 0.0F;
                }

                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
            });

            ProxyModelPredicateProviderRegistry.register(ModItems.SLINGSHOT.get(), Identifier.of("pulling"), (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
            });
        }
    }

    public static class Entities extends ModRenderers {
        public static void register() {
            ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
                EntityRenderers.register(ModEntities.STAR_BADGE.get(), FlyingItemEntityRenderer::new);
                EntityRenderers.register(ModEntities.DUELING_GLOVE.get(), FlyingItemEntityRenderer::new);
                EntityRenderers.register(ModEntities.SLINGSHOT.get(), FlyingItemEntityRenderer::new);
                EntityRenderers.register(ModEntities.PARTNER_NPC.get(), ctx -> new HumanEntityRenderer<>(ctx, false));
                EntityRenderers.register(ModEntities.SAFARI_NPC.get(), ctx -> new HumanEntityRenderer<>(ctx, false));
                EntityRenderers.register(ModEntities.NURSE_NPC.get(), ctx -> new HumanEntityRenderer<>(ctx, false));
                EntityRenderers.register(ModEntities.SHOOTING_STAR.get(), ShootingStarRenderer::new);
            });
        }
    }

    public static class BlockEntities extends ModRenderers {
        public static BlockEntityRendererFactory<BetterStructureBlockEntity> STRUCTURE_BLOCK;

        public static void register(Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry) {
            try {
                STRUCTURE_BLOCK = register(registry, ModBlocks.Entities.STRUCTURE_BLOCK.get(), BetterStructureBlockEntityRenderer::new);
            } catch(Exception e) {
                ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
                    STRUCTURE_BLOCK = register(registry, ModBlocks.Entities.STRUCTURE_BLOCK.get(), BetterStructureBlockEntityRenderer::new);
                });
            }
        }
    }

    public static <T extends BlockEntity> BlockEntityRendererFactory<T> register(
            Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry,
            BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> renderer) {
        registry.put(type, renderer);
        return renderer;
    }

}
