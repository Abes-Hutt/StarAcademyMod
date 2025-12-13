package abeshutt.staracademy;

import dev.architectury.platform.Platform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AcademyMixinPlugin implements IMixinConfigPlugin {

    private static final Set<String> MYTHS_AND_LEGENDS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.mythsandlegends.MixinSingleEntitySpawnAction"
    );

    private static final Set<String> FADING_CLOUDS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.fadingclouds.MixinGlassBottleItem"
    );

    private static final Set<String> OUTBREAKS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.outbreaks.MixinPlayerEntity"
    );

    private static final Set<String> MOONRISE_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.moonrise.MixinEntityMixin"
    );

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (MYTHS_AND_LEGENDS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("mythsandlegends");
        } else if (FADING_CLOUDS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("fading_clouds");
        } else if (OUTBREAKS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("cobblemonoutbreaks");
        } else if (MOONRISE_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("moonrise");
        }

        if (!Platform.isModLoaded("moonrise")) {
            if ("abeshutt.staracademy.mixin.moonrise.MixinChunkHolder".equals(mixinClassName)) {
                return false;
            } else if("abeshutt.staracademy.mixin.moonrise.MixinExplosion".equals(mixinClassName)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
