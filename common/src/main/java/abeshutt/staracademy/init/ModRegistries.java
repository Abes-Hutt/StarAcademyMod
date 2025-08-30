package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import com.mojang.serialization.MapCodec;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.function.Supplier;

public class ModRegistries {

    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.ITEM);
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.BLOCK);
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.ENTITY_TYPE);
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.BLOCK_ENTITY_TYPE);
    public static DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.SCREEN_HANDLER);
    public static DeferredRegister<LootFunctionType<?>> LOOT_FUNCTION_TYPES = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.LOOT_FUNCTION_TYPE);
    public static DeferredRegister<ComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.DATA_COMPONENT_TYPE);
    public static DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(StarAcademyMod.ID, RegistryKeys.CHUNK_GENERATOR);

    public static void register() {
        ModDataComponents.register();
        ModItems.register();
        ModBlocks.register();
        ModBlocks.Entities.register();
        ModEntities.register();
        ModScreenHandlers.register();
        ModNetwork.register();
        ModLootFunctionTypes.register();
        ModChunkGenerators.register();
        ModOutfits.register();

        DATA_COMPONENTS.register();
        BLOCKS.register();
        ITEMS.register();
        ENTITIES.register();
        BLOCK_ENTITY_TYPES.register();
        SCREEN_HANDLERS.register();
        LOOT_FUNCTION_TYPES.register();
        CHUNK_GENERATORS.register();

        if(Platform.getEnvironment() == Env.CLIENT) {
            ModRenderers.ItemModels.register();
            ModRenderers.Entities.register();
            ModScreens.register();
            ModKeyBindings.register();
        }

        ModWorldData.register();
        ModConfigs.register(true);
    }

    public static <T, V extends T> RegistrySupplier<V> register(DeferredRegister<T> registry, Identifier id, Supplier<V> value) {
        return registry.register(id, value);
    }

    public static <T, V extends T> RegistrySupplier<V> register(DeferredRegister<T> registry, String name, Supplier<V> value) {
        return registry.register(name, value);
    }

}
