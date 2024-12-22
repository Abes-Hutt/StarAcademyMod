package abeshutt.staracademy.item;

import abeshutt.staracademy.entity.StarBadgeEntity;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.util.ColorBlender;
import abeshutt.staracademy.world.StarOwnership;
import abeshutt.staracademy.world.data.PlayerProfileData;
import com.mojang.authlib.GameProfile;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StarBadgeItem extends Item {

    public StarBadgeItem() {
        super(new Settings().fireproof().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if(!world.isClient) {
            StarBadgeEntity entity = new StarBadgeEntity(world, user, stack.copy());
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.0F, 0.0F);
            world.spawnEntity(entity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.setCount(0);
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public Text getName(ItemStack stack) {
        String name = super.getName(stack).getString();
        double time = 0.0F;

        if(Platform.getEnv() != EnvType.SERVER) {
            time = ClientScheduler.getTick(MinecraftClient.getInstance().getTickDelta());
        }

        return this.styleText(name, time, 10.0F);
    }

    private Text styleText(String string, double time, float offset) {
        MutableText text = Text.empty();
        int count = 0;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            text = text.append(Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(this.getColor(time + count * offset).orElseThrow())));
            if(c != ' ') count++;
        }

        return text;
    }

    public Optional<Integer> getColor(double time) {
        ColorBlender blender = new ColorBlender(1.0F)
                .add(0xE3A335, 60.0F)
                .add(0xB76D32, 60.0F);
        return Optional.of(blender.getColor(time));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        List<StarOwnership> ownership = StarBadgeItem.getOwnership(stack);

        if(ownership.isEmpty()) {
            return;
        }

        String original = PlayerProfileData.CLIENT.getProfile(ownership.get(0).getUuid())
                .map(GameProfile::getName).orElse("Unknown");

        tooltip.add(Text.empty()
                .append(Text.literal("Originally bestowed to ").formatted(Formatting.GRAY, Formatting.ITALIC))
                .append(Text.literal(original).formatted(Formatting.GRAY, Formatting.ITALIC))
                .append(Text.literal(".").formatted(Formatting.GRAY, Formatting.ITALIC)));

        if(ownership.size() < 2) {
            return;
        }

        String surrendered = PlayerProfileData.CLIENT.getProfile(ownership.get(ownership.size() - 2).getUuid())
                .map(GameProfile::getName).orElse("Unknown");

        tooltip.add(Text.empty()
                .append(Text.literal("Last surrendered by ").formatted(Formatting.GRAY, Formatting.ITALIC))
                .append(Text.literal(surrendered).formatted(Formatting.GRAY, Formatting.ITALIC))
                .append(Text.literal(".").formatted(Formatting.GRAY, Formatting.ITALIC)));
    }

    public static List<StarOwnership> getOwnership(ItemStack stack) {
        List<StarOwnership> ownership = new ArrayList<>();

        if(stack.getNbt() != null) {
            NbtList list = stack.getNbt().getList("ownership", NbtElement.COMPOUND_TYPE);

            for(int i = 0; i < list.size(); i++) {
                ownership.add(StarOwnership.parseNbt(list.getCompound(i)));
            }
        }

        return ownership;
    }

    public static void setOwnership(ItemStack stack, List<StarOwnership> ownership) {
        NbtList list = new NbtList();

        for(StarOwnership entry : ownership) {
           entry.writeNbt().ifPresent(list::add);
        }

        stack.getOrCreateNbt().put("ownership", list);
    }

}
