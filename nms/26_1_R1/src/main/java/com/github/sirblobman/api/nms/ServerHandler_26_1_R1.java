package com.github.sirblobman.api.nms;


import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;


public final class ServerHandler_26_1_R1 extends ServerHandler {
    public ServerHandler_26_1_R1(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public int getMaxWorldSize() {
        Server server = getServer();
        return server.getMaxWorldSize();
    }

    @Override
    public double @NotNull [] getServerTpsValues() {
        return com.github.sirblobman.api.utility.paper.PaperHelper.getServerTpsValues();
    }

    @Override
    public @NotNull CommandMap getCommandMap() {
        Server server = getServer();
        if (!(server instanceof CraftServer)) {
            throw new UnsupportedOperationException("Server implementation is not the correct class.");
        }

        CraftServer craftServer = (CraftServer) server;
        return craftServer.getCommandMap();
    }
}
