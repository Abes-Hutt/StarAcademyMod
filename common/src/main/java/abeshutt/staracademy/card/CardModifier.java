package abeshutt.staracademy.card;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.*;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.roll.ConstantNumberRoll;
import abeshutt.staracademy.world.roll.NumberRoll;
import abeshutt.staracademy.world.roll.TrapezoidalNumberRoll;
import abeshutt.staracademy.world.roll.UniformNumberRoll;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.any;

public class CardModifier implements ISerializable<NbtCompound, JsonObject> {

    private String source;
    private Attribute<?> attribute;

    public CardModifier() {

    }

    public CardModifier(String source, Attribute<?> attribute) {
        this.source = source;
        this.attribute = attribute;
    }

    public Attribute<?> getAttribute() {
        return this.attribute;
    }

    public void attach(Attribute<?> root) {
        ModConfigs.CARD_MODIFIERS.get(this.source).ifPresent(mod -> {
            root.path(mod.getPath()).ifPresent(target -> {
                if(target instanceof NodeAttribute node) {
                    node.add(CardAlbumItem.REFERENCE, mod.getOrder(), this.attribute);
                }
            });
        });
    }

    public void setGrade(int grade) {
        this.attribute.iterate(child -> {
            if(child instanceof CardScalarAttribute<?> scalar) {
                scalar.setGrade(grade);
            }
        });
    }

    public void appendTooltip(ItemStack stack, int grade, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        MutableText text = Text.empty();

        if(this.attribute instanceof AddAttribute<?> given) {
            this.setGrade(0);
            Object rawValue = given.getValue().get(Option.absent(),
                    new AttributeContext(null)).orElse(null);
            this.setGrade(grade);
            Object gradedValue = given.getValue().get(Option.absent(),
                    new AttributeContext(null)).orElse(null);

            ModConfigs.CARD_MODIFIERS.get(this.source).ifPresent(mod -> {
                if(mod.getAttribute() instanceof AddAttribute<?> original && original.getValue() instanceof NodeAttribute<?> node) {
                    ModConfigs.ATTRIBUTE.getRoot().path(mod.getPath()).ifPresent(target -> {
                        ModConfigs.CARD_DISPLAYS.get(target).ifPresent(entry -> {
                            text.append(Text.literal("+"));

                            if(Screen.hasShiftDown()) {
                                text.append("(");
                                List<Attribute<?>> modifiers = new ArrayList<>(node.getModifiers().stream()
                                        .map(NodeAttribute.Modifier::getAttribute)
                                        .map(Attribute::copy).toList());

                                modifiers.removeIf(modifier -> {
                                    if(modifier instanceof CardScalarAttribute<?> scalar) {
                                        if(grade == 0) {
                                            return true;
                                        }

                                        scalar.setGrade(grade);
                                    }

                                    return false;
                                });

                                for(int i = 0; i < modifiers.size(); i++) {
                                    text.append(this.getPartText(modifiers.get(i), rawValue, entry, true));

                                    if(i != modifiers.size() - 1) {
                                        text.append(" ");
                                    }
                                }

                                text.append(")");
                            } else {
                                text.append(Text.literal(entry.getStyle().format(gradedValue)));
                            }

                            text.append(Text.literal(" "));
                            text.append(Text.translatable(entry.getName()));
                            text.setStyle(text.getStyle().withColor(entry.getColor()));
                        });
                    });
                }
            });
        }

        tooltip.add(text);
    }

    public Text getPartText(Attribute<?> attribute, Object value, CardDisplayEntry display, boolean advanced) {
        if(attribute instanceof NumberConstantAttribute constant) {
            Rational min = this.getMin(constant.getConfig());
            Rational max = this.getMax(constant.getConfig());

            if(!advanced || min.equals(max)) {
                if(value.equals(min)) {
                    return Text.literal(display.getStyle().format(value)).setStyle(Style.EMPTY.withColor(display.getColor()));
                } else {
                    return Text.empty()
                            .append(Text.literal(display.getStyle().format(value)).setStyle(Style.EMPTY.withColor(display.getColor())))
                            .append(Text.literal(" [" + display.getStyle().format(min) + "]")).formatted(Formatting.GRAY);
                }
            } else {
                return Text.empty()
                        .append(Text.literal(display.getStyle().format(value)).setStyle(Style.EMPTY.withColor(display.getColor())))
                        .append(Text.literal(" [" + display.getStyle().format(min) + "-" + display.getStyle().format(max) + "]")).formatted(Formatting.GRAY);
            }
        } else if(attribute instanceof AssignAttribute<?> assign) {
            return this.getPartText(assign.getValue(), value, display, advanced);
        } else if(attribute instanceof AddAttribute<?> add) {
            return Text.empty()
                    .append(Text.literal("+ ")).setStyle(Style.EMPTY.withColor(display.getColor()))
                    .append(this.getPartText(add.getValue(), value, display, advanced));
        } else if(attribute instanceof MultiplyAttribute<?> multiply) {
            return Text.empty()
                    .append(Text.literal("× ")).setStyle(Style.EMPTY.withColor(display.getColor()))
                    .append(this.getPartText(multiply.getValue(), value, display, advanced));
        } else if(attribute instanceof CardScalarAttribute<?> scalar) {
            Rational value2 = scalar.getScalar().orElse(null);
            return Text.literal("× " + (value2 == null ? "1" : display.getStyle().format(value2))).setStyle(Style.EMPTY.withColor(display.getColor()));
        }

        throw new UnsupportedOperationException();
    }

    private Rational getMin(NumberRoll roll) {
        return switch(roll) {
            case ConstantNumberRoll r -> r.value;
            case UniformNumberRoll r -> this.getMin(r.minimum);
            case TrapezoidalNumberRoll r -> this.getMin(r.minimum);
            default -> throw new IllegalStateException("Unexpected value: " + roll);
        };
    }

    private Rational getMax(NumberRoll roll) {
        return switch(roll) {
            case ConstantNumberRoll r -> r.value;
            case UniformNumberRoll r -> this.getMin(r.maximum);
            case TrapezoidalNumberRoll r -> this.getMin(r.maximum);
            default -> throw new IllegalStateException("Unexpected value: " + roll);
        };
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UTF_8.writeNbt(this.source).ifPresent(tag -> nbt.put("source", tag));
            Adapters.ATTRIBUTE.writeNbt(this.attribute, any()).ifPresent(tag -> nbt.put("attribute", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.source = Adapters.UTF_8.readNbt(nbt.get("source")).orElseThrow();
        this.attribute = Adapters.ATTRIBUTE.readNbt(nbt.get("attribute"), any()).orElseThrow();
    }

}
