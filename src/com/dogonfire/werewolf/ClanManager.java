package com.dogonfire.werewolf;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class ClanManager
{
	private Werewolf	plugin;

	public enum ClanType
	{
		WildBite, WerewolfBite, Potion;
	}

	private long						lastSaveTime		= 0L;
	
	private HashMap<ClanType, String>	werewolfAccount		= new HashMap<ClanType, String>();
	private HashMap<ClanType, UUID>		werewolfAccountId	= new HashMap<ClanType, UUID>();

	private HashMap<ClanType, String>	werewolfTextures	= new HashMap<ClanType, String>();
	private HashMap<ClanType, String>	werewolfTextureSignatures	= new HashMap<ClanType, String>();
	
	private String alphaTexture = "eyJ0aW1lc3RhbXAiOjE0MzQyNzgzMTczMzYsInByb2ZpbGVJZCI6ImUwZDA3NGJkNjcyMjQ3ZmM5NWQzZjI4ZTI4OTllMTU1IiwicHJvZmlsZU5hbWUiOiJXZXJld29sZkFscGhhIiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMDMzODY5NWQwMWY0NTQ2ZmVmZGU0YzllOTVlMTRiNzgzMmExMzg3Mjc3MjQ1ZDI4MmMyOThkNzgxYmFkNTMiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==";
	private String alphaTextureSignature = "KVI3HBmEn027sMbETloJRyyVjG1gf4p9+S1QmF7r3mEr2UPp+vHEjz/9+aJmOXrYfj4Xvj5xAJRJJYGc9Q5sTk+zimxTDUTRHSyk1lAy5R0fmULRspCKy7+Z7gL5MyFLB/Pcc9Jqwax/JrhH1Sj8Buq5fA4xzBQ5R1dY2yjONfuz1CYUf4jUHm+X4iEXBZ2nSKLaum6ZUf3qWSoUdV9cd3kgokN2xocm0fNwSXpOLyaaD55pbdeZJBXXiPipZKab7wQW0loWVSh+G1e931Ex/Zk3Kxeq1IszOCMBC54DYUk8MDHN+UayeiyaV3na2tgGWpWvJQtUqX57uaVKSoyUHLwYEn0D2V4lbzcJ/hDyErtJMQgclmylseB8TAuJNGF+cSVm8u5ug++bYwv2ZSb4lxXtvoqgimV+aStzE/PIwEZSHwD/rTTLd6IxV0Yak1XqasFNt08boymghCgd/JnHqbXJysPXKxQQv36A7do2rMM7fJKKhojCO400mYnfvrog8/mkqk7C+G1cMwSCzHjVLs/dk12meMS+7gVZY+2mgfoN9uYPyUk4TgpIU7XH70KFcQ2BSHPcYrS24s/XmYwrRsH7eudAuuCke60/FX5n8W2L26TXcBqHsuk/ralIh7Xgu5DF1NcPat6C61adeAMuwJfCBAScYpkDVqlqzDrtbEk=";


	private FileConfiguration			clansConfig			= null;
	private File						clansConfigFile		= null;
	private HashMap<ClanType, Double>	totalClanPoints		= new HashMap<ClanType, Double>();
	private HashMap<String, Double>		playerClanPoints	= new HashMap<String, Double>();
	private HashMap<ClanType, String>	clanNames			= new HashMap<ClanType, String>();
	private String						datePattern			= "HH:mm:ss dd-MM-yyyy";
	
	private UUID 						alphaAccountId		= null;
	private String 						alphaAccount		= "";

	ClanManager(Werewolf plugin)
	{
		this.plugin = plugin;
	}

	//Bloodmoon da508eccdbd946c5809547b91aa4ff5f
	//Silvermane b68a8f007d244c52b6ad1423bfbe26ee
	//Witherfang 68c5d9f216384a0f8f56d43fb368ad17
	//https://sessionserver.mojang.com/session/minecraft/profile/039c53aa-5873-4420-a095-9af971321408?unsigned=false
	//https://sessionserver.mojang.com/session/minecraft/profile/b68a8f00-7d24-4c52-b6ad-1423bfbe26ee?unsigned=false
	//https://sessionserver.mojang.com/session/minecraft/profile/da508ecc-dbd9-46c5-8095-47b91aa4ff5f?unsigned=false
	//UUID of player DogOnFire is 54f089fc-19e7-4a3e-9902-b90c5eb1cbed
	public void load()
	{
		if (this.clansConfigFile == null)
		{
			this.clansConfigFile = new File(this.plugin.getDataFolder(), "clans.yml");
		}
		this.clansConfig = YamlConfiguration.loadConfiguration(this.clansConfigFile);

		this.plugin.log("Loaded " + this.clansConfig.getKeys(false).size() + " clans.");
		
		this.alphaAccount = plugin.alphaAccountName;
		this.alphaAccountId = UUID.fromString(plugin.alphaAccountUUID);
		
		this.clanNames.put(ClanType.Potion, plugin.potionName);
		this.clanNames.put(ClanType.WerewolfBite, plugin.werewolfBiteName);
		this.clanNames.put(ClanType.WildBite, plugin.wildBiteName);

		this.werewolfAccount.put(ClanType.Potion, plugin.potionAccountName);
		this.werewolfAccount.put(ClanType.WerewolfBite, plugin.werewolfBiteAccountName);
		this.werewolfAccount.put(ClanType.WildBite, plugin.wildBiteAccountName);

		this.werewolfAccountId.put(ClanType.Potion,       UUID.fromString(plugin.potionAccountUUID));
		this.werewolfAccountId.put(ClanType.WerewolfBite, UUID.fromString(plugin.werewolfBiteAccountUUID));
		this.werewolfAccountId.put(ClanType.WildBite,     UUID.fromString(plugin.wildBiteAccountUUID));
					
		this.werewolfTextures.put(ClanType.Potion,       "eyJ0aW1lc3RhbXAiOjE0MzI5MDc2Nzk1NDgsInByb2ZpbGVJZCI6IjAzOWM1M2FhNTg3MzQ0MjBhMDk1OWFmOTcxMzIxNDA4IiwicHJvZmlsZU5hbWUiOiJ4ZW9uYnVpbGRlciIsImlzUHVibGljIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBiYTQ1YmFmY2MyNTZmNjJjYWYyYzRiNjliNjVjNzEwZjZhZmE2MDIxOTllMmIyYTMyZDlkOTdmMzJlZTAiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==");
		this.werewolfTextures.put(ClanType.WerewolfBite, "eyJ0aW1lc3RhbXAiOjE0MzI5OTYxMjcxNzksInByb2ZpbGVJZCI6ImI2OGE4ZjAwN2QyNDRjNTJiNmFkMTQyM2JmYmUyNmVlIiwicHJvZmlsZU5hbWUiOiJTTV9XZXJld29sZiIsImlzUHVibGljIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE4YmI5OWI2YTg2ZGFkOGM3NmUwNDc1OWNmNmY3MDc1OTYyN2EzMmQ0YTRjMWEzM2ZjZGQyNjZlZGMzN2M4IiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=");
		this.werewolfTextures.put(ClanType.WildBite,     "eyJ0aW1lc3RhbXAiOjE0MzI5MDg3NTY2MjEsInByb2ZpbGVJZCI6ImRhNTA4ZWNjZGJkOTQ2YzU4MDk1NDdiOTFhYTRmZjVmIiwicHJvZmlsZU5hbWUiOiJCTV9XZXJld29sZiIsImlzUHVibGljIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjMwZjI0YzhkMzhkNWQ0OWYzOGExN2YzNmZmNjQ4ZTZiNjgzYTRjZDU2MWIyZTM1NjU3Y2ZhIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=");
						
		this.werewolfTextureSignatures.put(ClanType.Potion,       "vN7JzBCT9Fr2sKQmk0chBLUH4qNHsFBsC09U44HOpSWvH3qFt+3lKPkctUhQuBqrM6/TOPgG9yFB3k/5LA+vW4v+pk94vSYbh/iWMUHfNHN5FSNrwIMvIk9Qr8Bn8QKIHHn2Guq4V6y1xV9eM6GM5c9mUNgdbNs3Mqp80r5Pm8L04pr69/VhB5uKRzDBVI1UrRubq6ibYJKG/cktYkakiejAnv7y8YFzWkUwBKHPcsAkj0GzbmDx1a8vgvogS+CYCqvvbHZD9Tva2/UC81N4jbdiB0Dl1zIT0FAYjkyx6DPPFoTOqV9m6DjowwA7D6cCoS29YfgHB9kUiZnbcOO9qnrCImx998v0a71an1pvmETaH9WF+5SpP0SWDWl9ccQdI9Roc1UNzzg9ueBapzsoYgQ957H2iBuWdOZVGYnhFJ1pcHrwd8c6l2dgKQ1WMABV+W3p5N6KCStAfbz25kPdmnQCY+g7wpT6q/WJ9/a0vliUH8aWqhpNoDq79w5URsTgSoQrzTDa+wvGxWW7w+D56nD2yl86gxn5ELdzTjYRr4K8ThbYUprErvFHDjACSYVW/WveMT9+diKEAcTAFsy0kNM1oUUz1sjTpuUj57UM+AzY6c4cyUak7tiDWai4O3brkRyVCLY6r8nOZXWWw69h0zabDHM+bsJDQ8+52aNbcYQ=");
		this.werewolfTextureSignatures.put(ClanType.WerewolfBite, "rmVoUjl/eeoJbDV2Twh/G8/HQeTcu1my04dnfckjpLzrMhBRW9F8O2diI6x7n9BI0chB7CPa/AmnvkqBxM6RViGD3p9Iw532ddYk9Dk7/jBJ/XAMKNQfsDuyB95lgHfDyuP+DsJXsWQ4CzmUAOahhOjswtT13nKZkGRtyzpmx4GiOtVgQlgnTJyhoRU7YRu6fYAAWPMyAKtx+c8/uflRpbGoJV1BaMYp8skCoB8eoi3ylo3gUy63Zgwcqs68g68P4YNuokcHrMXs0UbjAUVFWza7tcCdKj22Rou/erQPcCbRZKofjECFSb71MyidocyLxpL7MUURJNvh7w8JccLQ+MDUFILsD6j2w8XcGSP9tXeYWUpUjrCN5aeCh7e85/pxs/Dkmn69EUyyghC8P0WE8kZenVb55OQ3XISZ8KvJH42a2NvgJBuHdj9XwmHzT2CIsNVoAmAYCDawGpYbAnfwrXm67V/u8ekxnahB0+y2GhW4FByfT88fE3Kord01gpuQbZ/iS6ruwSHgge513WH6Q4dR14VHUnx+i3ONl2EbNaEIs5+UmVQ/AD/A2v8RufLsSCKWxi4tUzvhfDKNs5VHagZco9wjMyeSbL/rLQBK7u/ehkBgju+ggODok+xMk9Osp+GlmBkXEkWjM8MDhMggXdWIIur6C4UNlOuaJx2FZGU=");
		this.werewolfTextureSignatures.put(ClanType.WildBite,     "IbElGTsg5vIrDi/VDYjHPjdVB/n/Fh1SH45jfR8f2fkgozGkd6wxifIpBkBcgYl0GrsbQvvghO0+Euar4UCbHPa7xOnjqzt/gz2Y/VbotX/tPAJc7B2LoCG6xsF8K9xWCjHnfoM2wmgj1NfFtHjfBGd/LjkSNGyO4WG/Lg6XOzYUznUiX8Q6Bb/h5IFw+pfaO2ACH6r+NovorThvjCZqdZrW9mgQD6nzsd+u2LOAlMBa07nqfThzQDFkyfckLEp4/nGXuuQbp07uA+PqYWt488LEKbTk4ft8v0QT4M4DC0iMzZ+ghS6rCxQsPfKnL9U+eBe655JjEriQ/CbrDjT3zlwFTGmLiRQ8D0YEAKJVNnL3JlhObhNeIt/yvxZx4UNzm6Hfgg3ZyJMczGDaE+UnJn00qGUKpP9UMozMNtnglfRmMI0+s0FCmQit5cc4kKwIRCjgRVwje1MEVkhjc2eGPCU1XBvex42DHq8G6Nw8PCZ1/ESLjmMs41kSMMUkL9rbUigvxMIW9Xe8RaVWFuYt3l5ETmPt/K/0taz09TdcZxEgQo8Hhn5BYiGDi9ztVwG2XPbrfneyjqtS7wmYYn7LFdMh9idLwreGq6UjSBeOH6Ha16KIyMemfUuPZHQaBDAoenuNpJaGt6YKYNLT1x+9YJQ1LeSsBN8Natp3Bxrztf8=");
		
		for (ClanType clan : ClanType.values())
		{
			this.totalClanPoints.put(clan, Double.valueOf(0.0D));
		}
		
		resetClanScores();
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
		if ((this.clansConfig == null) || (this.clansConfigFile == null))
		{
			return;
		}
		
		try
		{
			this.clansConfig.save(this.clansConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.clansConfigFile + ": " + ex.getMessage());
		}
	}

	public String getClanName(UUID playerId)
	{
		return this.clanNames.get(Werewolf.getWerewolfManager().getWerewolfClan(playerId));
	}

	public String getWerewolfAccountForClan(ClanType type)
	{
		return this.werewolfAccount.get(type);
	}

	public UUID getWerewolfAccountIdForClan(ClanType type)
	{
		return this.werewolfAccountId.get(type);
	}

	
	public String getWerewolfTextureForClan(ClanType type)
	{
		return this.werewolfTextures.get(type);
	}
	
	public String getWerewolfTextureSignatureForClan(ClanType type)
	{
		return this.werewolfTextureSignatures.get(type);
	}

	public String getWerewolfAccountForAlpha(ClanType type)
	{
		return this.alphaAccount;
	}

	public UUID getWerewolfAccountIdForAlpha(ClanType type)
	{
		return this.alphaAccountId;
	}

	public String getWerewolfTextureForAlpha(ClanType type)
	{
		return this.alphaTexture;
	}

	public String getWerewolfTextureSignatureForAlpha(ClanType type)
	{
		return this.alphaTextureSignature;
	}
	
	public void handleMobKill(Player player, ClanType clanType, EntityType mobType)
	{
		double points = 0.0D;
		if (this.playerClanPoints.containsKey(player.getName()))
		{
			points = this.playerClanPoints.get(player.getName()).doubleValue();
		}
		
		switch (mobType)
		{
			case PLAYER:
				points = 5.0D;
				break;
			case SKELETON:
				points = 3.0D;
				break;
			case ENDERMAN:
				points = 3.0D;
				break;
			case SHEEP:
				points = 3.0D;
				break;
			case COW:
				points = 3.0D;
				break;
			case SPIDER:
				points = 2.0D;
				break;
			case ZOMBIE:
				points = 2.0D;
				break;
			case RABBIT:
				points = 1.0D;
				break;
			case SILVERFISH:
				points = 1.0D;
				break;
			case CHICKEN:
				points = 1.0D;
				break;
			default:
				break;
		}
				
		if (points > 0)
		{
			this.playerClanPoints.put(player.getName(), Double.valueOf(points));

			double totalPoints = ((Double) this.totalClanPoints.get(clanType)).doubleValue();

			totalPoints += points / Werewolf.getWerewolfManager().getWerewolfClanMembers(clanType).size();

			this.totalClanPoints.put(clanType, Double.valueOf(totalPoints));

			//Werewolf.getLanguageManager().setAmount("" + points);
			//this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledMobPoints, ChatColor.AQUA));
		}
	}

	public List<ClanType> getClansRanked()
	{
		List<ClanType> clanList = new ArrayList<ClanType>();
		for (ClanType clan : ClanType.values())
		{
			clanList.add(clan);
		}
		Collections.sort(clanList, new ClanComparator());

		return clanList;
	}

	public double getClanPoint(ClanType clan)
	{
		return ((Double) this.totalClanPoints.get(clan)).doubleValue();
	}

	public String getClanName(ClanType clan)
	{
		return (String) this.clanNames.get(clan);
	}

	// TODO Bloodrage clan?
	public void updateClans()
	{
		if (!this.plugin.useClans)
		{
			return;
		}
		List<ClanType> clanList = getClansRanked();
		if ((((Double) this.totalClanPoints.get(ClanType.Potion)).doubleValue() > 0.0D) || (((Double) this.totalClanPoints.get(ClanType.WildBite)).doubleValue() > 0.0D) || (((Double) this.totalClanPoints.get(ClanType.WerewolfBite)).doubleValue() > 0.0D))
		{
//			this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "------------ Werewolf Clan Summary ------------");
//			this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "1) " + ChatColor.WHITE + (String) this.clanNames.get(clanList.get(0)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", new Object[] { this.totalClanPoints.get(clanList.get(0)) }) + " points");
//			this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "2) " + ChatColor.WHITE + (String) this.clanNames.get(clanList.get(1)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", new Object[] { this.totalClanPoints.get(clanList.get(1)) }) + " points");
//			this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "3) " + ChatColor.WHITE + (String) this.clanNames.get(clanList.get(2)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", new Object[] { this.totalClanPoints.get(clanList.get(2)) }) + " points");
//			this.plugin.getServer().broadcastMessage("" + ChatColor.GOLD);
//			this.plugin.getServer().broadcastMessage(ChatColor.GOLD + (String) this.clanNames.get(clanList.get(0)) + " now has the " + ChatColor.DARK_RED + " Blood Rage " + ChatColor.DARK_RED + " power!");
//
			setBloodrageClan((ClanType) clanList.get(0));
		}
	}

	private void setBloodrageClan(ClanType clan)
	{
	}

	/*
	public void updateClan(ClanType clanType)
	{
		Set<String> werewolves = Werewolf.getWerewolfManager().getWerewolvesInClan(clanType);

		List<String> werewolfList = new ArrayList();
		for (String werewolf : werewolves)
		{
			werewolfList.add(werewolf);
		}
		
		Collections.sort(werewolfList, new ClanMemberComparator());

		resetClanScores();
	}
*/
	private void resetClanScores()
	{
		this.playerClanPoints.clear();
		for (ClanType clan : ClanType.values())
		{
			double points = ((Double) this.totalClanPoints.get(clan)).doubleValue();

			this.totalClanPoints.put(clan, Double.valueOf(points / 2.0D));
		}
	}

	public void setHomeForClan(ClanType clan, Location location)
	{
		this.clansConfig.set(clan.name() + ".Home.X", Double.valueOf(location.getX()));
		this.clansConfig.set(clan.name() + ".Home.Y", Double.valueOf(location.getY()));
		this.clansConfig.set(clan.name() + ".Home.Z", Double.valueOf(location.getZ()));
		this.clansConfig.set(clan.name() + ".Home.World", location.getWorld().getName());

		saveTimed();
	}

	public Location getHomeForClan(ClanType clan)
	{
		Location location = new Location(null, 0.0D, 0.0D, 0.0D);

		String worldName = this.clansConfig.getString(clan.name() + ".Home.World");
		if (worldName == null)
		{
			return null;
		}
		location.setWorld(this.plugin.getServer().getWorld(worldName));

		location.setX(this.clansConfig.getDouble(clan.name() + ".Home.X"));
		location.setY(this.clansConfig.getDouble(clan.name() + ".Home.Y"));
		location.setZ(this.clansConfig.getDouble(clan.name() + ".Home.Z"));

		return location;
	}
	
	
	public UUID getAlpha(ClanType clan)
	{
		String alphaId = this.clansConfig.getString(clan.name() + ".Alpha");

		try
		{
			return UUID.fromString(alphaId);
		}
		catch(Exception ex)
		{
			UUID alphaPlayerId = Werewolf.getWerewolfManager().getAlphaCandidate(clan);
			
			if (alphaPlayerId != null)
			{
				assignAlphaInClan(clan, alphaPlayerId);
			}						
		}

		return null;
	}

	public boolean isAlpha(UUID playerId)
	{
		for (ClanType clan : ClanType.values())
		{
			String alphaId = this.clansConfig.getString(clan.name() + ".Alpha");

			try
			{
				if(UUID.fromString(alphaId).equals(playerId))
				{
					return true;
				}
			}
			catch(Exception ex)
			{
				
				
			}
		}
		
		return false;
	}

	public boolean isAlpha(ClanType clan, UUID playerId)
	{
		String alphaId = this.clansConfig.getString(clan.name() + ".Alpha");

		try
		{
			return UUID.fromString(alphaId).equals(playerId);			
		}
		catch(Exception ex)
		{
			
			
		}
				
		return false;
	}

	public void assignAlphaInClan(ClanType clan, UUID playerId)
	{
		if(playerId==null)
		{	
			this.clansConfig.set(clan.name() + ".Alpha", null);			
		}
		else
		{
			this.clansConfig.set(clan.name() + ".Alpha", playerId.toString());			
		}		

		saveTimed();
	}

	public boolean hasRecentCall(ClanType clan)
	{
		DateFormat formatter = new SimpleDateFormat(this.datePattern);
		Date thisDate = new Date();
		Date transformDate = null;

		String transformDateString = this.clansConfig.getString(clan.name() + ".LastCallTime");
		try
		{
			transformDate = formatter.parse(transformDateString);
		}
		catch (Exception ex)
		{
			this.plugin.log(clan.name() + " has invalid LastCallTime date. Resetting.");
			transformDate = new Date();
			transformDate.setTime(0L);
		}
		long diff = thisDate.getTime() - transformDate.getTime();
		long diffSeconds = diff / 1000L;

		return diffSeconds < 30L;
	}

	public void setLastCall(ClanType clan)
	{
		Date thisDate = new Date();
		DateFormat formatter = new SimpleDateFormat(this.datePattern);

		this.clansConfig.set(clan.name() + ".LastCallTime", formatter.format(thisDate));

		saveTimed();
	}

	public class ClanMemberComparator implements Comparator<String>
	{
		public ClanMemberComparator()
		{
		}

		public int compare(String member1, String member2)
		{
			return (int) (((Double) ClanManager.this.playerClanPoints.get(member2)).doubleValue() - ((Double) ClanManager.this.playerClanPoints.get(member1)).doubleValue());
		}
	}

	public class ClanComparator implements Comparator<ClanManager.ClanType>
	{
		public int compare(ClanManager.ClanType clan1, ClanManager.ClanType clan2)
		{
			return (int) (((Double) ClanManager.this.totalClanPoints.get(clan2)).doubleValue() - ((Double) ClanManager.this.totalClanPoints.get(clan1)).doubleValue());
		}
	}
}