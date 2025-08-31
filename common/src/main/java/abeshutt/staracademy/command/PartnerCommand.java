package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.entity.PartnerNPCEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PartnerCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
                .requires(source -> source.hasPermissionLevel(4))
                .then(literal("partner")
                    .then(argument("npc", EntityArgumentType.entity())
                        .then(literal("select_partner")
                            .executes(this::onSelectPartner))
                        .then(literal("confirm_partner_selection")
                            .executes(this::onConfirmPartnerSelection)))));
    }

    private int onSelectPartner(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity entity = EntityArgumentType.getEntity(context, "npc");
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        if(!(entity instanceof PartnerNPCEntity npc)) return 0;

        npc.getConversation(player.getUuid()).ifPresent(conversation -> {
            conversation.onSelectPartner(player, npc);
        });

        return 0;
    }

    private int onConfirmPartnerSelection(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity entity = EntityArgumentType.getEntity(context, "npc");
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        if(!(entity instanceof PartnerNPCEntity npc)) return 0;

        npc.getConversation(player.getUuid()).ifPresent(conversation -> {
            conversation.onConfirmPartnerSelection(player, npc);
        });

        return 0;
    }

}
