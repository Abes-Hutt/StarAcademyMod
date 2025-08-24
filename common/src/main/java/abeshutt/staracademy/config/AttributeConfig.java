package abeshutt.staracademy.config;

import abeshutt.staracademy.attribute.again.*;
import abeshutt.staracademy.world.roll.NumberRoll;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.annotations.Expose;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.any;
import static abeshutt.staracademy.attribute.again.type.AttributeTypes.number;
import static abeshutt.staracademy.attribute.path.AttributePath.relative;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.*;

public class AttributeConfig extends FileConfig {

    @Expose protected Attribute<?> root;

    @Override
    public String getPath() {
        return "attribute";
    }

    public Attribute<?> getRoot() {
        return this.root;
    }

    @Override
    protected void reset() {
        this.root = NodeAttribute.of(any());
        Attribute<?> vanilla = NodeAttribute.of(any());

        for(RegistryEntry<EntityAttribute> entry : Registries.ATTRIBUTE.getIndexedEntries()) {
            Attribute<?> child = NodeAttribute.of(any());

            child.addChild(ADD_VALUE.asString(), NodeAttribute.of(number())
                    .add(null, 0, AssignAttribute.of(NumberRoll.constant(0))));

            child.addChild(ADD_MULTIPLIED_BASE.asString(), NodeAttribute.of(number())
                    .add(null, 0, AssignAttribute.of(NumberRoll.constant(0))));

            child.addChild(ADD_MULTIPLIED_TOTAL.asString(), NodeAttribute.of(number())
                    .add(null, 0, AssignAttribute.of(NumberRoll.constant(0))));

            vanilla.addChild(entry.getIdAsString(), child);
        }

        this.root.addChild("vanilla", vanilla);

        this.root.addChild("shiny_chance", NodeAttribute.of(number())
                .add(null, 0, new MultiplyAttribute<>(number(),
                        ReferenceAttribute.of(number(), relative("..", "increased"))))
                .addChild("increased", NodeAttribute.of(number())
                        .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));

        this.root.addChild("capture_friendship", NodeAttribute.of(number())
                .add(null, 0, new MultiplyAttribute<>(number(),
                        ReferenceAttribute.of(number(), relative("..", "increased"))))
                .addChild("increased", NodeAttribute.of(number())
                        .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));

        this.root.addChild("capture_experience", NodeAttribute.of(number())
                .add(null, 0, new MultiplyAttribute<>(number(),
                        ReferenceAttribute.of(number(), relative("..", "increased"))))
                .addChild("increased", NodeAttribute.of(number())
                        .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));

        this.root.addChild("capture_chance", NodeAttribute.of(number())
                .add(null, 0, new MultiplyAttribute<>(number(),
                        ReferenceAttribute.of(number(), relative("..", "increased"))))
                .addChild("increased", NodeAttribute.of(number())
                        .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));

        Attribute<?> bucketWeight = NodeAttribute.of(any());

        for(String bucket : List.of("common", "uncommon", "rare", "ultra-rare")) {
            bucketWeight.addChild(bucket, NodeAttribute.of(number())
                    .add(null, 0, new MultiplyAttribute<>(number(),
                            ReferenceAttribute.of(number(), relative("..", "increased"))))
                    .addChild("increased", NodeAttribute.of(number())
                            .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));
        }

        this.root.addChild("bucket_weight", bucketWeight);
        Attribute<?> labelWeight = NodeAttribute.of(any());

        for(ElementalType type : ElementalTypes.INSTANCE.all()) {
            labelWeight.addChild(type.getName().toLowerCase(), NodeAttribute.of(number())
                    .add(null, 0, new MultiplyAttribute<>(number(),
                            ReferenceAttribute.of(number(), relative("..", "increased"))))
                    .addChild("increased", NodeAttribute.of(number())
                            .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));
        }

        this.root.addChild("label_weight", labelWeight);

        Attribute<?> evYield = NodeAttribute.of(any());

        for(Stats stat : Stats.values()) {
            evYield.addChild(stat.getIdentifier().toString(), NodeAttribute.of(number())
                    .add(null, 0, new MultiplyAttribute<>(number(),
                            ReferenceAttribute.of(number(), relative("..", "increased"))))
                    .addChild("increased", NodeAttribute.of(number())
                            .add(null, 0, AssignAttribute.of(NumberRoll.constant(1)))));
        }

        this.root.addChild("ev_yield", evYield);
    }

}
