package com.dogonfire.werewolf;

import com.google.common.io.Files;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageManager
{
	private Werewolf			plugin;
	private FileConfiguration	languageConfig	= null;
	private Random				random			= new Random();
	private String				amount;
	private String				playerName;
	private String				type;
	private String				authorName;
	private String				languageName;

	private void downloadLanguageFile(String fileName) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/dogonfire/werewolf/master/lang/" + fileName).openStream());

		FileOutputStream fos = new FileOutputStream(this.plugin.getDataFolder() + "/lang/" + fileName);

		BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

		byte[] data = new byte[1024];

		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0)
		{
			bout.write(data, 0, x);
		}
		bout.close();

		in.close();
	}

	private boolean loadLanguageFile(String fileName)
	{
		File languageConfigFile = new File(this.plugin.getDataFolder() + "/lang/" + fileName);
		if (!languageConfigFile.exists())
		{
			return false;
		}
		try
		{
			this.languageConfig = new YamlConfiguration();
			this.languageConfig.loadFromString(Files.toString(languageConfigFile, Charset.forName("UTF-8")));
		}
		catch (Exception e)
		{
			this.plugin.log("Could not load data from " + languageConfigFile + ": " + e.getMessage());
			return false;
		}
		this.languageName = this.languageConfig.getString("Version.Name");
		this.authorName = this.languageConfig.getString("Version.Author");

		this.plugin.logDebug("Loaded " + this.languageConfig.getString("Version.Name") + " by " + this.languageConfig.getString("Version.Author") + " version " + this.languageConfig.getString("Version.Version"));

		return true;
	}

	public void load()
	{
		File directory = new File(this.plugin.getDataFolder() + "/lang");
		if (!directory.exists())
		{
			System.out.println("Creating language file directory '/lang'...");

			boolean result = directory.mkdir();
			if (result)
			{
				this.plugin.logDebug("Language directory created");
			}
			else
			{
				this.plugin.logDebug("Could not create language directory!");
				return;
			}
		}
		String languageFileName = this.plugin.language + ".yml";
		if (!loadLanguageFile(languageFileName))
		{
			this.plugin.log("Could not load " + languageFileName + " from the /lang folder.");
			this.plugin.log("Downloading " + languageFileName + " from DogOnFire...");
			try
			{
				downloadLanguageFile(languageFileName);
			}
			catch (Exception ex)
			{
				this.plugin.log("Could not download " + languageFileName + " language file from DogOnFire: " + ex.getMessage());
				return;
			}
			if (!loadLanguageFile(languageFileName))
			{
				this.plugin.log("Could not load " + languageFileName + "!");
			}
		}
	}
	
	public void save()
	{
		String languageFileName = this.plugin.language + ".yml";
		File languageConfigFile = new File(this.plugin.getDataFolder() + "/lang/" + languageFileName);
		
		if (!languageConfigFile.exists())
		{
			return;
		}
		
		try
		{
			this.languageConfig = new YamlConfiguration();
			this.languageConfig.save(languageConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save language config to " + languageConfigFile + ": " + ex.getMessage());
		}
	}

	public String getLanguageString(LANGUAGESTRING type, ChatColor color)
	{
		List<String> strings = this.languageConfig.getStringList(type.name());
		if (strings.size() == 0)
		{
			this.plugin.log("No language strings found for " + type.name() + "!");
			return type.name() + " MISSING";
		}
		String text = (String) strings.toArray()[this.random.nextInt(strings.size())];

		return parseString(text, color);
	}

	LanguageManager(Werewolf p)
	{
		this.plugin = p;
	}

	public String getAuthor()
	{
		return this.authorName;
	}

	public String getLanguageName()
	{
		return this.languageName;
	}

	public String parseString(String id, ChatColor color)
	{
		String string = color + id.replaceAll("&([0-9a-f])", "§$1");

		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + color);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + color);
		}
		if (string.contains("$Amount"))
		{
			string = string.replace("$Amount", ChatColor.GOLD + this.amount + color);
		}
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", ChatColor.GOLD + this.type + color);
		}
		return string;
	}

	public String parseStringForBook(String id)
	{
		String string = id;
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", this.plugin.serverName);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", this.playerName);
		}
		if (string.contains("$Amount"))
		{
			string = string.replace("$Amount", this.amount);
		}
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", this.type);
		}
		return string;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public void setPlayerName(String name)
	{
		if (name == null)
		{
			this.plugin.logDebug("WARNING: Setting null playername");
		}
		this.playerName = name;
	}

	public String getAmount()
	{
		return this.amount;
	}

	public void setAmount(String amount)
	{
		this.amount = amount;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(String t)
	{
		if (t == null)
		{
			this.plugin.logDebug("WARNING: Setting null type");
		}
		this.type = t;
	}

	public static enum LANGUAGESTRING
	{
		PlayerIsAFullWerewolf,
		PlayerIsAnInfectedWerewolf,
		PlayerIsNotAWerewolf,
		YouTeleportedToClanHome,
		YouCutTheHeadOfWerewolf,
		FullMoonIsRising,
		FullMoonNumberOfWerewolves,
		YouBoughtAInfectionPotion,
		YouBoughtACurePotion,
		YouBoughtAWolfbanePotion,
		YouBoughtASilverSword,
		YouBoughtALoreBook,
		YouDoNotHaveEnoughMoney,
		YouPlacedAInfectionPotionSign,
		YouPlacedACurePotionSign,
		YouPlacedAWolfbanePotionSign,
		YouPlacedASilverSwordSign,
		YouPlacedABookSign,
		YouAreNotTheAlphaOfTheClan,
		YourWerewolfName,
		DenyVampire,
		NoRecentAlphaCall,
		ClanAlphaCannotAnswerCall,
		YouCalledClanMember,
		YouAreBeingCalled,
		NewClanAlpha,
		CannotTransformSoSoon,
		WerewolfCommandControlledTransformation,
		WerewolfCommandGoldImmunity,
		WerewolfCommandNoDropItems,
		WerewolfCommandFullMoonImmunity,
		KilledMobPoints,
		MustExperienceMoreFullMoons,
		NoWerewolfHuntersInWorld,
		TopWerewolfHunters,
		CreatedWerewolfInfectionPotion,
		CreatedWerewolfCurePotion,
		CreatedWerewolfWolfbanePotion,
		CreatedWerewolfSilverSword,
		CreatedWerewolfLoreBook,
		NotAWerewolfAnymore,
		NowAWerewolf,
		InvalidCommand,
		HelpCall,
		HelpWerewolf,
		HelpCheck,
		HelpTop,
		HelpHunt,
		HelpBounty,
		HelpAddBounty,
		HelpInfectSelf,
		HelpInfectOther,
		HelpToggleSelf,
		HelpToggleOther,
		HelpInfectionPotion,
		HelpCurePotion,
		HelpWolfbanePotion,
		HelpSilverSword,
		HelpLoreBook,
		HelpCure,
		HelpTransform,
		HelpClan,
		HelpSetHome,
		HelpHome,
		ClanSetHome,
		MadeWerewolf,
		UnMadeWerewolf,
		Infected,
		PlayerIsHuntingWerewolves,
		PlayerIsNoLongerHuntingWerewolves,
		TakeCompassInHands,
		YouMustHaveYourHandsFree,
		NoPermissionForCommand,
		NoWerewolvesOnline,
		NoWerewolvesInThisWorld,
		WerewolvesCannotHuntWerewolves,
		WerewolfCommandNumberOfWerewolves,
		WerewolfCommandIsWerewolfAlpha,
		WerewolfCommandIsWerewolf,
		WerewolfCommandIsNotWerewolf,
		WerewolfCommandNextFullMoon,
		WerewolfCommandListCommands,
		Transform,
		Untransform,
		BiteVictim,
		BiteAttacker,
		BiteWildWolf,
		KilledMob,
		WerewolfTryEat,
		WerewolfTryDefense,
		DrinkCureSuccess,
		DrinkCureFailure,
		DrinkInfectionSuccess,
		DrinkInfectionFailure,
		PotionCreated,
		UrgesInfectedThisNight,
		UrgesInside,
		UrgesInfectedHumanForm,
		UrgesTransformed,
		InfectionPotionTitle,
		InfectionPotionDescription1,
		InfectionPotionDescription2,
		CurePotionTitle,
		CurePotionDescription1,
		CurePotionDescription2,
		WolfbanePotionTitle,
		WolfbanePotionDescription1,
		WolfbanePotionDescription2,
		SilverSwordTitle,
		SilverSwordDescription1,
		SilverSwordDescription2,
		SilverArmorTitle,
		SilverArmorDescription1,
		SilverArmorDescription2,
		LoreBookTitle,
		BountyPlayerAdded,
		BountyServerAdded,
		BountyTotal,
		KilledWerewolfBounty,
		KilledWerewolfNoBounty,
		TrophyDescription,
		InfoCommandHowl,
		InfoCommandGrowl,
		CurrentBounty,
		Today,
		Tomorrow,
		In2Days,
		In3Days,
		In4Days,
		In5Days,
		In6Days,
		In7Days;
	}
}