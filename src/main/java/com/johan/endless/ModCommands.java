/*
 * ModCommands.java -
 *
 * Author: Johan Lebek
 * Created at: Sun Feb 16 10:47:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class ModCommands extends CommandBase {
    private static final List<String> aliases = new ArrayList<String>();

    public ModCommands() {
        aliases.add("setautogg");
    }

    @Override
    public String getCommandName() {
        return "setautogg";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setautogg [message]";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cUsage: /setautogg [message]"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1) {
                sb.append(" ");
            }
        }
        Endless.autogg = sb.toString();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getCommandName().compareTo(o.getCommandName());
    }
}
