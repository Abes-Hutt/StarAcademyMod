package abeshutt.staracademy.mixin;

import abeshutt.staracademy.util.ProxyStructureTemplate;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.PalettedBlockInfoList;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplate implements ProxyStructureTemplate {

    @Unique private boolean custom;
    @Unique private Map<BlockPos, StructureBlockInfo> blockCache;
    @Unique private final Object lock = new Object();

    @Shadow private Vec3i size;
    @Shadow @Final private List<PalettedBlockInfoList> blockInfoLists;
    @Shadow @Final private List<StructureTemplate.StructureEntityInfo> entities;

    @Override
    public boolean isCustom() {
        return this.custom;
    }

    @Override
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    @Override
    public StructureBlockInfo get(BlockPos pos) {
        synchronized(this.lock) {
            if(this.blockCache == null) {
                this.blockCache = new HashMap<>();

                for(PalettedBlockInfoList list : this.blockInfoLists) {
                    for(StructureBlockInfo entry : list.getAll()) {
                        this.blockCache.put(entry.pos(), entry);
                    }
                }
            }
        }

        return this.blockCache.get(pos);
    }

    @Shadow protected abstract void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner);
    @Shadow private static void categorize(StructureBlockInfo blockInfo, List<StructureBlockInfo> fullBlocks, List<StructureBlockInfo> blocksWithNbt, List<StructureBlockInfo> otherBlocks) { }
    @Shadow private static List<StructureBlockInfo> combineSorted(List<StructureBlockInfo> fullBlocks, List<StructureBlockInfo> blocksWithNbt, List<StructureBlockInfo> otherBlocks) { return null; }

    @Inject(method = "saveFromWorld", at = @At("HEAD"), cancellable = true)
    public void saveFromWorld(World world, BlockPos start, Vec3i dimensions, boolean includeEntities, Block ignoredBlock, CallbackInfo ci) {
        if(!this.custom) {
            return;
        }

        if (dimensions.getX() >= 1 && dimensions.getY() >= 1 && dimensions.getZ() >= 1) {
            BlockPos blockPos = start.add(dimensions).add(-1, -1, -1);
            List<StructureBlockInfo> list = Lists.newArrayList();
            List<StructureBlockInfo> list2 = Lists.newArrayList();
            List<StructureBlockInfo> list3 = Lists.newArrayList();
            BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
            BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
            this.size = dimensions;
            Iterator var12 = BlockPos.iterate(blockPos2, blockPos3).iterator();

            while(true) {
                BlockPos blockPos4;
                BlockPos blockPos5;
                BlockState blockState;
                do {
                    if (!var12.hasNext()) {
                        List<StructureBlockInfo> list4 = combineSorted(list, list2, list3);
                        this.blockInfoLists.clear();
                        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
                        if (includeEntities) {
                            this.addEntitiesFromWorld(world, blockPos2, blockPos3);
                        } else {
                            this.entities.clear();
                        }

                        return;
                    }

                    blockPos4 = (BlockPos)var12.next();
                    blockPos5 = blockPos4.subtract(blockPos2);
                    blockState = world.getBlockState(blockPos4);
                } while((ignoredBlock != null && blockState.isOf(ignoredBlock)) || blockState.isAir());

                BlockEntity blockEntity = world.getBlockEntity(blockPos4);
                StructureBlockInfo structureBlockInfo;
                if (blockEntity != null) {
                    structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, blockEntity.createNbtWithId(world.getRegistryManager()));
                } else {
                    structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, null);
                }

                categorize(structureBlockInfo, list, list2, list3);
            }
        }

        ci.cancel();
    }

}
