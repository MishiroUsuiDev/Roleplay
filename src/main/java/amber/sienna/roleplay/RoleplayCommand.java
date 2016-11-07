package amber.sienna.roleplay;

import amber.sienna.assets.ConfigurationHandler;
import amber.sienna.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class RoleplayCommand implements CommandExecutor{


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(args.length == 0){
            if(RoleplayManager.getManager().isInRoleplay(p)){
                p.sendMessage(Main.getInstance().colorise(RoleplayManager.getManager().getRoleplayForUser(p).toString()));
            }else{
                p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cInvalid syntax!\n&aUse &b/rp help &ato view the valid commands."));
            }

        }else if(args.length == 1){
            switch(args[0].toLowerCase()){
                case "chat":
                    if(RoleplayManager.getManager().isInRoleplay(p)){
                        RoleplayManager.getManager().setRoleplaying(p, !RoleplayManager.getManager().isRoleplaying(p));
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &aYou have toggled your roleplay chat "+((RoleplayManager.getManager().isRoleplaying(p) ? "&a&lon" : "&c&loff"))));
                    }else{
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou have to be in a roleplay before you can use this command!"));
                    }
                    break;
                case "disband":
                    if(RoleplayManager.getManager().isInRoleplay(p)){
                        if(RoleplayManager.getManager().isRoleplayOwner(p)){
                            RoleplayManager.getManager().disbandRoleplay(RoleplayManager.getManager().getRoleplayForUser(p));
                        }else{
                            p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou must be in a roleplay that you own, to disband it!"));
                        }
                    }else{
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou must be in a roleplay that you own, to disband it!"));
                    }
                    break;
                case "help":
                    p.sendMessage(Main.getInstance().colorise(
                            "&6&l&m==============&r &c&lHelp &6&l&m==============&r\n" +
                            "&f- &b/rp accept <name> &aAccepts an invite for the specified roleplay.\n" +
                            "&f- &b/rp chat &aToggles the private roleplay chat.\n" +
                            "&f- &b/rp create <name> &aCreates a roleplay with the specified name.\n" +
                            "&f- &b/rp disband &aDisbands the roleplay. &cRP owner only!\n" +
                            "&f- &b/rp info &aDisplays information about the roleplay.\n" +
                            "&f- &b/rp invite <name> &aInvites a player to the roleplay. &cRP owner only!\n" +
                            "&f- &b/rp kick <name> &aKicks the player from the RP. &cRP owner only!\n" +
                            "&f- &b/rp leave &aLeaves your current roleplay.\n" +
                            "&6&l&m==============&r &c&lHelp &6&l&m==============&r"));
                    break;
                case "info":
                    if(RoleplayManager.getManager().isInRoleplay(p)){
                        p.sendMessage(Main.getInstance().colorise(RoleplayManager.getManager().getRoleplayForUser(p).toString()));
                    }else{
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou need to be in a roleplay to use this."));
                    }
                    break;
                case "leave":
                    if(RoleplayManager.getManager().isInRoleplay(p)){
                        RoleplayManager.getManager().cleanupUser(p);
                    }else{
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cYou aren't currently in a roleplay."));
                    }
                    break;
                case "reload":
                    if(p.hasPermission("roleplay.admin")){
                        long start = System.currentTimeMillis();
                        ConfigurationHandler.getHandler().loadConfig();
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &aReloaded configuration in "+(System.currentTimeMillis()-start)+"ms!"));
                    }
                    break;
                case "spy":
                    RoleplayManager.getManager().toggleSpy(p);
                    break;
                default:
                    p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cInvalid syntax!\n&aUse &b/rp help &ato view the valid commands."));
                    break;
            }
        }else if(args.length == 2){
            switch(args[0].toLowerCase()){
                case "accept":
                    RoleplayManager.getManager().handleInvite(p, args[1]);
                    break;
                case "create":
                    RoleplayManager.getManager().createRoleplay(p, args[1]);
                    break;
                case "add":
                case "invite":
                    Player target = null;
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getName().equals(args[1])) {
                            target = player;
                            break;
                        }
                    }
                    if(target==null){
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cSorry, I couldn't find a player by that name."));
                    }else{
                        RoleplayManager.getManager().inviteToRoleplay(p, target);
                    }
                    break;
                case "remove":
                case "kick":
                    if(!RoleplayManager.getManager().isRoleplayOwner(p)){
                        p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix() + " &cYou must be in a roleplay that you own, to kick a player in your roleplay!"));
                    }else{
                        Player targ = null;
                        for(Player player : Bukkit.getOnlinePlayers()){
                            if(player.getName().equals(args[1])) {
                                targ = player;
                                break;
                            }
                        }
                        if(targ==null){
                            p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cSorry, I couldn't find a player by that name."));
                        }else{
                            RoleplayManager.getManager().cleanupUser(p);
                        }
                    }
                    break;
                default:
                    p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cInvalid syntax!\n&aUse &b/rp help &ato view the valid commands."));
                    break;
            }
        }else{
            p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &cInvalid syntax!\n&aUse &b/rp help &ato view the valid commands."));
        }
        return true;
    }
}
