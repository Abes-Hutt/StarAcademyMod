package abeshutt.staracademy;

import dev.architectury.platform.Platform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AcademyMixinPlugin implements IMixinConfigPlugin {

    private static final Set<String> RAD_GYMS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.radgyms.MixinDataSaver",
            "abeshutt.staracademy.mixin.radgyms.MixinEntityRenderers",
            "abeshutt.staracademy.mixin.radgyms.MixinPlayerPartyStore"
    );

    private static final Set<String> MYTHS_AND_LEGENDS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.mythsandlegends.MixinSingleEntitySpawnAction"
    );

    private static final Set<String> FADING_CLOUDS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.fadingclouds.MixinGlassBottleItem"
    );

    private static final Set<String> OUTBREAKS_MIXINS = Set.of(
            "abeshutt.staracademy.mixin.outbreaks.MixinPlayerEntity"
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
        if (RAD_GYMS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("rad-gyms");
        } else if (MYTHS_AND_LEGENDS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("mythsandlegends");
        } else if (FADING_CLOUDS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("fading_clouds");
        } else if (OUTBREAKS_MIXINS.contains(mixinClassName)) {
            return Platform.isModLoaded("cobblemonoutbreaks");
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
