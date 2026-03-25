package abeshutt.staracademy.card;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CardGradingService {
    CompletableFuture<Optional<CardGradingData>> getCardData(ServerPlayerEntity player);
    CompletableFuture<CardGradingData> insertCardData(ServerPlayerEntity player, Hand hand, CardGradingData data);
    CompletableFuture<Boolean> removeCardData(ServerPlayerEntity player);
}
