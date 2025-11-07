package abeshutt.staracademy.mixin.moonrise;

import ca.spottedleaf.moonrise.common.list.ReferenceList;
import ca.spottedleaf.moonrise.common.util.WorldUtil;
import ca.spottedleaf.moonrise.patches.chunk_system.level.ChunkSystemServerLevel;
import ca.spottedleaf.moonrise.patches.chunk_system.level.chunk.ChunkSystemChunkHolder;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.ChunkHolderManager;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.NewChunkHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkHolder.class})
abstract class MixinChunkHolder extends AbstractChunkHolder implements ChunkSystemChunkHolder {

    @Shadow @Final private ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
    @Shadow private volatile CompletableFuture<OptionalChunk<WorldChunk>> accessibleFuture;
    @Shadow private volatile CompletableFuture<OptionalChunk<WorldChunk>> tickingFuture;
    @Shadow private volatile CompletableFuture<OptionalChunk<WorldChunk>> entityTickingFuture;
    @Shadow private CompletableFuture<?> levelIncreaseFuture;
    @Shadow private CompletableFuture<?> postProcessingFuture;
    @Shadow private CompletableFuture<?> savingFuture;
    @Unique private NewChunkHolder newChunkHolder;
    @Unique private final ReferenceList<ServerPlayerEntity> playersSentChunkTo;
    @Unique private boolean isMarkedDirtyForPlayers;
    @Unique private static final ServerPlayerEntity[] EMPTY_PLAYER_ARRAY = new ServerPlayerEntity[0];

    public MixinChunkHolder(ChunkPos chunkPos) {
        super(chunkPos);
        this.playersSentChunkTo = new ReferenceList(EMPTY_PLAYER_ARRAY);
    }

    @Unique
    private ServerChunkLoadingManager getChunkMap() {
        return (ServerChunkLoadingManager)this.playersWatchingChunkProvider;
    }

    public final NewChunkHolder moonrise$getRealChunkHolder() {
        return this.newChunkHolder;
    }

    public final void moonrise$setRealChunkHolder(NewChunkHolder newChunkHolder) {
        this.newChunkHolder = newChunkHolder;
    }

    public final void moonrise$addReceivedChunk(ServerPlayerEntity player) {
        if (!this.playersSentChunkTo.add(player)) {
            String var10002 = String.valueOf(this.pos);
            throw new IllegalStateException("Already sent chunk " + var10002 + " in world '" + WorldUtil.getWorldName(this.getChunkMap().world) + "' to player " + String.valueOf(player));
        }
    }

    public final void moonrise$removeReceivedChunk(ServerPlayerEntity player) {
        if (!this.playersSentChunkTo.remove(player)) {
            String var10002 = String.valueOf(this.pos);
            throw new IllegalStateException("Already sent chunk " + var10002 + " in world '" + WorldUtil.getWorldName(this.getChunkMap().world) + "' to player " + String.valueOf(player));
        }
    }

    public final boolean moonrise$hasChunkBeenSent() {
        return this.playersSentChunkTo.size() != 0;
    }

    public final boolean moonrise$hasChunkBeenSent(ServerPlayerEntity to) {
        return this.playersSentChunkTo.contains(to);
    }

    public final List<ServerPlayerEntity> moonrise$getPlayers(boolean onlyOnWatchDistanceEdge) {
        List<ServerPlayerEntity> ret = new ArrayList();
        ServerPlayerEntity[] raw = (ServerPlayerEntity[])this.playersSentChunkTo.getRawDataUnchecked();
        int i = 0;

        for(int len = this.playersSentChunkTo.size(); i < len; ++i) {
            ServerPlayerEntity player = raw[i];
            if (!onlyOnWatchDistanceEdge || ((ChunkSystemServerLevel)this.getChunkMap().world).moonrise$getPlayerChunkLoader().isChunkSent(player, this.pos.x, this.pos.z, onlyOnWatchDistanceEdge)) {
                ret.add(player);
            }
        }

        return ret;
    }

    public final boolean moonrise$isMarkedDirtyForPlayers() {
        return this.isMarkedDirtyForPlayers;
    }

    public final void moonrise$markDirtyForPlayers(boolean value) {
        this.isMarkedDirtyForPlayers = value;
    }

    @Inject(
            method = {"<init>(Lnet/minecraft/util/math/ChunkPos;ILnet/minecraft/world/HeightLimitView;Lnet/minecraft/world/chunk/light/LightingProvider;Lnet/minecraft/server/world/ChunkHolder$LevelUpdateListener;Lnet/minecraft/server/world/ChunkHolder$PlayersWatchingChunkProvider;)V"},
            at = {@At("RETURN")}
    )
    private void initFields(CallbackInfo ci) {
        this.accessibleFuture = null;
        this.tickingFuture = null;
        this.entityTickingFuture = null;
        this.levelIncreaseFuture = null;
        this.postProcessingFuture = null;
        this.savingFuture = null;
    }

    @Overwrite
    public CompletableFuture<OptionalChunk<Chunk>> getTickingFuture() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public CompletableFuture<OptionalChunk<Chunk>> getEntityTickingFuture() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public CompletableFuture<OptionalChunk<Chunk>> getAccessibleFuture() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public WorldChunk getWorldChunk() {
        if (this.newChunkHolder.isTickingReady()) {
            Chunk var2 = this.newChunkHolder.getCurrentChunk();
            if (var2 instanceof WorldChunk) {
                WorldChunk levelChunk = (WorldChunk)var2;
                return levelChunk;
            }
        }

        return null;
    }

    @Overwrite
    public CompletableFuture<?> getPostProcessingFuture() {
        throw new UnsupportedOperationException();
    }

    @Unique
    private boolean isRadiusLoaded(int radius) {
        ChunkHolderManager manager = ((ChunkSystemServerLevel)this.getChunkMap().world).moonrise$getChunkTaskScheduler().chunkHolderManager;
        ChunkPos pos = this.pos;
        int chunkX = pos.x;
        int chunkZ = pos.z;

        for(int dz = -radius; dz <= radius; ++dz) {
            for(int dx = -radius; dx <= radius; ++dx) {
                if ((dx | dz) != 0) {
                    NewChunkHolder holder = manager.getChunkHolder(dx + chunkX, dz + chunkZ);
                    if (holder == null || !holder.isFullChunkReady()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Overwrite
    public WorldChunk getPostProcessedChunk() {
        WorldChunk ret = this.moonrise$getFullChunk();
        return ret != null && this.isRadiusLoaded(1) ? ret : null;
    }

    public final WorldChunk moonrise$getFullChunk() {
        if (this.newChunkHolder.isFullChunkReady()) {
            Chunk var2 = this.newChunkHolder.getCurrentChunk();
            if (var2 instanceof WorldChunk) {
                WorldChunk levelChunk = (WorldChunk)var2;
                return levelChunk;
            }
        }

        return null;
    }

    @Overwrite
    public CompletableFuture<Chunk> getSavingFuture() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public boolean isSavable() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public void combineSavingFuture(CompletableFuture<?> completableFuture) {
        throw new UnsupportedOperationException();
    }

    @Redirect(
            method = {"markForBlockUpdate(Lnet/minecraft/util/math/BlockPos;)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ChunkHolder;getWorldChunk()Lnet/minecraft/world/chunk/WorldChunk;"
            )
    )
    private WorldChunk redirectBlockUpdate(ChunkHolder instance) {
        if (this.playersSentChunkTo.size() == 0) {
            return null;
        } else {
            WorldChunk ret = this.getPostProcessedChunk();
            if (ret != null) {
                ((ChunkSystemServerLevel)this.getChunkMap().world).moonrise$addUnsyncedChunk((ChunkHolder)(Object)this);
                return ret;
            } else {
                return ret;
            }
        }
    }

    @Redirect(
            method = {"markForLightUpdate(Lnet/minecraft/world/LightType;I)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ChunkHolder;getWorldChunk()Lnet/minecraft/world/chunk/WorldChunk;"
            )
    )
    private WorldChunk redirectLightUpdate(ChunkHolder instance) {
        if (this.playersSentChunkTo.size() == 0) {
            return null;
        } else {
            WorldChunk ret = this.getPostProcessedChunk();
            if (ret != null) {
                ((ChunkSystemServerLevel)this.getChunkMap().world).moonrise$addUnsyncedChunk((ChunkHolder)(Object)this);
                return ret;
            } else {
                return ret;
            }
        }
    }

    @Redirect(
            method = {"flushUpdates(Lnet/minecraft/world/chunk/WorldChunk;)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ChunkHolder$PlayersWatchingChunkProvider;getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;"
            )
    )
    private List<ServerPlayerEntity> redirectPlayerRetrieval(ChunkHolder.PlayersWatchingChunkProvider instance, ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
        return this.moonrise$getPlayers(onlyOnWatchDistanceEdge);
    }

    @Overwrite
    public void combinePostProcessingFuture(CompletableFuture<?> completableFuture) {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public int getLevel() {
        return this.newChunkHolder.getTicketLevel();
    }

    @Overwrite
    public int getCompletedLevel() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public void setCompletedLevel(int i) {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public void setLevel(int i) {
    }

    @Overwrite
    public void increaseLevel(ServerChunkLoadingManager chunkMap, CompletableFuture<OptionalChunk<WorldChunk>> completableFuture, Executor executor, ChunkLevelType fullChunkStatus) {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public void decreaseLevel(ServerChunkLoadingManager chunkMap, ChunkLevelType fullChunkStatus) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "updateFutures", at = @At("HEAD"), cancellable = true)
    public void updateFutures(ServerChunkLoadingManager chunkLoadingManager, Executor executor, CallbackInfo ci) {
        ci.cancel();
    }

    @Overwrite
    public boolean isAccessible() {
        throw new UnsupportedOperationException();
    }

    @Overwrite
    public void updateAccessibleStatus() {
        throw new UnsupportedOperationException();
    }

}
