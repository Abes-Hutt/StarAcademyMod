package abeshutt.staracademy.entity;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardGradingData;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.math.random.JavaRandom;
import abeshutt.staracademy.math.random.RandomSource;
import abeshutt.staracademy.net.UpdateCardGradingS2CPacket;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.networking.NetworkManager;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CardGraderNPCEntity extends HumanEntity {

    private Set<UUID> locked = ConcurrentHashMap.newKeySet();

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

        if(this.getWorld().isClient()) {
            this.equipCard();
            this.updateName();
        }

        super.tick();
    }

    @Environment(EnvType.CLIENT)
    private void equipCard() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        if (CardGradingData.CLIENT == null) {
            this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
        else {
            this.setStackInHand(Hand.MAIN_HAND, CardGradingData.CLIENT.getStack());
        }
    }

    @Environment(EnvType.CLIENT)
    private void updateName() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(player == null) {
            this.setCustomNameVisible(false);
            return;
        }

        if(CardGradingData.CLIENT == null || CardGradingData.CLIENT.getStack().isEmpty()) {
            this.setCustomNameVisible(false);
            return;
        }

        long millis = Math.max(Duration.between(Instant.now(), CardGradingData.CLIENT.getCompletionTime()).toMillis(), 0);
        long hours = millis / 1000 / 3600;
        long minutes = millis / 1000 % 3600 / 60;
        long seconds = millis / 1000 % 60;
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        this.setCustomNameVisible(true);
        this.setCustomName(Text.literal(time));
    }

    private void onRemoveGradedCard(CardGradingData data, ServerPlayerEntity player) {
        RandomSource random = JavaRandom.ofNanoTime();

        if (Instant.now().isAfter(data.getCompletionTime())) {
            var returned = data.getStack();
            int grade = ModConfigs.CARD_SCALARS.getGrade(random);

            if(returned.getItem() instanceof CardItem) {
                CardItem.get(returned).ifPresent(card -> card.setGrade(grade));
            }

            ItemStackHooks.giveItem(player, returned);
            player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                    ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.sendMessage(Text.empty().append(Text.translatable(REQUEST_COMPLETE.apply(random))
                    .formatted(Formatting.GRAY)));
            StarAcademyMod.cardGradingService.removeCardData(player).thenAccept(x -> {
                locked.remove(player.getUuid());
            });
            NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(null));
        }
        else {
            player.sendMessage(Text.empty().append(Text.translatable(REQUEST_IMPATIENT.apply(random))
                    .formatted(Formatting.GRAY)));
            locked.remove(player.getUuid());
        }
    }

    private void onStartCardGrading(ServerPlayerEntity player, Hand hand) {
        RandomSource random = JavaRandom.ofNanoTime();
        ItemStack stack = player.getStackInHand(hand);

        if(stack.getItem() instanceof CardItem && CardItem.get(stack).map(card -> card.getGrade() == 0).orElse(false)) {
            var cardGradingData = new CardGradingData(Instant.now().plus(ModConfigs.NPC.getGradingTimeMillis(), ChronoUnit.MILLIS), stack);
            StarAcademyMod.cardGradingService.insertCardData(player, hand, cardGradingData).thenAccept(data -> {
                NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(data));
                locked.remove(player.getUuid());
            });
        }
        else {
            player.sendMessage(Text.empty().append(Text.translatable(INITIAL_NO_CARD.apply(random))
                    .formatted(Formatting.GRAY)));
            locked.remove(player.getUuid());
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity user, Hand hand) {
        // TODO: Remove this check, make flatfile fallback.
        if (StarAcademyMod.cardGradingService == null) return ActionResult.SUCCESS;
        if (locked.contains(user.getUuid())) {
            StarAcademyMod.LOGGER.warn("Player {} tried to interact with card grader while already locked.", user.getName());
            return ActionResult.SUCCESS;
        }

        if(user instanceof ServerPlayerEntity player) {
            locked.add(player.getUuid());
            StarAcademyMod.cardGradingService.getCardData(player).thenAccept(data -> {
                var cardData = data.orElse(null);
                if (cardData != null) {
                    player.server.execute(() -> onRemoveGradedCard(cardData, player));
                }
                else {
                    player.server.execute(() -> onStartCardGrading(player, hand));
                }
            }).exceptionally(x -> {
                StarAcademyMod.LOGGER.error("Failed to get card data", x);
                locked.remove(player.getUuid());
                return null;
            });
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Identifier getSkinTexture() {
        return StarAcademyMod.id("textures/entity/card_grader_npc.png");
    }

    public static final Function<RandomSource, String> INITIAL_NO_CARD = create(
            "text.academy.grader.initial_no_card_1",
            "text.academy.grader.initial_no_card_2",
            "text.academy.grader.initial_no_card_3",
            "text.academy.grader.initial_no_card_4",
            "text.academy.grader.initial_no_card_5",
            "text.academy.grader.initial_no_card_6",
            "text.academy.grader.initial_no_card_7"
    );

    public static final Function<RandomSource, String> INITIAL_CARD = create(
            "text.academy.grader.initial_card_1",
            "text.academy.grader.initial_card_2",
            "text.academy.grader.initial_card_3"
    );

    public static final Function<RandomSource, String> INITIAL_BROKE = create(
            "text.academy.grader.initial_broke_1",
            "text.academy.grader.initial_broke_2",
            "text.academy.grader.initial_broke_3"
    );

    public static final Function<RandomSource, String> REQUEST_IMPATIENT = create(
            "text.academy.grader.request_impatient_1",
            "text.academy.grader.request_impatient_2",
            "text.academy.grader.request_impatient_3"
    );

    public static final Function<RandomSource, String> REQUEST_COMPLETE = create(
            "text.academy.grader.request_complete_1",
            "text.academy.grader.request_complete_2",
            "text.academy.grader.request_complete_3"
    );

    public static Function<RandomSource, String> create(String... lines) {
        return random -> lines[random.nextInt(lines.length)];
    }

}
