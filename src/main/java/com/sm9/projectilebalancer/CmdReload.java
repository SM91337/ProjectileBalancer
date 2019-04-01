package com.sm9.projectilebalancer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import static com.sm9.projectilebalancer.ProjectileBalancer.loadConfig;

public class CmdReload extends CommandBase {
    @Override
    public String getName() {
        return "pbreload";
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "pbreload";
    }

    @Override
    public void execute(MinecraftServer localServer, ICommandSender commandSender, String[] sArgs) throws CommandException {
        loadConfig();
        commandSender.sendMessage(new TextComponentString("Projectile balancer config reloaded successfully!"));
    }
}