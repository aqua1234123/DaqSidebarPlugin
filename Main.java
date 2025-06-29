package com.daqpvp.DaqSidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private HashMap<UUID, Integer> coins = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("DaqSidebar Enabled!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            setupSidebar(player);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("DaqSidebar Disabled!");
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            UUID id = killer.getUniqueId();
            coins.put(id, coins.getOrDefault(id, 0) + 1);
            updateSidebar(killer);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID id = player.getUniqueId();
                int current = coins.getOrDefault(id, 0);
                if (current >= 100) {
                    coins.put(id, current - 100);
                    player.getInventory().addItem(new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND_SWORD));
                    player.sendMessage(ChatColor.GREEN + "Kit purchased!");
                    updateSidebar(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough coins!");
                }
                return true;
            }
        }
        return false;
    }

    private void setupSidebar(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("sidebar", "dummy", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "DaqPVP");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore(ChatColor.GREEN + "$ Coins:").setScore(4);
        obj.getScore(ChatColor.RED + "❤ Lives: 10/10").setScore(3);
        obj.getScore(ChatColor.GOLD + "⚔ Killstreak: 0").setScore(2);
        obj.getScore(ChatColor.AQUA + "🕐 Combat Timer: 0").setScore(1);

        player.setScoreboard(board);
        updateSidebar(player);
    }

    private void updateSidebar(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective("sidebar");
        if (obj == null) return;

        board.resetScores(ChatColor.GREEN + "$ Coins:");
        int coin = coins.getOrDefault(player.getUniqueId(), 0);
        obj.getScore(ChatColor.GREEN + "$ Coins: " + ChatColor.WHITE + coin).setScore(4);
    }
}
