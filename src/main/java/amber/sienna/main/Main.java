package amber.sienna.main;

import amber.sienna.assets.ConfigurationHandler;
import amber.sienna.roleplay.RoleplayCommand;
import amber.sienna.roleplay.RoleplayEvents;
import amber.sienna.roleplay.RoleplayManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static org.bukkit.Bukkit.getScheduler;

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

public class Main extends JavaPlugin {
    @Getter private static Main instance;

    public String colorise(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Override
    public void onEnable(){
        instance = this;
        getLogger().log(Level.INFO, "Initialising Roleplay V.{0}", getDescription().getVersion());
        getCommand("roleplay").setExecutor(new RoleplayCommand());
        getServer().getPluginManager().registerEvents(new RoleplayEvents(), this);
        ConfigurationHandler.getHandler().loadConfig();
        getLogger().log(Level.INFO, "Initialised!");
    }

    @Override
    public void onDisable(){
        getLogger().log(Level.INFO, "Shutting down Roleplay V.{0}", getDescription().getVersion());
        getScheduler().cancelTasks(Main.getInstance());
        RoleplayManager.getManager().cleanup();
    }
}
