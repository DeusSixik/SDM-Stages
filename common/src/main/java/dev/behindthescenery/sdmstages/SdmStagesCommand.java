package dev.behindthescenery.sdmstages;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.Collection;

public class SdmStagesCommand {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        registerCommands(dispatcher);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sdm_stages")
                        .requires(source -> source.hasPermission(2))

                        .then(Commands.literal("add")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("stage", StringArgumentType.string())
                                                .executes(SdmStagesCommand::addStage))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("stage", StringArgumentType.string())
                                                .executes(SdmStagesCommand::removeStage))))

                        .then(Commands.literal("get_all")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(SdmStagesCommand::getAllStages)))
        );
    }

    private static int addStage(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
        final String stage = StringArgumentType.getString(ctx, "stage");
        final PlayerList players = ctx.getSource().getServer().getPlayerList();

        for (GameProfile profile : profiles) {

            final ServerPlayer player = players.getPlayer(profile.getId());
            if (player != null && StageApi.modifyStageAndSync(player, s -> s.addStage(stage))) {
                ctx.getSource().sendSuccess(() ->
                        Component.literal("Added stage '" + stage + "' to " + player.getName().getString()), true);
            } else {
                ctx.getSource().sendFailure(Component.literal("Player not found: " + profile.getName()));
            }
        }
        return 1;
    }

    private static int removeStage(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
        final String stage = StringArgumentType.getString(ctx, "stage");
        final PlayerList players = ctx.getSource().getServer().getPlayerList();

        for (var profile : profiles) {
            final ServerPlayer player = players.getPlayer(profile.getId());
            if (player != null && StageApi.modifyStageAndSync(player, s -> s.remove(stage))) {
                ctx.getSource().sendSuccess(() ->
                        Component.literal("Removed stage '" + stage + "' from " + player.getName().getString()), true);
            } else {
                ctx.getSource().sendFailure(Component.literal("Player not found: " + profile.getName()));
            }
        }
        return 1;
    }

    private static int getAllStages(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
        final PlayerList players = ctx.getSource().getServer().getPlayerList();

        for (var profile : profiles) {
            final ServerPlayer player = players.getPlayer(profile.getId());
            if (player != null) {
                ctx.getSource().sendSuccess(() ->
                        Component.literal("Stages for " + player.getName().getString() + ": " + StageApi.getServerStage().getStage(player).getStages().toString()), false);
            } else {
                ctx.getSource().sendFailure(Component.literal("Player not found: " + profile.getName()));
            }
        }
        return 1;
    }
}
