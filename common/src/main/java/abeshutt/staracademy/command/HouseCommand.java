package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.architectury.hooks.item.ItemStackHooks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static abeshutt.staracademy.init.ModDataComponents.ACCEPTANCE_LETTER_HOUSE;
import static abeshutt.staracademy.init.ModDataComponents.ACCEPTANCE_LETTER_OWNER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HouseCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
            .then(literal("house")
                .requires(source -> source.hasPermissionLevel(4))
                .then(literal("create")
                    .then(argument("id", StringArgumentType.string())
                        .then(argument("name", StringArgumentType.string())
                            .then(argument("color", IntegerArgumentType.integer())
                                .executes(this::onCreateHouse)))))
                .then(literal("remove")
                    .then(argument("id", StringArgumentType.string())
                        .suggests(this::getHouseSuggestions)
                        .executes(this::onRemoveHouse)))
                .then(literal("get")
                    .then(argument("id", StringArgumentType.string())
                        .suggests(this::getHouseSuggestions)
                        .then(literal("add_player")
                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                .executes(this::onAddPlayer)))
                        .then(literal("remove_player")
                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                .executes(this::onRemovePlayer)))
                        .then(literal("name")
                            .then(argument("name", StringArgumentType.string())
                                .executes(this::onSetName))
                            .executes(this::onGetName))
                        .then(literal("color")
                            .then(argument("color", IntegerArgumentType.integer())
                                .executes(this::onSetColor))
                            .executes(this::onGetColor))))
                .then(literal("invite")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                                .then(argument("id", StringArgumentType.string())
                                    .suggests(this::getHouseSuggestions)
                                    .executes(this::onHouseInvite))))
                ));
    }

    private int onGetName(CommandContext<ServerCommandSource> context) {
        String id = StringArgumentType.getString(context, "id");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("House ").formatted(Formatting.GRAY))
                .append(Text.literal(id).formatted(Formatting.WHITE))
                .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.empty()
            .append(Text.literal("House ").formatted(Formatting.GRAY))
            .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
            .append(Text.literal(" has name '").formatted(Formatting.GRAY))
            .append(Text.literal(house.getName()).formatted(Formatting.WHITE))
            .append(Text.literal("'.").formatted(Formatting.GRAY)), false);

        return 0;
    }

    private int onSetName(CommandContext<ServerCommandSource> context) {
        String id = StringArgumentType.getString(context, "id");
        String name = StringArgumentType.getString(context, "name");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal("House ").formatted(Formatting.GRAY))
                    .append(Text.literal(id).formatted(Formatting.WHITE))
                    .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        String oldName = house.getName();
        house.setName(name);

        context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("House ").formatted(Formatting.GRAY))
                .append(Text.literal(oldName).setStyle(Style.EMPTY.withColor(house.getColor())))
                .append(Text.literal(" name was changed to '").formatted(Formatting.GRAY))
                .append(Text.literal(house.getName()).formatted(Formatting.WHITE))
                .append(Text.literal("'.").formatted(Formatting.GRAY)), false);

        return 0;
    }

    private int onGetColor(CommandContext<ServerCommandSource> context) {
        String id = StringArgumentType.getString(context, "id");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("House ").formatted(Formatting.GRAY))
                .append(Text.literal(id).formatted(Formatting.WHITE))
                .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.empty()
            .append(Text.literal("House ").formatted(Formatting.GRAY))
            .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
            .append(Text.literal(" has color ").formatted(Formatting.GRAY))
            .append(Text.literal("" + house.getColor()).setStyle(Style.EMPTY.withColor(house.getColor())))
            .append(Text.literal(".").formatted(Formatting.GRAY)), false);

        return 0;
    }

    private int onSetColor(CommandContext<ServerCommandSource> context) {
        String id = StringArgumentType.getString(context, "id");
        int color = IntegerArgumentType.getInteger(context, "color");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("House ").formatted(Formatting.GRAY))
                .append(Text.literal(id.toString()).formatted(Formatting.WHITE))
                .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        int oldColor = house.getColor();
        house.setColor(color);

        context.getSource().sendFeedback(() -> Text.empty()
            .append(Text.literal("House ").formatted(Formatting.GRAY))
            .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(oldColor)))
            .append(Text.literal(" color was changed to ").formatted(Formatting.GRAY))
            .append(Text.literal("" + house.getColor()).setStyle(Style.EMPTY.withColor(house.getColor())))
            .append(Text.literal(".").formatted(Formatting.GRAY)), false);

        return 0;
    }

    private int onAddPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "player");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("House ").formatted(Formatting.GRAY))
                .append(Text.literal(id).formatted(Formatting.WHITE))
                .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        for(GameProfile player : players) {
            if(house.addPlayer(player.getId())) {
                context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal("Added ").formatted(Formatting.GRAY))
                    .append(Text.literal(player.getName()).formatted(Formatting.WHITE))
                    .append(Text.literal(" to ").formatted(Formatting.GRAY))
                    .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                    .append(Text.literal(".").formatted(Formatting.GRAY)), false);
            } else {
                context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal(player.getName()).formatted(Formatting.WHITE))
                    .append(Text.literal(" is already in ").formatted(Formatting.GRAY))
                    .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                    .append(Text.literal(".").formatted(Formatting.GRAY)), false);
            }
        }

        return 0;
    }

    private int onRemovePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "player");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.get(id).orElse(null);

        if(house == null) {
            context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal("House ").formatted(Formatting.GRAY))
                    .append(Text.literal(id).formatted(Formatting.WHITE))
                    .append(Text.literal(" does not exist.").formatted(Formatting.GRAY)), false);
            return 0;
        }

        for(GameProfile player : players) {
            if(house.removePlayer(player.getId())) {
                context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal("Removed ").formatted(Formatting.GRAY))
                    .append(Text.literal(player.getName()).formatted(Formatting.WHITE))
                    .append(Text.literal(" from ").formatted(Formatting.GRAY))
                    .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                    .append(Text.literal(".").formatted(Formatting.GRAY)), false);
            } else {
                context.getSource().sendFeedback(() -> Text.empty()
                    .append(Text.literal(player.getName()).formatted(Formatting.WHITE))
                    .append(Text.literal(" is absent from ").formatted(Formatting.GRAY))
                    .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                    .append(Text.literal(".").formatted(Formatting.GRAY)), false);
            }
        }

        return 0;
    }

    private int onCreateHouse(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");
        String name = StringArgumentType.getString(context, "name");
        int color = IntegerArgumentType.getInteger(context, "color");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        data.add(id, name, color);

        context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("Created house ").formatted(Formatting.GRAY))
                .append(Text.literal(name).setStyle(Style.EMPTY.withColor(color)))
                .append(Text.literal(".").formatted(Formatting.GRAY)), false);

        return 0;
    }

    private int onRemoveHouse(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");

        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        AcademyHouse house = data.remove(id);

        if(house != null) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("Removed house ").formatted(Formatting.GRAY))
                .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                .append(Text.literal(".").formatted(Formatting.GRAY)), false);
        }

        return 0;
    }

    private int onHouseInvite(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "player");
        String id = StringArgumentType.getString(context, "id");

        for(GameProfile profile : players) {
            ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.getId());
            if(player == null) continue;
            ItemStack stack = new ItemStack(ModItems.ACCEPTANCE_LETTER.get());
            stack.set(ACCEPTANCE_LETTER_OWNER.get(), player.getUuid());
            stack.set(ACCEPTANCE_LETTER_HOUSE.get(), id);

            for(ServerPlayerEntity other : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                other.sendMessage(Text.empty().append(player.getDisplayName())
                        .append(Text.literal(" has received a letter from the Academy...").formatted(Formatting.GRAY)));
            }

            ItemStackHooks.giveItem(player, stack);
        }

        return 0;
    }

    private CompletableFuture<Suggestions> getHouseSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        HouseData data = ModWorldData.HOUSE.getGlobal(context.getSource().getServer());
        return CommandSource.suggestMatching(data.getHouses().values().stream()
                .map(AcademyHouse::getId), builder);
    }

}
