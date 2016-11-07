package amber.sienna.roleplay;

import amber.sienna.assets.ConfigurationHandler;
import amber.sienna.main.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

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

public class Roleplay {
    @Getter @Setter private Player owner;
    @Getter private LinkedHashSet<Player> players = new LinkedHashSet<>();
    @Getter @Setter private String roleplayName;
    private long creationTime;
    @Getter @Setter private boolean scheduledForDeletion;

    public Roleplay(Player owner, String roleplayName){
        this.owner = owner;
        players.add(owner);
        this.roleplayName = roleplayName;
        creationTime = System.currentTimeMillis();
        scheduledForDeletion = false;
    }

    public void addToRoleplay(Player player){
        players.add(player);
        players.stream().filter(p -> p.getUniqueId()!=player.getUniqueId()).forEach(p -> p.sendMessage(Main.getInstance().colorise(ConfigurationHandler.getHandler().getPrefix()+" &6"+player.getName()+" &ahas joined the roleplay!")));
    }

    public void removeFromRoleplay(Player player){
        players.remove(player);
    }

    @Override
    public String toString(){
        String toReturn = "&6&l&m==============&r &5&lRoleplay&r &6&l&m==============&r\n" +
                "&bOwner: &c"+ owner.getName() + "\n" +
                "&bPlayers:\n";
        for(Player p : players){
            toReturn += (p.getUniqueId().equals(owner.getUniqueId()) ? "" : "&a- " + p.getName()) + "\n";
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - creationTime);
        long minutes = 0;
        long hours = 0;
        while(seconds >= 3600){
            seconds = seconds - 3600;
            hours++;
        }
        while(seconds >= 60){
            seconds = seconds - 60;
            minutes++;
        }
        toReturn += "&aThis roleplay is "+((hours > 0) ? hours+((hours==1) ? " hour, " : " hours, "): "")+
                ((minutes > 0) ? minutes+((minutes==1) ? " minute, " : " minutes, ") : "")+
                seconds+((seconds==1) ? " second" : " seconds")+" old.";
        return toReturn+"\n&6&l&m==============&r &5&lRoleplay&r &6&l&m==============&r";
    }

}
