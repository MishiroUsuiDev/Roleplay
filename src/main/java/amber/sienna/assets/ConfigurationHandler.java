package amber.sienna.assets;


import amber.sienna.main.Main;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

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

public class ConfigurationHandler {
    @Getter private static final ConfigurationHandler handler = new ConfigurationHandler();
    private final File f = new File(Main.getInstance().getDataFolder(), "config.yml");
    private FileConfiguration cfg = Main.getInstance().getConfig();
    @Getter private String prefix;
    @Getter private String format;
    @Getter private String spyFormat;
    @Getter private String roleplayExists;
    @Getter private String alreadyInRoleplay;
    @Getter private String successfulCreate;
    @Getter private int maxRoleplayers;
    @Getter private int deletionDelay;

    public void loadConfig(){
        if(!f.exists()) createConfig();

        prefix = cfg.getString("prefix");
        format = cfg.getString("formats.player").replace("%prefix%", prefix);
        spyFormat = cfg.getString("formats.spy").replace("%prefix%", prefix);
        roleplayExists = cfg.getString("messages.roleplay-exists").replace("%prefix%", prefix);
        alreadyInRoleplay = cfg.getString("messages.already-roleplaying").replace("%prefix%", prefix);
        successfulCreate = cfg.getString("messages.successful-creation").replace("%prefix%", prefix);
        maxRoleplayers = cfg.getInt("max-roleplayers");
        deletionDelay = cfg.getInt("deletion-delay-seconds")*20;
    }

    private void createConfig(){
        cfg.addDefault("prefix", "&dRoleplay&5&l>");
        cfg.addDefault("formats.player", "%prefix% &d%player%&b&l> &f%message%");
        cfg.addDefault("formats.spy", "&5%roleplay% &d%player%&b&l> &f%message%");
        cfg.addDefault("messages.roleplay-exists", "%prefix% &cSorry, there is already a roleplay with this name.");
        cfg.addDefault("messages.already-roleplaying", "%prefix% &cSorry, you are already taking part in a roleplay.");
        cfg.addDefault("messages.successful-creation", "%prefix% &aSuccessfully created the roleplay; &d%roleplay%");
        cfg.addDefault("max-roleplayers", 5);
        cfg.addDefault("deletion-delay-seconds", 60);

        cfg.options().copyDefaults(true);
        Main.getInstance().saveConfig();
    }
}
