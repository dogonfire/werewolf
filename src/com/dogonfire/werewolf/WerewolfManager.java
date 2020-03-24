package com.dogonfire.werewolf;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.dogonfire.werewolf.ClanManager.ClanType;
import com.dogonfire.werewolf.tasks.CheckTransformationTask;
/*
import org.bukkit.block.Biome;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
*/

public class WerewolfManager
{
	private Werewolf plugin;

	static enum WolfState {
		None, Infected, HumanForm, WolfForm;
	}

	private FileConfiguration		werewolvesConfig				= null;
	private File					werewolvesConfigFile			= null;
	private Random					random							= new Random();
	private long					lastSaveTime					= 0L;
	private String					datePattern						= "HH:mm:ss dd-MM-yyyy";
	private HashMap<UUID, Long>		lastFullMoonAnnouncementTimes	= new HashMap<UUID, Long>();
	private HashMap<UUID, String>	originalGroup					= new HashMap<UUID, String>();
	private HashMap<UUID, String>	playerlistNames					= new HashMap<UUID, String>();
	private HashMap<UUID, Long>		playersPouncing					= new HashMap<UUID, Long>();
	private HashMap<UUID, Integer>	packWolves						= new HashMap<UUID, Integer>();
	private HashMap<UUID, Long>		lastWorldUpdates				= new HashMap<UUID, Long>();

	WerewolfManager(Werewolf plugin)
	{
		this.plugin = plugin;

		for (World world : this.plugin.getServer().getWorlds())
		{
			this.lastFullMoonAnnouncementTimes.put(world.getUID(), 0L);
		}
	}

	public void load()
	{
		if (this.werewolvesConfigFile == null)
		{
			this.werewolvesConfigFile = new File(this.plugin.getDataFolder(), "werewolves.yml");
		}
		this.werewolvesConfig = YamlConfiguration.loadConfiguration(this.werewolvesConfigFile);

		this.plugin.log("Loaded " + this.werewolvesConfig.getKeys(false).size() + " werewolves.");
	}

	public void saveTimed()
	{
		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}

		save();
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.werewolvesConfig == null) || (this.werewolvesConfigFile == null))
		{
			return;
		}
		try
		{
			this.werewolvesConfig.save(this.werewolvesConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.werewolvesConfigFile + ": " + ex.getMessage());
		}
	}

	public int getNumberOfWerewolves()
	{
		Set<String> werewolves = this.werewolvesConfig.getKeys(false);
		if (werewolves == null)
		{
			return 0;
		}
		return this.werewolvesConfig.getKeys(false).size();
	}

	public Player getNearestWerewolf(UUID playerId)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);
		Player minWerewolf = null;

		float minDist = 999999.0F;
		if (player == null)
		{
			return null;
		}

		for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers())
		{
			if (hasWerewolfSkin(onlinePlayer.getUniqueId()) && ((player.getWorld() == onlinePlayer.getWorld()) && (player.getEntityId() != onlinePlayer.getEntityId())))
			{
				float dist = (float) onlinePlayer.getLocation().toVector().subtract(player.getLocation().toVector()).length();
				if (dist < minDist)
				{
					minWerewolf = onlinePlayer;
					minDist = dist;
				}
			}
		}

		return minWerewolf;
	}

	public String getPlayerListName(Player player)
	{
		return (String) this.playerlistNames.get(player.getUniqueId());
	}

	public void pushPlayerData(Player player)
	{
		String groupName = "NoGroup";

		// this.plugin.logDebug("Putting " + player.getName() + ", " +
		// player.getUniqueId() + " into the playerData (" +
		// ChatColor.stripColor(player.getPlayerListName()) + ")");

		this.playerlistNames.put(player.getUniqueId(), ChatColor.stripColor(player.getPlayerListName()));
		this.originalGroup.put(player.getUniqueId(), groupName);

		setWolfForm(player.getUniqueId(), player.getName());
	}

	public void popPlayerData(UUID playerId)
	{
		if (this.playerlistNames.containsKey(playerId))
		{
			this.playerlistNames.remove(playerId);
		}

		if (this.originalGroup.containsKey(playerId))
		{
			this.originalGroup.remove(playerId);
		}
	}

	public String getOriginalPermissionGroup(UUID playerId)
	{
		return this.werewolvesConfig.getString(playerId.toString() + ".OriginalGroup");
	}

	public void setOriginalPermissionGroup(UUID playerId, String groupName)
	{
		this.werewolvesConfig.set(playerId.toString() + ".OriginalGroup", groupName);
		saveTimed();
	}

	public Collection<UUID> getOnlineWerewolvesInWolfForm()
	{
		Set<UUID> werewolfPlayers = new HashSet<UUID>();

		for (World world : plugin.getServer().getWorlds())
		{
			for (UUID playerId : getOnlineWerewolves(world))
			{
				if (this.isWerewolf(playerId) && this.isWolfForm(playerId))
				{
					werewolfPlayers.add(playerId);
				}
			}
		}

		return werewolfPlayers;
	}

	public Collection<UUID> getOnlineWerewolvesInWolfForm(World world)
	{
		Set<UUID> werewolfPlayers = new HashSet<UUID>();

		for (UUID playerId : getOnlineWerewolves(world))
		{
			if (this.isWerewolf(playerId) && this.isWolfForm(playerId))
			{
				werewolfPlayers.add(playerId);
			}
		}

		return werewolfPlayers;
	}

	public Set<UUID> getOnlineWerewolves(World world)
	{
		Set<UUID> werewolfPlayers = new HashSet<UUID>();

		for (Player player : world.getPlayers())
		{
			if (isWerewolf(player))
			{
				werewolfPlayers.add(player.getUniqueId());
			}
		}
		return werewolfPlayers;
	}

	public Set<UUID> getAllWerewolves()
	{
		Set<UUID> werewolfList = new HashSet<UUID>();

		for (String playerIdString : this.werewolvesConfig.getKeys(false))
		{
			try
			{
				UUID playerId = UUID.fromString(playerIdString);

				werewolfList.add(playerId);
			}
			catch (Exception ex)
			{

			}
		}

		return werewolfList;
	}

	public Set<UUID> getWerewolvesInClan(ClanManager.ClanType clanType)
	{
		Set<UUID> werewolfList = new HashSet<UUID>();
		UUID playerId;

		for (String playerIdString : this.werewolvesConfig.getKeys(false))
		{
			try
			{
				playerId = UUID.fromString(playerIdString);
			}
			catch (Exception ex)
			{
				continue;
			}

			ClanType playerClanType = this.getWerewolfClan(playerId);

			if (playerClanType.equals(clanType))
			{
				werewolfList.add(playerId);
			}
		}

		return werewolfList;
	}

	public void sendMessageToClan(ClanType clan, String message)
	{
		for (UUID playerId : getWerewolvesInClan(clan))
		{
			plugin.sendInfo(plugin.getServer().getPlayer(playerId), message);
		}
	}

	public boolean makeWerewolf(Player player, boolean turnNow, ClanManager.ClanType clan)
	{
		if (turnNow)
		{
			setHumanForm(player.getUniqueId(), player.getName());

			setInfectedThisNight(player.getUniqueId(), player.getName(), false);

			setWerewolfClan(player.getUniqueId(), clan);

			setWerewolfName(player.getUniqueId());

			setWerewolfSkin(player);

			setWolfForm(player.getUniqueId(), player.getName());

			this.plugin.log(player.getName() + " was made an full werewolf");
		}
		else
		{
			setLastTransformation(player.getUniqueId());

			setInfectedThisNight(player.getUniqueId(), player.getName(), true);

			setWerewolfClan(player.getUniqueId(), clan);

			setWerewolfName(player.getUniqueId());

			setInfectedWerewolf(player.getUniqueId(), player.getName());

			this.plugin.log(player.getUniqueId() + " was made a infected werewolf");
		}
		return true;
	}

	public void unmakeWerewolf(UUID playerId)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player != null)
		{
			Werewolf.pu.addPotionEffectNoGraphic(player, new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
		}

		this.plugin.undisguiseWerewolf(playerId, true, true);

		if (this.plugin.useClans)
		{
			ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(playerId);
			Werewolf.getClanManager().assignAlphaInClan(clan, null);
		}

		this.plugin.log(plugin.getServer().getOfflinePlayer(playerId).getName() + " was unmade from being a werewolf");
	}

	public void setWerewolfSkin(Player player)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}

		if (!isWerewolf(player))
		{
			return;
		}

		if (hasWerewolfSkin(player.getUniqueId()))
		{
			return;
		}

		if (isInfectedThisNight(player.getUniqueId()))
		{
			return;
		}

		this.plugin.disguiseWerewolf(player);
	}

	public void unsetWerewolfSkin(UUID playerId, boolean makeVisible)
	{
		String playerName = plugin.getServer().getOfflinePlayer(playerId).getName();

		if (!isWerewolf(playerId))
		{
			this.plugin.logDebug("unsetWerewolfSkin(): " + playerName + " does not have werewolf skin");
			return;
		}

		setInfectedThisNight(playerId, playerName, false);

		if (!hasWerewolfSkin(playerId))
		{
			this.plugin.logDebug("unsetWerewolfSkin(): " + playerName + " does not have a werewolf skin");
			return;
		}

		if (isFullWerewolf(playerId))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);
			if (player != null)
			{
				String message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Untransform, ChatColor.LIGHT_PURPLE);
				player.sendMessage(message);
			}
		}

		this.plugin.log(playerName + " turned into human form!");

		this.plugin.undisguiseWerewolf(playerId, makeVisible, false);
	}

	public boolean canTransform(Player player)
	{
		return !hasWerewolfSkin(player.getUniqueId()) && !isInfectedThisNight(player.getUniqueId()) && this.plugin.isFullMoonInWorld(player.getWorld()) && this.plugin.isWerewolvesAllowedInWorld(player) && this.plugin.isUnderOpenSky(
				player) /*
						 * && !canControlledTransformation(player.getUniqueId())
						 */;
	}

	public boolean canUntransform(Player player)
	{
		return (hasWerewolfSkin(player.getUniqueId()) || isInfectedThisNight(player.getUniqueId())) && !this.plugin.isNightInWorld(player.getWorld()) && !canControlledTransformation(player.getUniqueId());
	}

	public boolean isWerewolf(Player player)
	{
		return isWerewolf(player.getUniqueId());
	}

	public boolean isWerewolf(UUID playerId)
	{
		if (hasWerewolfSkin(playerId))
		{
			return true;
		}

		String wolfState = this.werewolvesConfig.getString(playerId.toString() + ".WolfState");
		
		return wolfState != null;
	}

	public boolean isFullWerewolf(UUID playerId)
	{
		if (playerId == null)
		{
			return false;
		}

		String wolfType = this.werewolvesConfig.getString(playerId.toString() + ".WolfState");

		if (wolfType == null)
		{
			return false;
		}

		WolfState wolfState = WolfState.None;

		try
		{
			wolfState = WolfState.valueOf(wolfType);
		}
		catch (Exception ex)
		{
			setNotWerewolf(playerId);
			return false;
		}

		return (wolfState == WolfState.WolfForm) || (wolfState == WolfState.HumanForm);
	}

	public boolean isInfectedWerewolf(UUID playerId)
	{
		String wolfType = this.werewolvesConfig.getString(playerId.toString() + ".WolfState");
		if (wolfType == null)
		{
			return false;
		}
		WolfState wolfState = WolfState.None;
		try
		{
			wolfState = WolfState.valueOf(wolfType);
		}
		catch (Exception ex)
		{
			setNotWerewolf(playerId);
			return false;
		}
		return wolfState == WolfState.Infected;
	}

	public boolean isWolfForm(UUID playerId)
	{
		if (playerId == null)
		{
			return false;
		}

		String wolfType = this.werewolvesConfig.getString(playerId.toString() + ".WolfState");

		WolfState wolfState = WolfState.None;

		try
		{
			wolfState = WolfState.valueOf(wolfType);
		}
		catch (Exception ex)
		{
			setNotWerewolf(playerId);
			return false;
		}

		return wolfState == WolfState.WolfForm;
	}

	public boolean isHumanForm(UUID playerId)
	{
		if (playerId == null)
		{
			return false;
		}

		String wolfType = this.werewolvesConfig.getString(playerId.toString() + ".WolfState");

		if (wolfType == null)
		{
			return false;
		}

		WolfState wolfState = WolfState.None;

		try
		{
			wolfState = WolfState.valueOf(wolfType);
		}
		catch (Exception ex)
		{
			setNotWerewolf(playerId);
			return false;
		}

		return wolfState == WolfState.HumanForm;
	}

	public String getWerewolfName(UUID playerId)
	{
		if (playerId == null)
		{
			return "";
		}

		String werewolfName = this.werewolvesConfig.getString(playerId.toString() + ".WerewolfName");

		if (werewolfName == null || werewolfName.isEmpty())
		{
			setWerewolfName(playerId);
			werewolfName = this.werewolvesConfig.getString(playerId.toString() + ".WerewolfName");
		}

		return werewolfName;
	}

	public ClanManager.ClanType getWerewolfClan(UUID playerId)
	{
		if (playerId == null)
		{
			return ClanManager.ClanType.Potion;
		}

		String clanTypeString = this.werewolvesConfig.getString(playerId.toString() + ".Clan");

		ClanManager.ClanType clanType = ClanManager.ClanType.WerewolfBite;

		try
		{
			clanType = ClanManager.ClanType.valueOf(clanTypeString);
		}
		catch (Exception ex)
		{
			clanType = ClanManager.ClanType.values()[this.random.nextInt(ClanManager.ClanType.values().length)];
			this.werewolvesConfig.set(playerId.toString() + ".Clan", clanType.name());

			saveTimed();
		}

		return clanType;
	}

	public UUID getAlphaCandidate(ClanManager.ClanType clan)
	{
		List<UUID> clanMembers = Werewolf.getWerewolfManager().getWerewolfClanMembersRanked(clan);// new
																									// ArrayList<UUID>();

		/*
		 * for (String playerIdString : playerList) { try { UUID playerId =
		 * UUID.fromString(playerIdString);
		 * 
		 * String clanName = this.werewolvesConfig.getString(playerId.toString()
		 * + ".Clan"); if (clanName != null && clanName.equals(clan.name())) {
		 * clanMembers.add(playerId); } } catch(Exception ex) {
		 * 
		 * } }
		 * 
		 * Collections.sort(clanMembers, new TransformationsComparator());
		 */

		if (clanMembers.size() == 0)
		{
			return null;
		}

		// if(clanMembers.size()<2)
		// {
		return clanMembers.get(0);
		// }

		// int n = random.nextInt(clanMembers.size() - 1);

		// return clanMembers.get(n);
	}

	public class TransformationsComparator implements Comparator<UUID>
	{
		public int compare(UUID member1, UUID member2)
		{
			return Werewolf.getWerewolfManager().getNumberOfTransformations(member2) - Werewolf.getWerewolfManager().getNumberOfTransformations(member1);
		}
	}

	public List<String> getWerewolfClanMembers(ClanManager.ClanType clan)
	{
		Set<String> playerList = this.werewolvesConfig.getKeys(false);
		List<String> clanMembers = new ArrayList<String>();

		for (String playerName : playerList)
		{
			String clanString = this.werewolvesConfig.getString(playerName + ".Clan");
			if (clanString != null)
			{
				ClanManager.ClanType clanType = null;

				try
				{
					clanType = ClanManager.ClanType.valueOf(clanString);
				}
				catch (Exception localException)
				{
				}

				if (clanType.equals(clan))
				{
					clanMembers.add(playerName);
				}
			}
		}
		return clanMembers;
	}

	public List<UUID> getWerewolfClanMembersRanked(ClanManager.ClanType clan)
	{
		Set<String> playerList = this.werewolvesConfig.getKeys(false);
		List<UUID> clanMembers = new ArrayList<UUID>();

		for (String playerString : playerList)
		{
			try
			{
				UUID playerId = UUID.fromString(playerString);
				String clanName = this.werewolvesConfig.getString(playerId.toString() + ".Clan");

				if ((clanName != null) && clanName.equals(clan.name()))
				{
					clanMembers.add(playerId);
				}
			}
			catch (Exception ex)
			{

			}
		}

		Collections.sort(clanMembers, new TransformationsComparator());

		return clanMembers;
	}

	public boolean isInfectedThisNight(UUID playerId)
	{
		return this.werewolvesConfig.getBoolean(playerId.toString() + ".InfectedThisNight");
	}

	public void setInfectedThisNight(UUID playerId, String playerName, boolean thisNight)
	{
		this.werewolvesConfig.set(playerId.toString() + ".PlayerName", playerName);

		if (thisNight)
		{
			this.werewolvesConfig.set(playerId.toString() + ".InfectedThisNight", thisNight);
		}
		else
		{
			this.werewolvesConfig.set(playerId.toString() + ".InfectedThisNight", thisNight);
		}

		saveTimed();
	}

	public void setWerewolfClan(UUID playerId, ClanManager.ClanType clan)
	{
		if (Werewolf.getClanManager().getAlpha(clan) == null)
		{
			UUID alphaPlayerId = getAlphaCandidate(clan);

			if (alphaPlayerId == null)
			{
				Werewolf.getClanManager().assignAlphaInClan(clan, playerId);
			}
			else
			{
				Werewolf.getClanManager().assignAlphaInClan(clan, alphaPlayerId);
			}
		}

		this.werewolvesConfig.set(playerId.toString() + ".Clan", clan.name());

		saveTimed();
	}

	public void incrementNumberOfFullMoonTransformations(UUID playerId)
	{
		int transforms = this.werewolvesConfig.getInt(playerId.toString() + ".Transformations");

		transforms++;

		this.werewolvesConfig.set(playerId.toString() + ".Transformations", transforms);
	}

	public void setLastTransformation(UUID playerId)
	{
		DateFormat formatter = new SimpleDateFormat(this.datePattern);
		Date thisDate = new Date();

		this.werewolvesConfig.set(playerId.toString() + ".LastTransform", formatter.format(thisDate));

		saveTimed();
	}

	public int getNumberOfTransformations(UUID playerId)
	{
		return this.werewolvesConfig.getInt(playerId.toString() + ".Transformations");
	}

	public boolean canControlledTransformation(UUID playerId)
	{
		return getNumberOfTransformations(playerId) >= this.plugin.transformsForControlledTransformation;
	}

	public boolean hasToDropItems(UUID playerId)
	{
		return getNumberOfTransformations(playerId) < this.plugin.transformsForNoDropItems;
	}

	public boolean hasRecentTransformAutoCure(UUID playerId)
	{
		DateFormat formatter = new SimpleDateFormat(this.datePattern);
		Date thisDate = new Date();
		Date transformDate = null;

		String transformDateString = this.werewolvesConfig.getString(playerId.toString() + ".LastTransform");
		try
		{
			transformDate = formatter.parse(transformDateString);
		}
		catch (Exception ex)
		{
			this.plugin.log(playerId.toString() + " has invalid LastTransform date. Resetting.");
			transformDate = new Date();
			transformDate.setTime(0L);
		}

		long diff = thisDate.getTime() - transformDate.getTime();
		long diffDays = diff / 86400000L;

		return diffDays < this.plugin.autoCureDays;
	}

	public boolean hasRecentTransform(UUID playerId)
	{
		DateFormat formatter = new SimpleDateFormat(this.datePattern);
		Date thisDate = new Date();
		Date transformDate = null;

		String transformDateString = this.werewolvesConfig.getString(playerId.toString() + ".LastTransform");
		try
		{
			transformDate = formatter.parse(transformDateString);
		}
		catch (Exception ex)
		{
			this.plugin.log(plugin.getServer().getOfflinePlayer(playerId).getName() + " has invalid LastTransform date. Resetting.");
			transformDate = new Date();
			transformDate.setTime(0L);
		}

		long diff = thisDate.getTime() - transformDate.getTime();
		long diffSeconds = diff / 1000L;

		return diffSeconds < 600L;
	}

	public boolean hasWerewolfSkin(UUID playerId)
	{
		return this.playerlistNames.containsKey(playerId);
	}

	private String newWerewolfName()
	{
		String givenName = "";
		String surname = "";

		// Generate a random givenName
		List<String> strings = plugin.givenNames;
		if (strings.size() == 0)
		{
			this.plugin.log("No given name strings found in the config!");
		}
		else
		{
			givenName = (String) strings.toArray()[this.random.nextInt(strings.size())];
		}

		// Generate a random surname
		List<String> strings2 = plugin.surnames;
		if (strings2.size() == 0)
		{
			this.plugin.log("No surname strings found in the config!");
		}
		else
		{
			surname = (String) strings2.toArray()[this.random.nextInt(strings2.size())];
		}

		String fullName = givenName + surname;

		if (fullName.isEmpty())
		{
			fullName = "Werewolf";
		}

		return fullName;
	}

	public void setWerewolfName(UUID playerId)
	{
		this.werewolvesConfig.set(playerId.toString() + ".WerewolfName", newWerewolfName());

		saveTimed();
	}

	public void setHumanForm(UUID playerId, String playerName)
	{
		this.werewolvesConfig.set(playerId.toString() + ".PlayerName", playerName);
		this.werewolvesConfig.set(playerId.toString() + ".WolfState", WolfState.HumanForm.name());

		saveTimed();
	}

	public void setWolfForm(UUID playerId, String playerName)
	{
		this.werewolvesConfig.set(playerId.toString() + ".PlayerName", playerName);
		this.werewolvesConfig.set(playerId.toString() + ".WolfState", WolfState.WolfForm.name());

		saveTimed();
	}

	public void setInfectedWerewolf(UUID playerId, String playerName)
	{
		Date thisDate = new Date();
		String pattern = "HH:mm dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);

		this.werewolvesConfig.set(playerId.toString() + ".PlayerName", playerName);
		this.werewolvesConfig.set(playerId.toString() + ".WolfState", WolfState.Infected.name());
		this.werewolvesConfig.set(playerId.toString() + ".InfectedDate", formatter.format(thisDate));

		saveTimed();
	}

	public void setNotWerewolf(UUID playerId)
	{
		this.werewolvesConfig.set(playerId.toString(), null);

		saveTimed();
	}

	public void howl(Player player)
	{
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 10.0F, 1.0F);
	}

	public void growl(Player player)
	{
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 10.0F, 1.0F);
	}

	public void sendWerewolfUrges(Player player)
	{
		String message = "";

		if (this.random.nextInt(20 + 10 * Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId())) > 0)
		{
			return;
		}

		if (!isWerewolf(player))
		{
			return;
		}

		if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
		{
			message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedThisNight, ChatColor.LIGHT_PURPLE);
		}
		else if (isInfectedWerewolf(player.getUniqueId()))
		{
			if (isInfectedThisNight(player.getUniqueId()))
			{
				message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedThisNight, ChatColor.LIGHT_PURPLE);
			}
			else
			{
				message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedHumanForm, ChatColor.LIGHT_PURPLE);
			}
		}
		else if (this.plugin.isNightInWorld(player.getWorld()) && player.getWorld().getHighestBlockYAt(player.getLocation()) > player.getLocation().getBlockY())
		{
			message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInside, ChatColor.LIGHT_PURPLE);
		}

		if (!message.isEmpty())
		{
			player.sendMessage(message);
		}
	}

	public void setPouncing(UUID playerId)
	{
		this.playersPouncing.put(playerId, Long.valueOf(System.currentTimeMillis()));
	}

	public boolean isPouncing(UUID playerId)
	{
		return this.playersPouncing.containsKey(playerId);
	}

	public void removePouncing(UUID playerId)
	{
		if (!this.playersPouncing.containsKey(playerId))
		{
			return;
		}

		if (this.playersPouncing.get(playerId) < System.currentTimeMillis() - 1000L)
		{
			this.playersPouncing.remove(playerId);
		}
	}

	public void increaseNumberOfPackWolvesForPlayer(UUID playerId)
	{
		int numberOfWolves = 0;
		if (this.packWolves.containsKey(playerId))
		{
			numberOfWolves = ((Integer) this.packWolves.get(playerId)).intValue();
		}
		numberOfWolves++;

		this.packWolves.put(playerId, numberOfWolves);
	}

	public int getNumberOfPackWolvesForPlayer(UUID playerId)
	{
		if (this.packWolves.containsKey(playerId))
		{
			return this.packWolves.get(playerId);
		}

		return 0;
	}

	public void clearNumberOfPackWolvesForPlayer(UUID playerId)
	{
		if (this.packWolves.containsKey(playerId))
		{
			this.packWolves.remove(playerId);
		}
	}

	public void update()
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}

		for (final World world : plugin.getServer().getWorlds())
		{
			if (!lastWorldUpdates.containsKey(world.getUID()))
			{
				lastWorldUpdates.put(world.getUID(), 0L);
			}

			if (System.currentTimeMillis() - this.lastWorldUpdates.get(world.getUID()) < 10000L)
			{
				continue;
			}

			if (this.plugin.isFullMoonDuskInWorld(world))
			{
				this.plugin.logDebug("Is FullMoonDusk in " + world.getName());

				if (!this.lastFullMoonAnnouncementTimes.containsKey(world.getUID()))
				{
					this.lastFullMoonAnnouncementTimes.put(world.getUID(), 0L);
				}

				if (System.currentTimeMillis() - this.lastFullMoonAnnouncementTimes.get(world.getUID()) > 600000L)
				{
					Set<UUID> werewolfPlayers = Werewolf.getWerewolfManager().getOnlineWerewolves(world);
					int numberOfTurningWerewolves = werewolfPlayers.size();

					this.plugin.logDebug("Number Of Turning Werewolves is " + numberOfTurningWerewolves);

					if (numberOfTurningWerewolves > 0)
					{
						Werewolf.getLanguageManager().setAmount("" + numberOfTurningWerewolves);
						Werewolf.getLanguageManager().setPlayerName(world.getName());

						String fullMoonText = ChatColor.BOLD + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.FullMoonIsRising, ChatColor.GOLD);
						String numberOfWerewolvesText = ChatColor.BOLD + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.FullMoonNumberOfWerewolves, ChatColor.GOLD);

						plugin.announcementMessage(world, fullMoonText, Sound.AMBIENT_CAVE, 20);
						plugin.announcementMessage(world, numberOfWerewolvesText, Sound.AMBIENT_CAVE, 140);

						this.lastFullMoonAnnouncementTimes.put(world.getUID(), System.currentTimeMillis());

						for (UUID werewolfPlayer : werewolfPlayers)
						{
							int r = random.nextInt(30);
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new CheckTransformationTask(plugin, werewolfPlayer), 20 * 60 * 4 + r * 20L);
						}
					}
				}
			}

			this.lastWorldUpdates.put(world.getUID(), System.currentTimeMillis());
		}

		if (this.plugin.autoCureDays > 0 && this.random.nextInt(100) == 0 && Werewolf.getWerewolfManager().getAllWerewolves().size() > 0)
		{
			int r = this.random.nextInt(Werewolf.getWerewolfManager().getAllWerewolves().size());
			UUID werewolfPlayerId = (UUID) Werewolf.getWerewolfManager().getAllWerewolves().toArray()[r];

			if (!hasRecentTransformAutoCure(werewolfPlayerId))
			{
				String werewolfPlayerName = plugin.getServer().getOfflinePlayer(werewolfPlayerId).getName();
				unmakeWerewolf(werewolfPlayerId);
				this.plugin.log(werewolfPlayerName + " has not transformed for " + this.plugin.autoCureDays + " days. Removing his werewolf infection.");
			}
		}

		if (this.plugin.getServer().getOnlinePlayers().size() == 0)
		{
			return;
		}

		Player player = (Player) this.plugin.getServer().getOnlinePlayers().toArray()[this.random.nextInt(this.plugin.getServer().getOnlinePlayers().size())];

		if (isWerewolf(player))
		{
			/*
			 * TODO: re-add Vampire integration..
			 * 
			 * if (this.plugin.isVampire(player)) {
			 * this.plugin.log(player.getName() +
			 * " is a Vampire! Removing his Werewolf infection...");
			 * Werewolf.getWerewolfManager().unmakeWerewolf(player.getUniqueId()
			 * ); return; }
			 */
			if (Werewolf.getWerewolfManager().canTransform(player))
			{
				this.plugin.transform(player);
				return;
			}
			else if (Werewolf.getWerewolfManager().canUntransform(player))
			{
				this.plugin.untransform(player);
				return;
			}
			else if (this.plugin.werewolfUrges)
			{
				sendWerewolfUrges(player);
			}
		}

		/*
		 * if (this.random.nextInt(10) == 0) { if
		 * (isValidBiomeForWildWolf(player.getWorld().getBiome(player.
		 * getLocation().getBlockX(), player.getLocation().getBlockZ()))) { int
		 * wolves = getNumberOfPackWolvesForPlayer(player.getUniqueId()); if
		 * (this.random.nextInt(50 * wolves + 50) == 0) { spawnWildWolf(player);
		 * } } }
		 */

		if (this.plugin.autoBounty && this.plugin.vaultEnabled)
		{
			Werewolf.getHuntManager().autoAddBounty();
		}
	}

	/*
	 * private boolean isValidBiomeForWildWolf(Biome biome) { Boolean bool;
	 * switch (biome) { case SNOWY_TAIGA: case SNOWY_TAIGA_HILLS: case TAIGA:
	 * case GIANT_SPRUCE_TAIGA: case FOREST: bool = true; default: bool = false;
	 * } return bool; }
	 * 
	 * private void spawnWildWolf(Player player) { World world =
	 * player.getWorld(); Location center = player.getLocation(); int minDist =
	 * 15; int maxDist = 20; int x; int z;
	 * 
	 * do { x = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockX();
	 * z = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockZ(); }
	 * while ((Math.abs(x - center.getBlockX()) < minDist) || (Math.abs(z -
	 * center.getBlockZ()) < minDist));
	 * 
	 * int y = world.getHighestBlockYAt(x, z);
	 * 
	 * Location spawnLocation = new Location(world, x, y, z);
	 * 
	 * Wolf wolf = (Wolf) world.spawnEntity(spawnLocation, EntityType.WOLF);
	 * 
	 * wolf.setTarget(player); }
	 */
}