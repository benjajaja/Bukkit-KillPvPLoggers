package com.yermocraft.Gipsy;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TaggedPlayerList {
	
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
		addTag(victim);
		
		
		victim.sendMessage(messageVictim.replaceAll("\\$1", attacker.getDisplayName()));
		
		attacker.sendMessage(messageAttacker.replaceAll("\\$1", victim.getDisplayName()));
	}

	private void addTag(final Player victim) {
		if (taggedPlayers.containsKey(victim.getName())) {
			taggedPlayers.get(victim.getName()).cancel();
		}
		
		int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				taggedPlayers.remove(victim.getName());
				victim.sendMessage(messageVictimFree);
				
			}
		}, timeout * 20);
		
		taggedPlayers.put(victim.getName(), new Tag(id));
	}


	public void punishQuitter(Player player) {
		if (remove(player)) {
			//player.getWorld().strikeLightningEffect(player.getLocation());
			
			player.getWorld().playEffect(player.getLocation(), Effect.GHAST_SHRIEK, 0, 20);
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 20);
			
			player.setMetadata("hasPvPlogged", new FixedMetadataValue(plugin, true));
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
