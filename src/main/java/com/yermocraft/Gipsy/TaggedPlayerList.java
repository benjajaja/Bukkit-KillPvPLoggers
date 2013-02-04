package com.yermocraft.Gipsy;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TaggedPlayerList {
	Logger log = Logger.getLogger("KillPvPLoggers");
	private class Tag {
		private int taskId;

		public Tag(int id) {
			this.taskId = id;
		}

		public void cancel() {
			plugin.getServer().getScheduler().cancelTask(taskId);
		}
	}
	
	HashMap<String, Tag> taggedPlayers = new HashMap<String, Tag>();
	HashMap<String, Tag> attackingPlayers = new HashMap<String, Tag>();
	private KillPvPLoggers plugin;
	
	private String messageVictim;
	private String messageAttacker;
	private String messageVictimFree;
	private String messageDeath;
	private int timeout;
	
	public TaggedPlayerList(KillPvPLoggers killPvPLoggers, int timeout, ConfigurationSection messages) {
		this.plugin = killPvPLoggers;
		this.timeout = timeout;
		
		messageVictim = messages.getString("victim");
		messageAttacker = messages.getString("attacker");
		messageVictimFree = messages.getString("victimFree");
		messageDeath = messages.getString("death");
	}

	public void tagPlayer(Player victim, Player attacker) {
		if (!taggedPlayers.containsKey(victim.getName())) {
			victim.sendMessage(messageVictim.replaceAll("\\$1", attacker.getDisplayName()));
			log.info(attacker.getName() + " has entered combat with " + victim.getName());
		}
		if ((!taggedPlayers.containsKey(attacker.getName())) && (!attackingPlayers.containsKey(attacker.getName()))) {
			attacker.sendMessage(messageAttacker.replaceAll("\\$1", victim.getDisplayName()));
		}
		addTag(victim, attacker);
		
	}

	private void addTag(final Player victim, final Player attacker) {
		if (taggedPlayers.containsKey(victim.getName())) {
			taggedPlayers.get(victim.getName()).cancel();
		}
		if (attackingPlayers.containsKey(attacker.getName())) {
			attackingPlayers.get(attacker.getName()).cancel();
		}		
		int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			public void run() {
				taggedPlayers.remove(victim.getName());
				attackingPlayers.remove(attacker.getName());
				//victim.sendMessage(messageVictimFree);
				
				log.info("Tagged Players: " + taggedPlayers);
				log.info("Attacking Players: " + attackingPlayers);
				
				log.info(victim.getName() + " is out of combat");
				
			}
		}, timeout * 20);
		
		taggedPlayers.put(victim.getName(), new Tag(id));
		attackingPlayers.put(attacker.getName(), new Tag(id));
	}


	public void punishQuitter(Player player) {
		if (remove(player)) {
			player.getWorld().strikeLightningEffect(player.getLocation());
			
			player.getWorld().playEffect(player.getLocation(), Effect.GHAST_SHRIEK, 0, 20);
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 20);
			
			player.setMetadata("hasPvPlogged", new FixedMetadataValue(plugin, true));
			log.info("Slaying " + player.getName() + " for PvP Logging");
			player.damage(1000);
		}
	}

	public boolean remove(Player player) {
		if (!taggedPlayers.containsKey(player.getName())) {
			return false;
		}
		
		taggedPlayers.get(player.getName()).cancel();
		taggedPlayers.remove(player.getName());
		player.removeMetadata("hasPvPlogged", plugin);		
		return true;
	}

	public String getDeathMessage(Player player) {
		return messageDeath.replaceAll("\\$1", player.getDisplayName());
		
	}
}
