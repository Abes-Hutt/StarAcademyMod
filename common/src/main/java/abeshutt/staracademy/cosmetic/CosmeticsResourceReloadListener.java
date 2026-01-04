package abeshutt.staracademy.cosmetic;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.client.render.ModelTextureSupplier;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimation;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationAdapter;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationGroup;
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CosmeticsResourceReloadListener implements ResourceReloader {

    private static final Gson MODEL_GSON = TexturedModel.Companion.getGSON();

    private static final Gson ANIMATION_GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(BedrockAnimation.class, BedrockAnimationAdapter.INSTANCE)
            .create();

    private static final Gson TEXTURE_ANIMATION_GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(BedrockAnimation.class, BedrockAnimationAdapter.INSTANCE)
            .create();

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler,
                                          Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> prepare(manager, prepareProfiler), prepareExecutor)
                .thenCompose(synchronizer::whenPrepared)
                .thenCompose(resources ->
                        CompletableFuture.runAsync(() -> apply(resources, manager, applyProfiler), applyExecutor)
                );
    }

    public CosmeticsResources prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, ModelPart> bakedModels = new HashMap<>();
        Map<Identifier, PosableModel> posableModels = new  HashMap<>();
        Map<Identifier, BedrockAnimationGroup> animations = new HashMap<>();
        Map<Identifier, ModelTextureSupplier> textureAnimations = new HashMap<>();

        manager.findResources("cosmetics/models", path -> path.getPath().endsWith(".geo.json"))
                .forEach((path, resource) -> {
                    try {
                        TexturedModel rawModel = MODEL_GSON.fromJson(resource.getReader(), TexturedModel.class);
                        Object bakedModel = rawModel.create().createModel(); // Cobblemon has an extension on ModelPart
                        PosableModel posableModel = new PosableModel((Bone)bakedModel);
                        posableModel.registerPartAndAllNamedChildren("root", (Bone)bakedModel);
                        Identifier id = Identifier.of(path.getNamespace(),
                                path.getPath().replace(".geo.json", ""));
                        bakedModels.put(id, (ModelPart)bakedModel);
                        posableModels.put(id, posableModel);
                    } catch (Exception e) {
                        StarAcademyMod.LOGGER.error("Failed to load model " + path, e);
                    }
                });

        manager.findResources("cosmetics/animations", path -> path.getPath().endsWith(".animation.json"))
                .forEach((path, resource) -> {
                    try {
                        BedrockAnimationGroup animationGroup = ANIMATION_GSON
                                .fromJson(resource.getReader(), BedrockAnimationGroup.class);

                        animationGroup.getAnimations().forEach((name, animation) -> {
                            animation.setName(name);

                            try {
                                animation.checkForErrors();
                            } catch (Throwable e) {
                                StarAcademyMod.LOGGER.error("Failed to load animation %s in %s".formatted(name, path), e);
                            }
                        });

                        Identifier id = Identifier.of(path.getNamespace(),
                                path.getPath().replace(".animation.json", ""));
                        animations.put(id, animationGroup);
                    } catch (Exception e) {
                        StarAcademyMod.LOGGER.error("Failed to load animation " + path, e);
                    }
                });

        manager.findResources("cosmetics/texture_animations", path -> path.getPath().endsWith(".json"))
                .forEach((path, resource) -> {
                    try {
                        ModelTextureSupplier textureAnimation = TEXTURE_ANIMATION_GSON
                                .fromJson(resource.getReader(), ModelTextureSupplier.class);
                        Identifier id = Identifier.of(path.getNamespace(),
                                path.getPath().replace(".json", ""));
                        textureAnimations.put(id, textureAnimation);
                    } catch (Exception e) {
                        StarAcademyMod.LOGGER.error("Failed to load texture animation " + path, e);
                    }
                });

        return new CosmeticsResources(bakedModels, posableModels, animations, textureAnimations);
    }

    public void apply(CosmeticsResources resources, ResourceManager manager, Profiler profiler) {
        StarAcademyMod.RESOURCES = resources;
    }

}
