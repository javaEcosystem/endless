/*
 * CmdF8.java -
 *
 * Author: Johan Lebek
 * Created at: Sat Mar 8 23:00:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless.commands;

import com.johan.endless.Endless;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class CmdF8 extends CommandBase {
    private static final List<String> aliases = new ArrayList<String>();

    public CmdF8() {
        aliases.add("setf8");
    }

    @Override
    public String getCommandName() {
        return "setf8";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setf8 [message]";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cUsage: /setf8 [message]"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1) {
                sb.append(" ");
            }
        }
        Endless.cmdF8 = sb.toString();
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
