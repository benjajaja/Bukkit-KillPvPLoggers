package com.yermocraft.Gipsy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * KillPvPLoggers for Bukkit
 *
 * @author GipsyKing
 */
public class KillPvPLoggers extends JavaPlugin {
	
    public void onEnable() {
    	saveDefaultConfig();
    	
    	FileConfiguration config = getConfig();
    	
    	
    	ChatColor deathMessageColor = null;
    	if (config.getString("deathMessageColor", "false").equals("false")) {
    		try {
    			deathMessageColor = ChatColor.valueOf(config.getString("deathMessageColor"));
    		} catch (Exception e) {
    			getLogger().warning("Bad value for deathMessageColor: " + config.getString("deathMessageColor"));
    		}
    	}
    	
    	ConfigurationSection messages = config.getConfigurationSection("messages");
    	
        getServer().getPluginManager().registerEvents(
        		new KillPvPLoggersListener(new TaggedPlayerList(this, config.getInt("timeout"), messages), deathMessageColor), this);
    }
    
    public void onDisable() {
    	getServer().getScheduler().cancelTasks(this);

    	getLogger().info(getName() + " ended remaining tasks");
    }
}

