package GipsyKing.KillPvPLoggers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * KillPvPLoggers block listener
 * @author GipsyKing
 */
public class KillPvPLoggersBlockListener implements Listener {
    private final KillPvPLoggers plugin;

    public KillPvPLoggersBlockListener(final KillPvPLoggers plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
	public void onAttacked(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		EntityDamageEvent e = event.getEntity().getLastDamageCause();
		if (!(e instanceof EntityDamageByEntityEvent)) {
	      return;
	    }
		
		EntityDamageByEntityEvent dEvent = (EntityDamageByEntityEvent)e;
		if (!(dEvent.getDamager() instanceof Player)) {
	      return;
	    }
		
		Player attacker = (Player)dEvent.getDamager();
		Player victim = (Player)event.getEntity();
		
		victim.sendMessage(attacker.getDisplayName() + " has tagged you as under-attack");
	}
}
