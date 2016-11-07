package amber.sienna.roleplay;

import amber.sienna.assets.ConfigurationHandler;
import amber.sienna.main.Main;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
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

public class RoleplayManager {
    @Getter private final static RoleplayManager manager = new RoleplayManager();
    private HashMap<UUID, Roleplay> roleplays = new HashMap<>();
    @Getter private HashMap<UUID, Boolean> roleplaying = new HashMap<>();
    @Getter private HashSet<UUID> spying = new HashSet<>();
    private HashSet<Invite> invites = new HashSet<>();
    private Invite result;
    private HashSet<Invite> cache = new HashSet<>();
    @Getter private HashMap<UUID, BukkitTask> disconnects = new HashMap<>();

    public boolean isInRoleplay(Player player){
        return roleplays.containsKey(player.getUniqueId());
    }

    public void createRoleplay(Player player, String roleplayName){
        if(roleplays.containsKey(player.getUniqueId())){
            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getAlreadyInRoleplay()));
        }else{
            if(roleplays.values().stream().anyMatch(roleplay -> roleplay.getRoleplayName().equals(roleplayName))){
                player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getRoleplayExists()));
            }else{
                Roleplay roleplay = new Roleplay(player, roleplayName);
                roleplays.put(player.getUniqueId(), roleplay);
                setRoleplaying(player, false);
                player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &aYou have successfully created the roleplay &6"+roleplay.getRoleplayName()+"&a!"));
            }
        }
    }

    public void inviteToRoleplay(Player player, Player target){
        if(roleplays.containsKey(player.getUniqueId())){
            if(!roleplays.containsKey(target.getUniqueId())){
                if(player.getUniqueId().equals(roleplays.get(player.getUniqueId()).getOwner().getUniqueId())){
                    if(roleplays.get(player.getUniqueId()).getPlayers().size()==ConfigurationHandler.getHandler().getMaxRoleplayers()){
                        if(!player.hasPermission("roleplay.donor")){
                            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou have to have a donator rank to invite more people!"));
                            return;
                        }
                    }
                    invites.add(new Invite(roleplays.get(player.getUniqueId()), target));
                    player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &aYou have invited "+target.getName()+" to your roleplay!"));
                    target.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &6"+player.getName()+" &ahas invited you to the roleplay: &d"+roleplays.get(player.getUniqueId()).getRoleplayName()+"&a!"));
                }else{
                    player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou must be the owner of the roleplay to invite others!"));
                }
            }else{
                player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cThat player is already in a roleplay!"));
            }
        }else{
            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou must be the owner of a roleplay to invite others!"));
        }
    }

    public Roleplay getRoleplayForUser(Player player){
        return roleplays.get(player.getUniqueId());
    }

    public boolean isRoleplaying(Player player){
        return isInRoleplay(player) && roleplaying.get(player.getUniqueId());
    }

    public void setRoleplaying(Player player, boolean roleplaying){
        this.roleplaying.put(player.getUniqueId(), roleplaying);
    }

    public void cleanupUser(Player player){
        if(roleplays.get(player.getUniqueId()).getOwner().getUniqueId().equals(player.getUniqueId())){
            disbandRoleplay(roleplays.get(player.getUniqueId()));
            return;
        }
        roleplays.get(player.getUniqueId()).removeFromRoleplay(player);
        roleplays.get(player.getUniqueId()).getPlayers().forEach(roleplayer ->{
            if(roleplayer!=null && roleplayer.isOnline()) roleplayer.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix() + " &c" + player.getName() + " has left the roleplay!"));
        });
        roleplays.remove(player.getUniqueId());
        roleplaying.remove(player.getUniqueId());
    }

    public void disbandRoleplay(Roleplay roleplay){
        roleplay.getPlayers().forEach(roleplayer ->{
            roleplays.remove(roleplayer.getUniqueId());
            roleplaying.remove(roleplayer.getUniqueId());
            if(!roleplayer.isOnline()) return;
            roleplayer.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cThe roleplay '"+roleplay.getRoleplayName()+"' that you were in has been disbanded!"));
        });
    }

    public boolean isRoleplayOwner(Player player){
        return roleplays.containsKey(player.getUniqueId()) && roleplays.get(player.getUniqueId()).getOwner().getUniqueId().equals(player.getUniqueId());
    }

    public void toggleSpy(Player player){
        if(player.hasPermission("roleplay.spy")){
           if(spying.contains(player.getUniqueId())){
               spying.remove(player.getUniqueId());
               player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &bYour spy has been toggled &4&lOFF"));
           }else{
               spying.add(player.getUniqueId());
               player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &bYour spy has been toggled &2&lON"));
           }
        }else{
            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cSorry, you do not have permission to use this."));
        }
    }

    public void handleInvite(Player player, String targetRoleplay){
        result = null;
        invites.forEach(invite ->{
            if(invite.getPlayer().getUniqueId().equals(player.getUniqueId()) && invite.getRoleplay().getRoleplayName().equals(targetRoleplay)) result = invite;
        });
        if(result==null){
            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cEither the specified roleplay doesn't exist, or you don't have a valid invite to this roleplay!"));
        }else{
            invites.forEach(invite ->{
                if(invite.getPlayer().getUniqueId().equals(player.getUniqueId())) cache.add(invite);
            });
            cache.forEach(invite -> invites.remove(invite));
            cache.clear();
            result.getRoleplay().addToRoleplay(player);
            roleplays.put(player.getUniqueId(), result.getRoleplay());
            roleplaying.put(player.getUniqueId(), false);
            player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &aYou have joined the roleplay: &6"+result.getRoleplay().getRoleplayName()+"&a!"));
        }
    }

    public void cleanup(){
        roleplays.values().forEach(this::disbandRoleplay);
        roleplays.clear();
        disconnects.clear();
    }

    public void handleQuit(Player p){
        Roleplay rp = getRoleplayForUser(p);
        boolean isOwner = rp.getOwner().getUniqueId().equals(p.getUniqueId());
        rp.getPlayers().stream().filter(player -> player.isOnline() && !player.getUniqueId().equals(p.getUniqueId())).forEach(player -> player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix() + " &a" + p.getName() + " &ehas &cdisconnected &efrom the server!\n&e&oIf they rejoin before the specified time, " + ((isOwner) ? "the roleplay will not be disbanded!" : "they will not be kicked from the roleplay!"))));
    }

    public void handleRejoin(Player p){
        Roleplay rp = getRoleplayForUser(p);
        boolean isOwner = rp.getOwner().getUniqueId().equals(p.getUniqueId());
        rp.getPlayers().stream().filter(player -> player.isOnline() && !player.getUniqueId().equals(p.getUniqueId())).forEach(player -> player.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix() + " &a" + p.getName() + " &aHas reconnected, and " + ((isOwner) ? "the roleplay will not be disbanded!" : "they will not be kicked from the roleplay!"))));
    }
}
