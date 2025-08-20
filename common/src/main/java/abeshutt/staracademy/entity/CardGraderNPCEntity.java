package abeshutt.staracademy.entity;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.world.data.CardGradingData;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import dev.architectury.hooks.item.ItemStackHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Function;

public class CardGraderNPCEntity extends HumanEntity {

    public CardGraderNPCEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F, 0.1F));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        this.setInvulnerable(true);
        this.setCustomNameVisible(false);

        if(this.getWorld().isClient()) {
            this.equipCard();
        }

        if(!this.getWorld().isClient() && this.getServer() != null) {
            String name = ModConfigs.NPC.getCardGraderNpcName();
            this.setCustomName(name == null ? Text.empty() : Text.literal(ModConfigs.NPC.getCardGraderNpcName()));
        }

        super.tick();
    }

    @Environment(EnvType.CLIENT)
    private void equipCard() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        this.setStackInHand(Hand.MAIN_HAND, CardGradingData.CLIENT.getStack(player.getUuid()));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity user, Hand hand) {
        RandomSource random = JavaRandom.ofNanoTime();

        if(user instanceof ServerPlayerEntity player) {
            ItemStack stack = player.getStackInHand(hand);
            CardGradingData data = ModWorldData.CARD_GRADING.getGlobal(player.getWorld());

            if(data.has(player.getUuid())) {
                if(data.isFinished(player.getUuid())) {
                    ItemStack returned = data.getStack(player.getUuid());
                    int grade = ModConfigs.CARD_SCALARS.getGrade(random);

                    if(returned.getItem() instanceof CardItem) {
                        CardItem.get(returned).ifPresent(card -> card.setGrade(grade));
                    }

                    ItemStackHooks.giveItem(player, returned);
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                            ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.sendMessage(Text.empty().append(Text.literal(REQUEST_COMPLETE.apply(random))
                            .formatted(Formatting.GRAY)));
                    data.remove(player.getUuid());
                } else {
                    player.sendMessage(Text.empty().append(Text.literal(REQUEST_IMPATIENT.apply(random))
                            .formatted(Formatting.GRAY)));
                }
            } else if(stack.getItem() instanceof CardItem && CardItem.get(stack).map(card -> card.getGrade() == 0).orElse(false)) {
                data.add(player.getUuid(), stack.copy());
                player.setStackInHand(hand, ItemStack.EMPTY);

                player.sendMessage(Text.empty().append(Text.literal(INITIAL_CARD.apply(random))
                        .formatted(Formatting.GRAY)));
            } else {
                player.sendMessage(Text.empty().append(Text.literal(INITIAL_NO_CARD.apply(random))
                        .formatted(Formatting.GRAY)));
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Identifier getSkinTexture() {
        return StarAcademyMod.id("textures/entity/card_grader_npc.png");
    }

    public static final Function<RandomSource, String> INITIAL_NO_CARD = create(
            "Ah, practicing the ancient art of pretend card holding, I see.",
            "That hand is looking mint! But I only grade cards.",
            "You know, maybe the rarest card of them all is the one we pretend to hold.",
            "No card for me? That’s alright, the real treasure is the trainer holding it.",
            "Looking to get something graded? Just hand me a card when you're ready.",
            "Got a favorite card? I’d be happy to take a look!",
            "I can’t grade empty hands, but I’ll gladly grade any card you bring."
    );

    public static final Function<RandomSource, String> INITIAL_CARD = create(
            "Ah, let me take a look at that beauty… This might take a while to appraise properly. Leave it with me for an hour.",
            "Oho! A fine specimen you’ve brought me. I’ll need some time to check its condition. Come back in about an hour!",
            "A fine choice! I’ll make sure it gets the grade it deserves. Check back in one hour."
    );

    public static final Function<RandomSource, String> REQUEST_IMPATIENT = create(
            "Patience, patience... good grading takes time!",
            "Come back later. The card is still being evaluated.",
            "Careful work cannot be rushed. Trust me, you’ll want a proper grade."
    );

    public static final Function<RandomSource, String> REQUEST_COMPLETE = create(
            "The evaluation is complete. I must say, this one surprised me...",
            "Ta-daaaa! One freshly graded card, hot out of the oven!",
            "All done! Thank you for waiting."
    );

    public static Function<RandomSource, String> create(String... lines) {
        return random -> lines[random.nextInt(lines.length)];
    }

}
