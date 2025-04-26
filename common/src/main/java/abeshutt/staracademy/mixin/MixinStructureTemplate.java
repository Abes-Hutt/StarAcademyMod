package abeshutt.staracademy.mixin;

import abeshutt.staracademy.util.ProxyStructureTemplate;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplate implements ProxyStructureTemplate {

    @Unique private boolean custom;
    @Unique private Map<BlockPos, StructureBlockInfo> blockCache;

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
        if(this.blockCache == null) {
            this.blockCache = new HashMap<>();

            for(PalettedBlockInfoList list : this.blockInfoLists) {
                for(StructureBlockInfo entry : list.getAll()) {
                   this.blockCache.put(entry.pos(), entry);
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

        if(dimensions.getX() >= 1 && dimensions.getY() >= 1 && dimensions.getZ() >= 1) {
            BlockPos blockPos = start.add(dimensions).add(-1, -1, -1);
            List<StructureBlockInfo> list = new ArrayList<>();
            List<StructureBlockInfo> list2 = new ArrayList<>();
            List<StructureBlockInfo> list3 = new ArrayList<>();
            BlockPos min = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
            BlockPos max = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
            this.size = dimensions;

            int current = 0;
            int total = this.size.getX() * this.size.getY() * this.size.getZ();

            for(BlockPos offset : BlockPos.iterate(min, max)) {
                BlockPos blockPos5 = offset.subtract(min);
                BlockState blockState = world.getBlockState(offset);

                if(!blockState.isAir()) {
                    BlockEntity blockEntity = world.getBlockEntity(offset);
                    StructureBlockInfo structureBlockInfo;

                    if(blockEntity != null) {
                        //structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, blockEntity.createNbtWithId());
                        structureBlockInfo = null;
                    } else {
                        structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, null);
                    }

                    categorize(structureBlockInfo, list, list2, list3);
                }

                current++;

                if(total / 8 > 0 && current % (total / 8) == 0) {
                    System.out.println("Saving progress: " + (float)((double)current / total * 100.0D) + "%");
                }
            }

            List<StructureBlockInfo> list4 = combineSorted(list, list2, list3);
            this.blockInfoLists.clear();
            this.blockInfoLists.add(new PalettedBlockInfoList(list4));

            if(includeEntities) {
                this.addEntitiesFromWorld(world, min, max.add(1, 1, 1));
            } else {
                this.entities.clear();
            }
        }

        ci.cancel();
    }

}
