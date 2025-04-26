package abeshutt.staracademy.world;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.entity.PartialEntity;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.data.serializable.INbtSerializable;
import abeshutt.staracademy.data.tile.PartialBlockState;
import abeshutt.staracademy.data.tile.PartialTile;
import abeshutt.staracademy.init.ModBlocks;
import com.google.common.collect.Lists;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EmptyBlockView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class StructureTemplate implements INbtSerializable<NbtCompound> {

    public static final Comparator<PartialTile> SORTER = Comparator.
            <PartialTile>comparingInt(tile -> tile.getPos().getY())
            .thenComparingInt(tile -> tile.getPos().getX())
            .thenComparingInt(tile -> tile.getPos().getZ());

    private List<PartialTile> tiles = new ArrayList<>();
    private List<PartialEntity> entities = new ArrayList<>();

    private IdPalette palette;
    private Vec3i size = Vec3i.ZERO;

    public static StructureTemplate fromPath(String path) {
        NbtCompound nbt;

        try {
            nbt = NbtIo.readCompressed(new FileInputStream(path), NbtSizeTracker.ofUnlimitedBytes());
        } catch(IOException e) {
            return null;
        }

        StructureTemplate template = new StructureTemplate();
        template.readNbt(nbt);
        return template;
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        NbtCompound nbt = new NbtCompound();

        if(this.tiles.isEmpty()) {
            nbt.put("blocks", new NbtList());
            nbt.put("palette", new NbtList());
        } else {
            NbtList blocksNBT = new NbtList();

            for(PartialTile tile : this.tiles) {
                int paletteId = this.palette.getIdFor(tile.getState());
                blocksNBT.add(toPaletteNBT(tile, new NbtCompound(), paletteId));
            }

            nbt.put("blocks", blocksNBT);

            NbtList paletteNBT = new NbtList();

            for(PartialBlockState state : this.palette) {
                Adapters.PARTIAL_BLOCK_STATE.writeNbt(state).ifPresent(paletteNBT::add);
            }

            nbt.put("palette", paletteNBT);
        }

        NbtList entitiesNBT = new NbtList();

        for(PartialEntity entity : this.entities) {
            NbtCompound entityNBT = new NbtCompound();

            NbtList posNBT = new NbtList();
            posNBT.add(NbtDouble.of(entity.getPos().x));
            posNBT.add(NbtDouble.of(entity.getPos().y));
            posNBT.add(NbtDouble.of(entity.getPos().z));
            entityNBT.put("pos", posNBT);

            NbtList blockPosNBT = new NbtList();
            blockPosNBT.add(NbtInt.of(entity.getBlockPos().getX()));
            blockPosNBT.add(NbtInt.of(entity.getBlockPos().getY()));
            blockPosNBT.add(NbtInt.of(entity.getBlockPos().getZ()));
            entityNBT.put("blockPos", blockPosNBT);

            entity.getNbt().asWhole().ifPresent(tag -> {
                entityNBT.put("nbt", tag);
            });

            entitiesNBT.add(entityNBT);
        }

        nbt.put("entities", entitiesNBT);

        NbtList sizeNBT = new NbtList();
        sizeNBT.add(NbtInt.of(this.size.getX()));
        sizeNBT.add(NbtInt.of(this.size.getY()));
        sizeNBT.add(NbtInt.of(this.size.getZ()));
        nbt.put("size", sizeNBT);
        nbt.putInt("DataVersion", SharedConstants.getGameVersion().getSaveVersion().getId());
        return Optional.of(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.tiles.clear();
        this.entities.clear();

        NbtList sizeNBT = nbt.getList("size", 3);
        this.size = new Vec3i(sizeNBT.getInt(0), sizeNBT.getInt(1), sizeNBT.getInt(2));

        NbtList blocksNBT = nbt.getList("blocks", 10);

        if(nbt.contains("palettes", 9)) {
            this.loadPalette(nbt.getList("palettes", 9).getList(0), blocksNBT);
            StarAcademyMod.LOGGER.error("Template does not support multiple palettes, using the first one instead");
        } else {
            this.loadPalette(nbt.getList("palette", 10), blocksNBT);
        }

        NbtList entitiesNBT = nbt.getList("entities", 10);

        for(int j = 0; j < entitiesNBT.size(); ++j) {
            NbtCompound entityNBT = entitiesNBT.getCompound(j);

            NbtList posNBT = entityNBT.getList("pos", 6);
            Vec3d pos = new Vec3d(posNBT.getDouble(0), posNBT.getDouble(1), posNBT.getDouble(2));

            NbtList blockPosNBT = entityNBT.getList("blockPos", 3);
            BlockPos blockPos = new BlockPos(blockPosNBT.getInt(0), blockPosNBT.getInt(1), blockPosNBT.getInt(2));

            if(entityNBT.contains("nbt")) {
                PartialEntity entity = PartialEntity.of(pos, blockPos, PartialCompoundNbt.of(entityNBT.getCompound("nbt")));
                this.entities.add(entity);
            }
        }
    }

    private void loadPalette(NbtList paletteNBT, NbtList blocksNBT) {
        this.palette = new IdPalette();

        for(int i = 0; i < paletteNBT.size(); ++i) {
            PartialBlockState state = Adapters.PARTIAL_BLOCK_STATE.readNbt(paletteNBT.getCompound(i)).orElse(null);
            if(state == null) continue;
            this.palette.addMapping(state, i);
        }

        List<PartialTile> normal = Lists.newArrayList();
        List<PartialTile> withNBT = Lists.newArrayList();
        List<PartialTile> withSpecialShape = Lists.newArrayList();

        for(int j = 0; j < blocksNBT.size(); ++j) {
            NbtCompound blockNBT = blocksNBT.getCompound(j);
            PartialTile tile = fromPaletteNBT(blockNBT, this.palette::getStateFor);
            BlockState state = tile.getState().asWhole().orElse(ModBlocks.ERROR.get().getDefaultState());

            if(tile.getEntity().asWhole().isPresent()) {
                withNBT.add(tile);
            } else if(!state.getBlock().hasDynamicBounds() && state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
                normal.add(tile);
            } else {
                withSpecialShape.add(tile);
            }
        }

        this.tiles.addAll(getOrderedTiles(normal, withNBT, withSpecialShape));
    }

    private static List<PartialTile> getOrderedTiles(List<PartialTile> normal, List<PartialTile> withNBT, List<PartialTile> withSpecialShape) {
        normal.sort(SORTER);
        withSpecialShape.sort(SORTER);
        withNBT.sort(SORTER);

        List<PartialTile> tiles = new ArrayList<>();
        tiles.addAll(normal);
        tiles.addAll(withSpecialShape);
        tiles.addAll(withNBT);
        return tiles;
    }

    public NbtElement toPaletteNBT(PartialTile tile, NbtCompound nbt, int index) {
        nbt.putInt("state", index);

        tile.getEntity().asWhole().ifPresent(tag -> {
            nbt.put("nbt", tag.copy());
        });

        if(tile.getPos() != null) {
            NbtList posNBT = new NbtList();
            posNBT.add(NbtInt.of(tile.getPos().getX()));
            posNBT.add(NbtInt.of(tile.getPos().getY()));
            posNBT.add(NbtInt.of(tile.getPos().getZ()));
            nbt.put("pos", posNBT);
        }

        return nbt;
    }

    public static PartialTile fromPaletteNBT(NbtCompound tag, Function<Integer, PartialBlockState> stateFunction) {
        PartialBlockState state = PartialBlockState.of(ModBlocks.ERROR.get().getDefaultState());
        PartialCompoundNbt nbt = PartialCompoundNbt.empty();
        BlockPos pos = null;

        if(tag.contains("state", NbtElement.INT_TYPE)) {
            state = stateFunction.apply(tag.getInt("state"));
        }

        if(tag.contains("pos", NbtElement.LIST_TYPE)) {
            NbtList posNBT = tag.getList("pos", NbtElement.INT_TYPE);
            pos = new BlockPos(posNBT.getInt(0), posNBT.getInt(1), posNBT.getInt(2));
        }

        if(tag.contains("nbt", NbtElement.COMPOUND_TYPE)) {
            nbt = PartialCompoundNbt.of(tag.getCompound("nbt"));
        }

        return PartialTile.of(state, nbt, pos);
    }

    public static class IdPalette implements Iterable<PartialBlockState> {
        public static final PartialBlockState DEFAULT_STATE = PartialBlockState.of(ModBlocks.ERROR.get().getDefaultState());

        private final IdMapper<PartialBlockState> ids = new IdMapper<>(16);
        private int nextId;

        public int getIdFor(PartialBlockState pState) {
            int i = this.ids.getId(pState);

            if(i == -1) {
                i = this.nextId++;
                this.ids.addMapping(pState, i);
            }

            return i;
        }

        public PartialBlockState getStateFor(int id) {
            PartialBlockState state = this.ids.byId(id);
            return state == null ? DEFAULT_STATE : state;
        }

        public void addMapping(PartialBlockState state, int id) {
            this.ids.addMapping(state, id);
        }

        @Override
        public Iterator<PartialBlockState> iterator() {
            return this.ids.iterator();
        }
    }

}

