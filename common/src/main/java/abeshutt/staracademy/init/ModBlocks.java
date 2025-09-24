package abeshutt.staracademy.init;

import abeshutt.staracademy.block.*;
import abeshutt.staracademy.block.entity.*;
import com.mojang.datafixers.types.Type;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks extends ModRegistries {

    public static RegistrySupplier<Block> ERROR;
    public static RegistrySupplier<BetterStructureBlock> STRUCTURE_BLOCK;
    public static RegistrySupplier<SafariPortalBlock> SAFARI_PORTAL;
    public static RegistrySupplier<Block> SAFARI_PORTAL_FRAME;
    public static RegistrySupplier<HousePokedexBlock> HOUSE_POKEDEX;
    public static RegistrySupplier<ShinyPokedollCollectorBlock> SHINY_POKEDOLL_COLLECTOR;
    public static RegistrySupplier<MeteoricChunkBlock> METEORIC_CHUNK;
    public static RegistrySupplier<MeteoricChunkBlock> RARE_METEORIC_CHUNK;
    public static RegistrySupplier<MeteoricChunkBlock> FABULOUS_METEORIC_CHUNK;

    public static void register() {
        ERROR = register("error", () -> new Block(Block.Settings.copy(Blocks.SLIME_BLOCK)),
                block -> new BlockItem(block.get(), new Item.Settings()));

        STRUCTURE_BLOCK = register("structure_block", BetterStructureBlock::new,
                block -> new BlockItem(block.get(), new Item.Settings()));

        SAFARI_PORTAL = register("safari_portal", SafariPortalBlock::new,
                block -> new BlockItem(block.get(), new Item.Settings()));

        SAFARI_PORTAL_FRAME = register("safari_portal_frame", () -> new Block(AbstractBlock.Settings
                        .create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F)),
                block -> new BlockItem(block.get(), new Item.Settings()));

        HOUSE_POKEDEX = register("house_pokedex", HousePokedexBlock::new,
                block -> new BlockItem(block.get(), new Item.Settings()));

        SHINY_POKEDOLL_COLLECTOR = register("shiny_pokedoll_collector", ShinyPokedollCollectorBlock::new,
                block -> new BlockItem(block.get(), new Item.Settings()));

        METEORIC_CHUNK = register("meteoric_chunk", () -> new MeteoricChunkBlock(Block.Settings.copy(Blocks.AMETHYST_BLOCK)),
                block -> new BlockItem(block.get(), new Item.Settings()));

        RARE_METEORIC_CHUNK = register("rare_meteoric_chunk", () -> new MeteoricChunkBlock(Block.Settings.copy(Blocks.AMETHYST_BLOCK)),
                block -> new BlockItem(block.get(), new Item.Settings()));

        FABULOUS_METEORIC_CHUNK = register("fabulous_meteoric_chunk", () -> new MeteoricChunkBlock(Block.Settings.copy(Blocks.AMETHYST_BLOCK)),
                block -> new BlockItem(block.get(), new Item.Settings()));
    }

    public static class Entities extends ModBlocks {
        public static RegistrySupplier<BlockEntityType<BetterStructureBlockEntity>> STRUCTURE_BLOCK;
        public static RegistrySupplier<BlockEntityType<SafariPortalBlockEntity>> SAFARI_PORTAL;
        public static RegistrySupplier<BlockEntityType<HousePokedexBlockEntity>> HOUSE_POKEDEX;
        public static RegistrySupplier<BlockEntityType<ShinyPokedollCollectorBlockEntity>> SHINY_POKEDOLL_COLLECTOR;
        public static RegistrySupplier<BlockEntityType<MeteoricChunkBlockEntity>> METEORIC_CHUNK;

        public static void register() {
            STRUCTURE_BLOCK = register("structure_block", BetterStructureBlockEntity::new, ModBlocks.STRUCTURE_BLOCK);
            SAFARI_PORTAL = register("safari_portal", SafariPortalBlockEntity::new, ModBlocks.SAFARI_PORTAL);
            HOUSE_POKEDEX = register("house_pokedex", HousePokedexBlockEntity::new, ModBlocks.HOUSE_POKEDEX);
            SHINY_POKEDOLL_COLLECTOR = register("shiny_pokedoll_collector", ShinyPokedollCollectorBlockEntity::new, ModBlocks.SHINY_POKEDOLL_COLLECTOR);
            METEORIC_CHUNK = register("meteoric_chunk", MeteoricChunkBlockEntity::new, ModBlocks.METEORIC_CHUNK, ModBlocks.RARE_METEORIC_CHUNK, ModBlocks.FABULOUS_METEORIC_CHUNK);
        }
    }

    public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block) {
        return register(id, block);
    }

    public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block) {
        return register(name, block);
    }

    public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block, Function<RegistrySupplier<V>, BlockItem> item) {
        return register(id, block, id, item);
    }

    public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block, Function<RegistrySupplier<V>, BlockItem> item) {
        return register(name, block, name, item);
    }

    public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block, Identifier itemId, Function<RegistrySupplier<V>, BlockItem> item) {
        RegistrySupplier<V> entry = register(BLOCKS, id, block);

        if(item != null) {
            Supplier<BlockItem> value = () -> item.apply(entry);
            ModItems.register(itemId, value);
        }

        return entry;
    }

    public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block, String itemName, Function<RegistrySupplier<V>, BlockItem> item) {
        RegistrySupplier<V> entry = register(BLOCKS, name, block);

        if(item != null) {
            Supplier<BlockItem> value = () -> item.apply(entry);
            ModItems.register(itemName, value);
        }

        return entry;
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityFactory<T> blockEntity, RegistrySupplier<Block>... blocks) {
        return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, null), blocks);
    }

    public static <T extends BlockEntity>RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityFactory<T>blockEntity, String typeId, RegistrySupplier<Block>... blocks) {
        return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityFactory<T> blockEntity, Type<?> type, RegistrySupplier<Block>... blocks) {
        return register(BLOCK_ENTITY_TYPES, id, () -> {
            Block[] values = new Block[blocks.length];
            for(int i = 0; i < blocks.length; i++) values[i] = blocks[i].get();
            return BlockEntityType.Builder.create(blockEntity, values).build(type);
        });
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityFactory<T> blockEntity, RegistrySupplier<? extends Block>... blocks) {
        return register(name, blockEntity, (Type<?>)null, blocks);
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityFactory<T> blockEntity, String typeId, RegistrySupplier<? extends Block>... blocks) {
        return register(name, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityFactory<T> blockEntity, Type<?> type, RegistrySupplier<? extends Block>... blocks) {
        return register(BLOCK_ENTITY_TYPES, name, () -> {
            Block[] values = new Block[blocks.length];
            for(int i = 0; i < blocks.length; i++) values[i] = blocks[i].get();
            return BlockEntityType.Builder.create(blockEntity, values).build(type);
        });
    }

}
