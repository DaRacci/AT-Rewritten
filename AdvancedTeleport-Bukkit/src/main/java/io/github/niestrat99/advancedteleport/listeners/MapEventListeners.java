package io.github.niestrat99.advancedteleport.listeners;

import io.github.niestrat99.advancedteleport.api.Warp;
import io.github.niestrat99.advancedteleport.api.events.warps.WarpPostCreateEvent;
import io.github.niestrat99.advancedteleport.config.NewConfig;
import io.github.niestrat99.advancedteleport.managers.PluginHookManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MapEventListeners implements Listener {

    @EventHandler
    public void onWarpAdd(WarpPostCreateEvent event) {
        if (!NewConfig.get().ADD_WARPS.get()) return;
        Warp warp = event.getWarp();
        PluginHookManager.get().getMapPlugins().values().forEach(mapPlugin -> mapPlugin.addWarp(warp));
    }

}