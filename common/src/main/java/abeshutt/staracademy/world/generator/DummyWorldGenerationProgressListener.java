package abeshutt.staracademy.world.generator;

import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class DummyWorldGenerationProgressListener implements WorldGenerationProgressListener {

    @Override
    public void start(ChunkPos spawnPos) {

    }

    @Override
    public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
