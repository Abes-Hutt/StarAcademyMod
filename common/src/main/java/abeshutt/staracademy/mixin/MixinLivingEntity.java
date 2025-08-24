package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.attribute.Attributes;
import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.entity.IDefaultedAttributes;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow @Final private AttributeContainer attributes;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract void remove(RemovalReason reason);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/DefaultAttributeRegistry;get(Lnet/minecraft/entity/EntityType;)Lnet/minecraft/entity/attribute/DefaultAttributeContainer;"))
    public DefaultAttributeContainer getAttributes(EntityType<? extends LivingEntity> type) {
        if(this instanceof IDefaultedAttributes entity) {
            return entity.getDefaultAttributes();
        }

        return DefaultAttributeRegistry.get(type);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if(this.getWorld().isClient()) {
            return;
        }

        for(RegistryEntry<EntityAttribute> entry : Registries.ATTRIBUTE.getIndexedEntries()) {
            if(!this.attributes.hasAttribute(entry)) continue;
            EntityAttributeInstance instance = this.attributes.getCustomInstance(entry);
            if(instance == null) continue;

            for(Operation operation : Operation.values()) {
                Identifier id = StarAcademyMod.id("attribute/" + operation.asString());
                AttributePath<Rational> path = Attributes.ofVanilla(entry, operation);

                Attribute<Rational> attribute = AttributeHolder.getRoot(this).path(path).orElse(null);
                if(attribute == null) continue;
                Double value = attribute.get(Option.absent(), AttributeContext.random())
                        .map(Rational::doubleValue).orElse(null);

                if(value == null) {
                    instance.removeModifier(id);
                    continue;
                }

                EntityAttributeModifier modifier = instance.getModifier(id);

                if(modifier != null && modifier.value() == value) {
                    continue;
                }

                instance.removeModifier(id);
                instance.addTemporaryModifier(new EntityAttributeModifier(id, value, operation));
            }
        }
    }

}
