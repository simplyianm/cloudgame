package pw.ian.cloudgame.commands.game;

import pw.ian.albkit.command.PlayerCommandHandler;
import pw.ian.albkit.command.parser.Arguments;
import pw.ian.cloudgame.CloudGame;
import pw.ian.cloudgame.game.Game;
import pw.ian.cloudgame.gameplay.Gameplay;
import pw.ian.cloudgame.hosted.Host;
import pw.ian.cloudgame.model.arena.Arena;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pw.ian.cloudgame.hosted.HostedGameCountdown;

public class GameHostCommand extends PlayerCommandHandler {

    private final CloudGame cloudGame;

    public GameHostCommand() {
        super("host");
        this.cloudGame = CloudGame.inst();

        setDescription("Hosts a new game of the given type");
        setUsage("/game host <gameplay> <arena> [time]");
        setPermission("cloudgame.admin");
    }

    @Override
    public void onCommand(Player player, Arguments args) {
        if (args.length() < 3) {
            sendUsageMessage(player);
            return;
        }

        Gameplay gameplay = cloudGame.getGameplayManager().getGameplay(args.getRaw(1));
        if (gameplay == null) {
            player.sendMessage(ChatColor.RED + "That game type doesn't exist!");
            return;
        }

        Arena arena = cloudGame.getModelManager().getArenas().find(args.getRaw(2));
        if (arena == null) {
            gameplay.sendGameMessage(player, "That arena doesn't exist!");
            return;
        }

        Game game = cloudGame.getGameManager().createGame(gameplay, arena, new Host(player));
        if (game == null) {
            gameplay.sendGameMessage(player, "Failed to create game!");
            return;
        }

        // TODO: How do we handle gameplay-specific stuff like easy/hard & time
        (new HostedGameCountdown(game)).runTaskTimer(cloudGame, 2, 2);
        gameplay.sendGameMessage(player, "Scheduled game to start!");
    }
}