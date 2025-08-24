package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.attribute.again.NodeAttribute;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.component.CardAlbumInventory;
import abeshutt.staracademy.event.CommonEvents;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.AttributeHolder;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;

public class Attributes {

    public static final AttributePath<Rational> SHINY_CHANCE = AttributePath.absolute("shiny_chance");
    public static final AttributePath<Rational> CAPTURE_FRIENDSHIP = AttributePath.absolute("capture_friendship");
    public static final AttributePath<Rational> CAPTURE_EXPERIENCE = AttributePath.absolute("capture_experience");
    public static final AttributePath<Rational> CAPTURE_CHANCE = AttributePath.absolute("capture_chance");

    public static AttributePath<Rational> ofBucketWeight(String name) {
        return AttributePath.absolute("bucket_weight", name);
    }

    public static AttributePath<Rational> ofLabelWeight(String name) {
        return AttributePath.absolute("label_weight", name);
    }

    public static AttributePath<Rational> ofEVYield(Stat stat) {
        return AttributePath.absolute("ev_yield", stat.getIdentifier().toString());
    }

    public static AttributePath<Rational> ofVanilla(RegistryEntry<EntityAttribute> attribute, Operation operation) {
        return AttributePath.absolute("vanilla", attribute.getIdAsString(), operation.asString());
    }

    public static void init() {
        CommonEvents.POKEMON_CAPTURED.register(event -> {
            int current = event.getPokemon().getFriendship();

            int friendship = AttributeHolder.getRoot( event.getPlayer()).path(CAPTURE_FRIENDSHIP).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(Rational.of(current)), AttributeContext.random());

                if(result.isPresent()) {
                    RandomSource random = JavaRandom.ofNanoTime();
                    double raw = result.get().doubleValue();
                    int floored = (int)raw;
                    return floored + (random.nextDouble() >= raw - floored ? 1 : 0);
                }

                return current;
            }).orElse(current);

            if(current != friendship) {
                event.getPokemon().setFriendship(friendship, true);
            }
        }, Priority.LOWEST);

        CommonEvents.POKEMON_EXPERIENCE_GAINED_PRE.register(event -> {
            LivingEntity entity = event.getPokemon().getOwnerEntity();
            if(entity == null) return;
            int current = event.getExperience();

            int experience = AttributeHolder.getRoot( entity).path(CAPTURE_EXPERIENCE).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(Rational.of(current)), AttributeContext.random());

                if(result.isPresent()) {
                    RandomSource random = JavaRandom.ofNanoTime();
                    double raw = result.get().doubleValue();
                    int floored = (int)raw;
                    return floored + (random.nextDouble() >= raw - floored ? 1 : 0);
                }

                return current;
            }).orElse(current);

            if(current != experience) {
                event.setExperience(experience);
            }
        }, Priority.LOWEST);

        CommonEvents.POKEMON_CATCH_RATE.register(event -> {
            LivingEntity entity = event.getThrower();
            float current = event.getCatchRate();

            float rate = AttributeHolder.getRoot( entity).path(CAPTURE_CHANCE).map(attribute -> {
                Option<Rational> result = attribute.get(Option.present(Rational.of(current)), AttributeContext.random());
                return result.isPresent() ? result.get().floatValue() : current;
            }).orElse(current);

            if(current != rate) {
                event.setCatchRate(rate);
            }
        }, Priority.LOWEST);

        CommonEvents.PLAYER_TICK.register(player -> {
            if(player.getWorld().isClient()) return;
            Attribute<?> root = AttributeHolder.getRoot(player);

            root.iterate(child -> {
                if(child instanceof NodeAttribute<?> node) {
                    node.remove(CardAlbumItem.REFERENCE);
                }
            });

            TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                Map<String, TrinketInventory> legs = trinkets.getInventory().get("legs");
                if(legs == null) return;
                TrinketInventory album = legs.get("card_album");
                if(album == null || album.isEmpty()) return;
                ItemStack stack = album.getStack(0);
                if(!(stack.getItem() instanceof CardAlbumItem)) return;
                CardAlbumInventory container = stack.get(ModDataComponents.CARD_ALBUM_CONTAINER.get());
                if(container == null) return;

                for(int i = 0; i < container.size(); i++) {
                    ItemStack card = container.getStack(i);
                    if(card == null || !(card.getItem() instanceof CardItem)) continue;
                    CardItem.get(card).ifPresent(data -> data.attach(root));
                }
            });
        });
    }

}
