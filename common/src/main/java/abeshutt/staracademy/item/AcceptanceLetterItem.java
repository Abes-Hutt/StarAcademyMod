package abeshutt.staracademy.item;

import abeshutt.staracademy.screen.AcceptanceLetterScreen;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.util.ColorBlender;
import abeshutt.staracademy.world.data.PlayerProfileData;
import com.mojang.authlib.GameProfile;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static abeshutt.staracademy.init.ModDataComponents.*;

public class AcceptanceLetterItem extends Item {

    public AcceptanceLetterItem() {
        super(new Settings().fireproof().maxCount(1)
                .component(ACCEPTANCE_LETTER_OPEN.get(), false)
                .component(ACCEPTANCE_LETTER_ENROLLED.get(), false));
    }

    @Override
    public Text getName(ItemStack stack) {
        double time = 0.0D;

        if(Platform.getEnv() == EnvType.CLIENT) {
            time = ClientScheduler.getTick(MinecraftClient.getInstance().getRenderTickCounter()
                    .getTickDelta(true));
        }

        return styleText(super.getName(stack).getString(), time, 10);
    }

    private static Text styleText(String string, double time, float offset) {
        MutableText text = Text.empty();
        int count = 0;

        for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            text = text.append(Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(getColor(time + count * offset))));
            if(c != ' ') count++;
        }

        return text;
    }

    public static Integer getColor(double time) {
        ColorBlender blender = new ColorBlender(1.5F)
                .add(0xf48396, 250.0F)
                .add(0x86c5fb, 250.0F);
        return blender.getColor(time);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if(!world.isClient()) {
            if(!user.getUuid().equals(stack.getOrDefault(ACCEPTANCE_LETTER_OWNER.get(), null))) {
                user.sendMessage(Text.translatable("text.academy.acceptance_letter.incorrect_recipient").formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            } else {
                stack.set(ACCEPTANCE_LETTER_OPEN.get(), true);
            }
        } else {
            return this.openScreen(stack);
        }

        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        UUID uuid = stack.getOrDefault(ACCEPTANCE_LETTER_OWNER.get(), null);

        if(uuid != null) {
            String name = PlayerProfileData.CLIENT.getProfile(uuid)
                    .map(GameProfile::getName).orElse("Unknown");
            double time = 0.0D;

            if(Platform.getEnv() == EnvType.CLIENT) {
                time = ClientScheduler.getTick(MinecraftClient.getInstance().getRenderTickCounter()
                        .getTickDelta(true));
            }

            MutableText text = Text.empty();
            text.append(Text.translatable("item.academy.acceptance_letter.recipient"));
            text.append(styleText(name, time, 10.0F));
            tooltip.add(text);
        }
    }

    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> openScreen(ItemStack stack) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(player == null) {
            return TypedActionResult.fail(stack);
        }

        if(!player.getUuid().equals(stack.getOrDefault(ACCEPTANCE_LETTER_OWNER.get(), null))) {
            return TypedActionResult.fail(stack);
        }

        MinecraftClient.getInstance().setScreen(new AcceptanceLetterScreen(stack.getOrDefault(ACCEPTANCE_LETTER_ENROLLED.get(), false)));
        return TypedActionResult.consume(stack);
    }

}
