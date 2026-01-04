package abeshutt.staracademy.fabric.resource;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FabricResourceReloadListener implements IdentifiableResourceReloadListener {

    private final Identifier id;
    private final ResourceReloader delegate;

    public FabricResourceReloadListener(Identifier id, ResourceReloader delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public Identifier getFabricId() {
        return this.id;
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return this.delegate.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
    }

}
