package com.yermocraft.Gipsy;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
/**
 * KillPvPLoggers block listener
 * @author GipsyKing
 */
public class KillPvPLoggersListener implements Listener {
	Logger log = Logger.getLogger("KillPvPLoggers");
	
	private TaggedPlayerList list;
	private ChatColor deathMessageColor = null;
    
    public KillPvPLoggersListener(TaggedPlayerList list, ChatColor deathMessageColor) {
        this.list = list;
        this.deathMessageColor = deathMessageColor;
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
	public void onAttacked(EntityDamageByEntityEvent event) {
    	if (event.isCancelled()) {
    		return;
    	}
    	
    	if (!(event.getEntity() instanceof Player)) {
			return;
		}
    	
    	
		if ((event.getDamager() instanceof Player)) {
		
			list.tagPlayer((Player)event.getEntity(), (Player)event.getDamager());
			
		} else if ((event.getDamager() instanceof Arrow)) {
			LivingEntity shooter = ((Arrow)event.getDamager()).getShooter();
			if (shooter instanceof Player) {
				list.tagPlayer((Player)event.getEntity(), (Player) shooter);
			}
		}
	}

    @EventHandler(priority=EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
    	list.punishQuitter(event.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event) {
    	if (event.getEntity().hasMetadata("hasPvPlogged")) {
    		event.setDeathMessage(list.getDeathMessage(event.getEntity()));
    	}
    	
    	if (deathMessageColor != null) {
    		event.setDeathMessage(deathMessageColor + event.getDeathMessage());
    	}
    	
    	list.remove(event.getEntity());
    }
}
