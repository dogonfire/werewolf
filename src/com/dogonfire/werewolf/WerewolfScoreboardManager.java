package com.dogonfire.werewolf;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class WerewolfScoreboardManager
{
	private ScoreboardManager manager;
	private HashMap<UUID, Objective> playerHuntingObjectives = new HashMap<UUID, Objective>();
	
	WerewolfScoreboardManager()
	{
		manager = Bukkit.getScoreboardManager();
	}

	// Hunting Scoreboard \\

	public void newPlayerHuntingScoreboard(Player player)
	{
		Scoreboard board = manager.getNewScoreboard();
	
		//Team team = board.registerNewTeam("teamname");
	
		// Setting the scoreboard/objective and it's display name
		Objective objective = board.registerNewObjective("WerewolfHunting", "dummy", ChatColor.DARK_AQUA + "Hunting Status");
	
		// Setting where to display the scoreboard/objective (either SIDEBAR, PLAYER_LIST or BELOW_NAME)
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	 
		// Setting the display name of the scoreboard/objective
		//objective.setDisplayName(ChatColor.DARK_AQUA + "Hunting Status");
		
		playerHuntingObjectives.put(player.getUniqueId(), objective);
		
		player.setScoreboard(board);		
		
		setMobKillsForPlayer(player, 0);
		setHumanKillsForPlayer(player, 0);	
		
		/* TODO: re-add Vampire integration..
		
		if(plugin.vampireEnabled)	
		{	
			setVampireKillsForPlayer(player, 0);				
		}
		*/
	}
	

	public void removePlayerHuntingScoreboard(Player player)
	{
		player.setScoreboard(manager.getNewScoreboard());
	}
	
	public void setMobKillsForPlayer(Player player, int kills)
	{
		Score score = playerHuntingObjectives.get(player.getUniqueId()).getScore(ChatColor.RED + "Mobs killed:"); //Get a fake offline player
		score.setScore(kills);
	}

	public void setHumanKillsForPlayer(Player player, int kills)
	{
		Score score = playerHuntingObjectives.get(player.getUniqueId()).getScore(ChatColor.RED + "Humans slain:"); //Get a fake offline player
		score.setScore(kills);
	}

	public void setVampireKillsForPlayer(Player player, int kills)
	{
		Score score = playerHuntingObjectives.get(player.getUniqueId()).getScore(ChatColor.RED + "Vampires slain:"); //Get a fake offline player
		score.setScore(kills);
	}


	// Scoreboard Teams \\
	public void showNametagForPlayer(Player player, Boolean showNametag)
	{
		Werewolf.instance().logDebug("running showNametagForPlayer(" + player.getName() + ", " + showNametag + ")");
		Scoreboard board = manager.getMainScoreboard();

		if (board != null)
		{
			Werewolf.instance().logDebug("Board is not null for player!");
			if (Werewolf.getWerewolfManager().isWerewolf(player))
			{
				Werewolf.instance().logDebug("Player is a Werewolf!");
				Team werewolfTeam = null;

				// Make sure the team exists
				if (board.getTeam("Werewolf") != null)
				{
					werewolfTeam = board.getTeam("Werewolf");
				}
				else if (board.getTeam("Werewolf") == null)
				{
					werewolfTeam = board.registerNewTeam("Werewolf");
					werewolfTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
				}

				// Apply the edits necessary for the player
				if (!showNametag)
				{
					werewolfTeam.addEntry(player.getName());
					Werewolf.instance().logDebug("The player, " + player.getName() + ", is now added to the Werewolf Team (!NO NAMETAG!)");
				}
				else if (showNametag && werewolfTeam.getEntries().contains(player.getName()))
				{
					werewolfTeam.removeEntry(player.getName());
					Werewolf.instance().logDebug("The player, " + player.getName() + ", is now removed from the Werewolf Team (!NAMETAG SHOWN!)");
				}
				return;
			}
		}
		Werewolf.instance().logDebug("Could not find the main scoreboard!");
	}
}
