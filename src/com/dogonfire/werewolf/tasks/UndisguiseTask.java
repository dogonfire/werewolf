package com.dogonfire.werewolf.tasks;

import java.util.UUID;

import com.dogonfire.werewolf.Werewolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.ScoreboardManager;

public class UndisguiseTask implements Runnable
{
	public Werewolf	plugin;
	public UUID	playerId;
	private boolean	makeVisible;
	private boolean	forever;

	public UndisguiseTask(Werewolf instance, UUID playerId, boolean visible, boolean forever)
	{
		this.plugin = instance;
		this.playerId = playerId;
		this.makeVisible = visible;
		this.forever = forever;
	}

	public void run()
	{
		Player player = this.plugin.getServer().getPlayer(this.playerId);
		ScoreboardManager manager;
		
		if (player != null)
		{
			
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.CONFUSION);
			// Walkspeed works sooo, but let's still remove it...
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.SPEED);
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.HUNGER);
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.NIGHT_VISION);
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.INCREASE_DAMAGE);
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.REGENERATION);
			Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.JUMP);
			
			player.setWalkSpeed(0.2F);
			
			// \/ what? \/
			if (!this.plugin.usePounce)
			{
				Werewolf.pu.removePotionEffectNoGraphic(player, PotionEffectType.JUMP);
			}
			else
			{
				player.setAllowFlight(false);
			}
			
			/*
			String playerListName = Werewolf.getWerewolfManager().getPlayerListName(player);
			if (playerListName == null)
			{
				this.plugin.logDebug("Could not find playerlist name for " + player.getName());
			}
			else
			{
				player.setPlayerListName(playerListName);
			}*/
			player.setPlayerListName(player.getDisplayName());

			// Before trying to undisguise, check if Disguises are enabled...
			if (this.makeVisible && plugin.disguisesEnabled)
			{
				Werewolf.getSkinManager().unsetWerewolfSkin(player);
			}
			if (this.plugin.healthBarEnabled)
			{
				manager = Bukkit.getScoreboardManager();
				player.setScoreboard(manager.getMainScoreboard());
			}
			
			if(plugin.useScoreboards)
			{
				Werewolf.getWerewolfScoreboardManager().removePlayerHuntingScoreboard(player);
			}		
		}
				
		Werewolf.getWerewolfManager().popPlayerData(this.playerId);
		
		if (this.plugin.useWerewolfGroupName && player != null)
		{
			String groupName = Werewolf.getWerewolfManager().getOriginalPermissionGroup(playerId);
			if ((groupName != null) && (!groupName.equals(this.plugin.werewolfGroupName)))
			{
				this.plugin.logDebug("Putting " + player.getName() + " into group " + groupName);

				String playerName = plugin.getServer().getOfflinePlayer(playerId).getName();
				
				Werewolf.getPermissionsManager().setGroup(playerName, groupName);
			}
		}
		
		if (this.forever)
		{
			Werewolf.getWerewolfManager().setNotWerewolf(this.playerId);
		}
		else if (player != null)
		{
			Werewolf.getWerewolfManager().setHumanForm(this.playerId, player.getName());
		}
		
		Werewolf.getWerewolfManager().clearNumberOfPackWolvesForPlayer(this.playerId);
		
		if(player!=null)
		{
			Werewolf.getStatisticsManager().clearStatistics(player.getUniqueId());
		}
	}
}