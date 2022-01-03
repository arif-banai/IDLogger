package me.arifbanai.idLogger.utils;

import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.*;

public class ChatUtils {

    public static final String prefixDatabase = "idlogger_";

    private static final String prefix = GRAY + "[" + GREEN + "IDLogger" + GRAY + "] " + RESET;

    public static void sendSuccess(CommandSender sender, String msg) {
        sender.sendMessage(prefix + GREEN + msg);
    }
}
