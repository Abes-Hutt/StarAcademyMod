package abeshutt.staracademy.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.Predicate;

public class FastBlockCheck {

    public static final DistanceCheck EUCLIDEAN_DISTANCE = Vec3i::isWithinDistance;
    public static final DistanceCheck MANHATTAN_DISTANCE = (origin, currentPos, radius) -> origin.getManhattanDistance(currentPos) <= radius;


    public static boolean findBlockFastEuclideanDistance(int radius, boolean forceChunkLoads, World world, BlockPos origin, Predicate<BlockState> stateTest) {
        return countBlocksFast(radius, Integer.MAX_VALUE, forceChunkLoads, world, origin, stateTest, EUCLIDEAN_DISTANCE) > 0;
    }

    public static boolean findBlockFastManhattanDistance(int radius, boolean forceChunkLoads, World world, BlockPos origin, Predicate<BlockState> stateTest) {
        return countBlocksFast(radius, Integer.MAX_VALUE, forceChunkLoads, world, origin, stateTest, MANHATTAN_DISTANCE) > 0;
    }

    public static int countBlocksFast(int radius, int maxCount, boolean forceChunkLoads, World world, BlockPos origin, Predicate<BlockState> stateTest, DistanceCheck distanceCheck) {
        int hits = 0;

        int minChunkX = ChunkSectionPos.getSectionCoord(origin.getX() - radius);
        int minChunkZ = ChunkSectionPos.getSectionCoord(origin.getZ() - radius);

        int maxChunkX = ChunkSectionPos.getSectionCoord(origin.getX() + radius);
        int maxChunkZ = ChunkSectionPos.getSectionCoord(origin.getZ() + radius);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (forceChunkLoads) {
                    if (!world.isChunkLoaded(chunkX, chunkZ)) {
                        continue;
                    }
                }
                WorldChunk chunk = world.getChunk(chunkX, chunkZ);

                int minSectionIDX = chunk.getSectionIndex(origin.getY() - radius);
                int maxSectionIDX = chunk.getSectionIndex(origin.getY() + radius);

                for (int sectionIDX = minSectionIDX; sectionIDX <= maxSectionIDX; sectionIDX++) {

                    ChunkSection section = chunk.getSection(sectionIDX);
                    if (section == null || section.isEmpty()) {
                        continue;
                    }
                    //if (!section.hasAny(stateTest)) {
                    //    continue;
                    //}

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                mutable.set(
                                        ChunkSectionPos.getOffsetPos(chunkX, x),
                                        ChunkSectionPos.getOffsetPos(chunk.sectionIndexToCoord(sectionIDX), x),
                                        ChunkSectionPos.getOffsetPos(chunkZ, z)
                                );

                                if (!distanceCheck.test(origin, mutable, radius)) {
                                    continue;
                                }

                                BlockState state = section.getBlockState(x, y, z);
                                if (stateTest.test(state)) {
                                    hits++;
                                    if (hits >= maxCount) {
                                        return hits; // Early exit if max count is reached
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return hits;
    }


    @FunctionalInterface
    public interface DistanceCheck {
        boolean test(BlockPos origin, BlockPos currentPos, int radius);
    }
}
