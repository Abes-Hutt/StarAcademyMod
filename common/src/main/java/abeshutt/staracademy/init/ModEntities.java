package abeshutt.staracademy.init;

import abeshutt.staracademy.entity.*;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModEntities extends ModRegistries {

    public static RegistrySupplier<EntityType<StarBadgeEntity>> STAR_BADGE;
    public static RegistrySupplier<EntityType<DuelingGloveEntity>> DUELING_GLOVE;
    public static RegistrySupplier<EntityType<SlingshotEntity>> SLINGSHOT;
    public static RegistrySupplier<EntityType<PartnerNPCEntity>> PARTNER_NPC;
    public static RegistrySupplier<EntityType<SafariNPCEntity>> SAFARI_NPC;
    public static RegistrySupplier<EntityType<NurseNPCEntity>> NURSE_NPC;
    public static RegistrySupplier<EntityType<CardGraderNPCEntity>> CARD_GRADER_NPC;
    public static RegistrySupplier<EntityType<ShootingStarEntity>> SHOOTING_STAR;

    public static void register() {
        STAR_BADGE = register("star_badge", StarBadgeEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.98F, 0.7F).maxTrackingRange(128));

        DUELING_GLOVE = register("dueling_glove", DuelingGloveEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.98F, 0.7F).maxTrackingRange(128));

        SLINGSHOT = register("slingshot", SlingshotEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.98F, 0.7F).maxTrackingRange(128));

        PARTNER_NPC = register("partner_npc", PartnerNPCEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.6F, 1.8F).maxTrackingRange(128));

        SAFARI_NPC = register("safari_npc", SafariNPCEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.6F, 1.8F).maxTrackingRange(128));

        NURSE_NPC = register("nurse_npc", NurseNPCEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.6F, 1.8F).maxTrackingRange(128));

        CARD_GRADER_NPC = register("card_grader_npc", CardGraderNPCEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.6F, 1.8F).maxTrackingRange(128));

        SHOOTING_STAR = register("shooting_star", ShootingStarEntity::new, SpawnGroup.MISC,
                builder -> builder.dimensions(0.5F, 0.5F).maxTrackingRange(4096));
    }

    public static <V extends Entity> RegistrySupplier<EntityType<V>> register(Identifier id, EntityType.EntityFactory<V> factory,
                                                                              SpawnGroup group, Consumer<EntityType.Builder<V>> config) {
        return register(ENTITIES, id, () -> {
            EntityType.Builder<V> builder = EntityType.Builder.create(factory, group);
            config.accept(builder);
            return builder.build(id.getPath());
        });
    }

    public static <V extends Entity> RegistrySupplier<EntityType<V>> register(String name, EntityType.EntityFactory<V> factory,
                                                                              SpawnGroup group, Consumer<EntityType.Builder<V>> config) {
        return register(ENTITIES, name, () -> {
            EntityType.Builder<V> builder = EntityType.Builder.create(factory, group);
            config.accept(builder);
            return builder.build(name);
        });
    }

}
