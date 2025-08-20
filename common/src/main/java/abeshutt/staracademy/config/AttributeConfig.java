package abeshutt.staracademy.config;

import abeshutt.staracademy.attribute.again.*;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.world.roll.NumberRoll;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.any;
import static abeshutt.staracademy.attribute.again.type.AttributeTypes.number;
import static abeshutt.staracademy.attribute.path.AttributePath.relative;

public class AttributeConfig extends FileConfig {

    @Expose protected Map<Identifier, String> associations;
    @Expose protected Attribute<?> root;

    @Override
    public String getPath() {
        return "attribute";
    }

    public Optional<AttributePath> getAssociation(Identifier attribute) {
        String raw = this.associations.get(attribute);
        return raw == null ? Optional.empty() : Adapters.ATTRIBUTE_PATH.readJson(new JsonPrimitive(raw));
    }

    public Attribute<?> getRoot() {
        return this.root;
    }

    @Override
    protected void reset() {
        this.root = NodeAttribute.of(any());
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
