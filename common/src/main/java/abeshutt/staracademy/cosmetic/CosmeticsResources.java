package abeshutt.staracademy.cosmetic;

import com.cobblemon.mod.common.client.render.ModelTextureSupplier;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationGroup;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public class CosmeticsResources {

    private final Map<Identifier, ModelPart> bakedModels;
    private final Map<Identifier, PosableModel> posableModels;
    private final Map<Identifier, BedrockAnimationGroup> animations;
    private final Map<Identifier, ModelTextureSupplier> textureAnimations;

    public CosmeticsResources(Map<Identifier, ModelPart> bakedModels, Map<Identifier, PosableModel> posableModels,
                              Map<Identifier, BedrockAnimationGroup> animations, Map<Identifier,ModelTextureSupplier> textureAnimations) {
        this.bakedModels = bakedModels;
        this.posableModels = posableModels;
        this.animations = animations;
        this.textureAnimations = textureAnimations;
    }

    public Map<Identifier, ModelPart> getBakedModels() {
        return this.bakedModels;
    }

    public Map<Identifier, PosableModel> getPosableModels() {
        return this.posableModels;
    }

    public Map<Identifier, BedrockAnimationGroup> getAnimations() {
        return this.animations;
    }

    public Map<Identifier, ModelTextureSupplier> getTextureAnimations() {
        return this.textureAnimations;
    }

    public Optional<ModelPart> getBakedModel(Identifier id) {
        return Optional.ofNullable(this.bakedModels.get(id));
    }

    public Optional<PosableModel> getPosableModel(Identifier id) {
        return Optional.ofNullable(this.posableModels.get(id));
    }

    public Optional<BedrockAnimationGroup> getAnimation(Identifier id) {
        return Optional.ofNullable(this.animations.get(id));
    }

    public Optional<ModelTextureSupplier> getTextureAnimation(Identifier id) {
        return Optional.ofNullable(this.textureAnimations.get(id));
    }

}
