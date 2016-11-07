package amber.sienna.roleplay;

import amber.sienna.assets.ConfigurationHandler;
import amber.sienna.main.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Copyright 2016 Amber Sienna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RoleplayEvents implements Listener {
    @Getter private HashMap<UUID, RoleplayQuitRunnable> disconnects = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e){
        if(RoleplayManager.getManager().getDisconnects().containsKey(e.getPlayer().getUniqueId())){
            RoleplayManager.getManager().getDisconnects().get(e.getPlayer().getUniqueId()).cancel();
            RoleplayManager.getManager().getDisconnects().remove(e.getPlayer().getUniqueId());
            RoleplayManager.getManager().handleRejoin(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e){
        if(RoleplayManager.getManager().isInRoleplay(e.getPlayer())){
            RoleplayManager.getManager().getDisconnects().putIfAbsent(e.getPlayer().getUniqueId(), new RoleplayQuitRunnable(e.getPlayer()).runTaskLater(Main.getInstance(), ConfigurationHandler.getHandler().getDeletionDelay()));
            RoleplayManager.getManager().handleQuit(e.getPlayer());
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(RoleplayManager.getManager().isRoleplaying(e.getPlayer())) {
            final Roleplay rp = RoleplayManager.getManager().getRoleplayForUser(e.getPlayer());
            final String spy = Main.getInstance().colorise(ConfigurationHandler.getHandler().getSpyFormat().replace("%roleplay%", rp.getRoleplayName()).replace("%player%", e.getPlayer().getName()));
            String normies = Main.getInstance().colorise(ConfigurationHandler.getHandler().getFormat().replace("%player%", e.getPlayer().getName()));
            rp.getPlayers().forEach(roleplayer -> roleplayer.sendMessage(normies.replace("%message%", e.getMessage())));

            RoleplayManager.getManager().getSpying().forEach(spying -> {
                Player p = Bukkit.getPlayer(spying);
                if (p != null && p.isOnline()) {
                    p.sendMessage(spy.replace("%message%", e.getMessage()));
                }
            });
            e.setCancelled(true);
        }else{
            RoleplayManager.getManager().getRoleplaying().keySet().forEach(uuid ->{
                if(RoleplayManager.getManager().getRoleplaying().get(uuid)){
                    e.getRecipients().remove(Bukkit.getPlayer(uuid));
                }
            });
        }
    }
}
