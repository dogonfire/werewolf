package com.dogonfire.werewolf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import com.dogonfire.werewolf.ClanManager.ClanType;

public class Commands implements TabExecutor
{
	private Werewolf plugin;

	Commands(Werewolf p)
	{
		this.plugin = p;
	}

	public boolean commandInfo(Player player)
	{
		if (player == null)
		{
			this.plugin.log("---------- " + this.plugin.getDescription().getFullName() + " ----------");

			Werewolf.getLanguageManager().setAmount("" + Werewolf.getWerewolfManager().getNumberOfWerewolves());

			this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNumberOfWerewolves, ChatColor.AQUA));

			this.plugin.log("");

			Werewolf.getLanguageManager().setAmount(this.plugin.getNextFullMoonText(plugin.getServer().getWorlds().get(0)));
			this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNextFullMoon, ChatColor.AQUA));

		}
		else
		{
			player.sendMessage(ChatColor.YELLOW + "---------- " + this.plugin.getDescription().getFullName() + " ----------");
			player.sendMessage(ChatColor.AQUA + "By DogOnFire");
			// player.sendMessage(ChatColor.AQUA +
			// Werewolf.getLanguageManager().getLanguageName() + " by " +
			// Werewolf.getLanguageManager().getAuthor());
			player.sendMessage("" + ChatColor.AQUA);

			Werewolf.getLanguageManager().setAmount("" + Werewolf.getWerewolfManager().getNumberOfWerewolves());
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNumberOfWerewolves, ChatColor.AQUA));
			player.sendMessage("");

			if (Werewolf.getWerewolfManager().isWerewolf(player))
			{
				int numberOfTransformations = Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId());

				Werewolf.getLanguageManager().setType(Werewolf.getClanManager().getClanName(player.getUniqueId()));
				Werewolf.getLanguageManager().setAmount("Level " + Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId()));
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsWerewolf, ChatColor.WHITE));
				if (this.plugin.useClans && Werewolf.getClanManager().isAlpha(player.getUniqueId()))
				{
					String clanName = Werewolf.getClanManager().getClanName(Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId())) + "s";
					Werewolf.getLanguageManager().setType(clanName);
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsWerewolfAlpha, ChatColor.WHITE));
				}

				player.sendMessage("");

				Werewolf.getLanguageManager().setPlayerName(Werewolf.getWerewolfManager().getWerewolfName(player.getUniqueId()));
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YourWerewolfName, ChatColor.AQUA));

				player.sendMessage("");

				if (numberOfTransformations >= this.plugin.transformsForNoDropItems)
				{
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNoDropItems, ChatColor.WHITE));
				}

				if (numberOfTransformations >= this.plugin.transformsForControlledTransformation)
				{
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandControlledTransformation, ChatColor.WHITE));
				}

				if (numberOfTransformations >= this.plugin.transformsForGoldImmunity)
				{
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandGoldImmunity, ChatColor.WHITE));
				}
			}
			else
			{
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf, ChatColor.RED));
			}
			player.sendMessage("");

			Werewolf.getLanguageManager().setAmount(this.plugin.getNextFullMoonText(player.getWorld()));
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNextFullMoon, ChatColor.AQUA));

			player.sendMessage("");

			if (Werewolf.getHuntManager() != null && Werewolf.getHuntManager().getBounty() > 0)
			{
				Werewolf.getLanguageManager().setAmount("" + Werewolf.getHuntManager().getBounty());
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CurrentBounty, ChatColor.AQUA));

				player.sendMessage("");
			}

			if (plugin.useClans && Werewolf.getWerewolfManager().isWerewolf(player))
			{
				Werewolf.getLanguageManager().setType("/ww clan");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpClan, ChatColor.AQUA));
			}

			Werewolf.getLanguageManager().setType("/ww help");
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandListCommands, ChatColor.AQUA));

			this.plugin.log(player.getName() + ": /werewolf");
		}
		return true;
	}

	public void commandReload(Player player)
	{
		if (player == null)
		{
			this.plugin.reloadSettings();
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Reloaded configuration.");
		}
		else if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.reload")))
		{
			this.plugin.reloadSettings();
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		}
	}

	public void commandSave(Player player)
	{
		if (player == null)
		{
			Werewolf.getClanManager().save();
			Werewolf.getWerewolfManager().save();
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Saved configuration(s).");
		}
		else if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.save")))
		{
			Werewolf.getClanManager().save();
			Werewolf.getWerewolfManager().save();
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Saved configuration(s).");
		}
	}

	public boolean commandHelp(Player player)
	{
		if (player == null)
		{
			this.plugin.log("/werewolf - Show basic info");
			this.plugin.log("/howl - Howl as a Werewolf!");
			this.plugin.log("/growl - Growl as a Werewolf!");
			this.plugin.log("/werewolf top - View the top Werewolf hunters in " + this.plugin.serverName);
			this.plugin.log("/werewolf hunt - Toggles Werewolf hunt mode");
			this.plugin.log("/werewolf check <playername> - Check Werewolf status for a player");
			this.plugin.log("/werewolf bounty - Check the current bounty for killing a Werewolf");
			this.plugin.log("/werewolf addbounty - Add to the bounty for killing a Werewolf");
			this.plugin.log("/werewolf toggle - Toggles Werewolf status for yourself");
			this.plugin.log("/werewolf toggle <playername> - Toggles Werewolf status for another player");
			this.plugin.log("/werewolf infect <playername> - Infects a player with the Werewolf infection");
		}
		else
		{
			player.sendMessage(ChatColor.YELLOW + "---------- " + this.plugin.getDescription().getFullName() + " ----------");
			Werewolf.getLanguageManager().setType("/werewolf help");
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpWerewolf, ChatColor.AQUA));
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.hunt"))
			{
				Werewolf.getLanguageManager().setType("/ww hunt");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpHunt, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top")))
			{
				Werewolf.getLanguageManager().setType("/ww top");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpTop, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.bounty")))
			{
				Werewolf.getLanguageManager().setType("/ww bounty");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpBounty, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.addbounty")))
			{
				Werewolf.getLanguageManager().setType("/ww addbounty <amount>");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpAddBounty, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.transform")))
			{
				Werewolf.getLanguageManager().setType("/ww transform");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpTransform, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.check")))
			{
				Werewolf.getLanguageManager().setType("/ww check <playername>");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCheck, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectself")))
			{
				Werewolf.getLanguageManager().setType("/ww infect");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpInfectSelf, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infect")))
			{
				Werewolf.getLanguageManager().setType("/ww infect <playername>");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpInfectOther, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolf")))
			{
				Werewolf.getLanguageManager().setType("/ww toggle <playername>");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpToggleOther, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolfself")))
			{
				Werewolf.getLanguageManager().setType("/ww toggle");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpToggleSelf, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.infection.create")))
			{
				Werewolf.getLanguageManager().setType("/ww infectionpotion");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpInfectionPotion, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.cure.create")))
			{
				Werewolf.getLanguageManager().setType("/ww curepotion");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCurePotion, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.wolfbane.create")))
			{
				Werewolf.getLanguageManager().setType("/ww wolfbane");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpWolfbanePotion, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.silversword.create")))
			{
				Werewolf.getLanguageManager().setType("/ww silversword");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpSilverSword, ChatColor.AQUA));
			}
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.lorebook.create")))
			{
				Werewolf.getLanguageManager().setType("/ww lorebook");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpLoreBook, ChatColor.AQUA));
			}
			if (this.plugin.useClans)
			{
				if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.clan")))
				{
					Werewolf.getLanguageManager().setType("/ww clan");
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpClan, ChatColor.AQUA));
				}
				if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.home")))
				{
					Werewolf.getLanguageManager().setType("/ww home");
					player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpHome, ChatColor.AQUA));
				}

				if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
				{
					if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.call")))
					{
						Werewolf.getLanguageManager().setType("/ww call");
						player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCall, ChatColor.AQUA));
					}
					if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sethome")))
					{
						Werewolf.getLanguageManager().setType("/ww sethome");
						player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpSetHome, ChatColor.AQUA));
					}
				}
			}
			this.plugin.log(player.getName() + ": /werewolf help");
		}
		return true;
	}

	private boolean commandList(CommandSender sender)
	{
		if (sender == null)
		{
			return false;
		}
		if ((!sender.isOp()) && (!Werewolf.getPermissionsManager().hasPermission((Player) sender, "werewolf.list")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWerewolf((Player) sender))
		{
			sender.sendMessage(ChatColor.RED + "You are not a werewolf");
			return false;
		}

		Player player = (Player) sender;

		ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());

		List<UUID> list = Werewolf.getWerewolfManager().getWerewolfClanMembersRanked(clan);

		sender.sendMessage(ChatColor.GOLD + "--------- The Members of " + Werewolf.getClanManager().getClanName(clan) + " ---------");
		if (list.size() > 15)
		{
			list = list.subList(0, 15);
		}

		int n = 1;

		boolean playerShown = false;

		for (UUID memberId : list)
		{
			String memberName = plugin.getServer().getOfflinePlayer(memberId).getName();

			int numberOfTransformations = Werewolf.getWerewolfManager().getNumberOfTransformations(memberId);
			if (memberName.equals(sender.getName()))
			{
				playerShown = true;
				sender.sendMessage(ChatColor.GOLD +

						String.format("%2d", new Object[] { Integer.valueOf(n) }) + " - " +

						memberName + ChatColor.GOLD + StringUtils.rightPad(new StringBuilder().append("      level ").append(numberOfTransformations).toString(), 2));
			}
			else
			{
				sender.sendMessage(ChatColor.YELLOW + String.format("%2d", new Object[] { Integer.valueOf(n) }) + ChatColor.AQUA + " - " + memberName + ChatColor.GOLD + StringUtils.rightPad(new StringBuilder().append("      level ").append(numberOfTransformations).toString(), 2));
			}
			n++;
		}

		n = 1;

		if (!playerShown)
		{
			for (UUID memberId : list)
			{
				String memberName = plugin.getServer().getOfflinePlayer(memberId).getName();

				int numberOfTransformations = Werewolf.getWerewolfManager().getNumberOfTransformations(memberId);

				if (memberName.equals(sender.getName()))
				{
					playerShown = true;
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + memberName + StringUtils.rightPad(new StringBuilder().append("      level ").append(numberOfTransformations).toString(), 2));
				}
				n++;
			}
		}

		this.plugin.sendInfo(player, ChatColor.AQUA + "Use " + ChatColor.GOLD + "/ww clan" + ChatColor.AQUA + " to see info about your clan");

		return true;
	}

	private void commandTopHunters(Player player)
	{
		if (!this.plugin.vaultEnabled)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");

		}
		else if ((player == null) || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top")) || player.isOp())
		{
			List<Hunter> hunters = new ArrayList<Hunter>();

			Set<String> list = Werewolf.getHuntManager().getHunters();
			for (String hunterName : list)
			{
				int kills = Werewolf.getHuntManager().getHunterKills(hunterName);
				if (kills > 0)
				{
					hunters.add(new Hunter(hunterName, kills));
				}
			}

			if (hunters.size() == 0)
			{
				if (player != null)
				{
					this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolfHuntersInWorld, ChatColor.RED));
				}
				else
				{
					this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolfHuntersInWorld, ChatColor.WHITE));
				}
				return;
			}

			if (player != null)
			{
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TopWerewolfHunters, ChatColor.RED));
			}
			else
			{
				this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TopWerewolfHunters, ChatColor.WHITE));
			}

			Collections.sort(hunters, new TopHuntersComparator());

			int l = hunters.size();
			if (l > 10)
			{
				hunters = hunters.subList(0, 10);
			}
			int n = 1;
			for (Hunter hunter : hunters)
			{
				String message = "" + n + ChatColor.AQUA + " - " + StringUtils.rightPad(hunter.name, 15) + StringUtils.rightPad(new StringBuilder().append(hunter.kills).append(" Kills").toString(), 3);

				this.plugin.sendInfo(player, ChatColor.YELLOW + message);
				// plugin.sendInfo(player.getUniqueId(),
				// LanguageManager.LANGUAGESTRING., ChatColor.GREEN, "",
				// (int)this.plugin.infectionPrice, 1);

				n++;
			}
			if (player != null)
			{
				this.plugin.log(player.getName() + ": /werewolf top");
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}
	}

	// The command that is called by a player when the player wants to transform
	// into a Werewolf manually.
	public boolean commandTransform(Player player)
	{
		if (player == null)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.transform")) && !player.isOp())
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWerewolf(player))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf, ChatColor.RED));
			return false;
		}
		if (Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId()) < this.plugin.transformsForControlledTransformation)
		{
			int fullMoons = this.plugin.transformsForControlledTransformation - Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId());
			Werewolf.getLanguageManager().setAmount("" + fullMoons);
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.MustExperienceMoreFullMoons, ChatColor.RED));
			return false;
		}
		if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
		{
			this.plugin.untransform(player);
		}
		else
		{
			if (Werewolf.getWerewolfManager().hasRecentTransform(player.getUniqueId()))
			{
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CannotTransformSoSoon, ChatColor.RED));
				return false;
			}
			this.plugin.transform(player);
		}
		return true;
	}

	// The command that is runned by a Werewolf to get info about their current
	// clan and the other clans
	public boolean commandClan(Player player)
	{
		if (!this.plugin.useClans)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Clans are not enabled.");
			return false;
		}
		if (player == null)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
			return false;
		}
		if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.clan") && !player.isOp())
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWerewolf(player))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf, ChatColor.RED));
			return false;
		}

		String clanName = Werewolf.getClanManager().getClanName(player.getUniqueId());
		String alphaName = "None!";
		ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());
		UUID alphaId = Werewolf.getClanManager().getAlpha(playerClan);

		if (alphaId != null)
		{
			alphaName = plugin.getServer().getOfflinePlayer(alphaId).getName();
		}

		this.plugin.sendInfo(player, ChatColor.GOLD + "------------ The " + clanName + " Clan ------------");

		this.plugin.sendInfo(player, "" + ChatColor.AQUA);
		this.plugin.sendInfo(player, "" + ChatColor.AQUA + " Clan Alpha : " + ChatColor.WHITE + alphaName);
		this.plugin.sendInfo(player, "" + ChatColor.AQUA);

		if (alphaId != null && !player.getUniqueId().equals(alphaId))
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Kill " + ChatColor.GOLD + alphaName + ChatColor.RED + " to take over the position as alpha!");
			this.plugin.sendInfo(player, "" + ChatColor.AQUA);
		}

		this.plugin.sendInfo(player, ChatColor.GOLD + "------------ The Werewolf Clans ------------");

		List<ClanManager.ClanType> clanList = Werewolf.getClanManager().getClansRanked();

		int n = 1;
		for (ClanManager.ClanType clan : clanList)
		{
			if (clan.equals(playerClan))
			{
				this.plugin.sendInfo(player, "" + ChatColor.GOLD + n + ") " + ChatColor.GOLD + Werewolf.getClanManager().getClanName(clan) + ChatColor.AQUA + "  -  " + Werewolf.getWerewolfManager().getWerewolfClanMembers(clan).size() + " members  -  "
						+ String.format("%1$,.2f", new Object[] { Double.valueOf(Werewolf.getClanManager().getClanPoint(clan)) }) + " clan points");
			}
			else
			{
				this.plugin.sendInfo(player, "" + ChatColor.GOLD + n + ") " + ChatColor.WHITE + Werewolf.getClanManager().getClanName(clan) + ChatColor.AQUA + "  -  " + Werewolf.getWerewolfManager().getWerewolfClanMembers(clan).size() + " members  -  "
						+ String.format("%1$,.2f", new Object[] { Double.valueOf(Werewolf.getClanManager().getClanPoint(clan)) }) + " clan points");
			}
			n++;
		}

		this.plugin.sendInfo(player, "" + ChatColor.GOLD);

		if (Werewolf.getClanManager().isAlpha(playerClan, player.getUniqueId()))
		{
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sethome"))
			{
				Werewolf.getLanguageManager().setType("/ww sethome");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpSetHome, ChatColor.AQUA));
			}
		}
		else
		{
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.home"))
			{
				Werewolf.getLanguageManager().setType("/ww home");
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpHome, ChatColor.AQUA));
			}
		}

		this.plugin.sendInfo(player, ChatColor.AQUA + "Use " + ChatColor.GOLD + "/ww list" + ChatColor.AQUA + " to see the list of members in your clan");

		// this.plugin.sendInfo(player, "" + ChatColor.GOLD +
		// Werewolf.getClanManager().getClanName((ClanManager.ClanType)
		// clanList.get(0)) + ChatColor.DARK_RED + " has the Blood Rage!");

		return true;
	}

	// The command that is run by the clan alpha to set the home for the clan
	private boolean commandSetHome(Player player)
	{
		if (!this.plugin.useClans)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Clans are not enabled.");
			return false;
		}
		if (player == null)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
			return false;
		}

		if (!player.isOp() && !Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sethome"))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWerewolf(player))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf, ChatColor.RED));
			return false;
		}

		ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());

		if (!Werewolf.getClanManager().isAlpha(clan, player.getUniqueId()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouAreNotTheAlphaOfTheClan, ChatColor.RED));
			return false;
		}

		Werewolf.getClanManager().setHomeForClan(clan, player.getLocation());

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.ClanSetHome, ChatColor.GREEN));

		return true;
	}

	// The command that is run by werewolves to get to their clan home
	private boolean commandHome(Player player)
	{
		if (!player.isOp() && !Werewolf.getPermissionsManager().hasPermission(player, "werewolf.home"))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}

		if (!Werewolf.getWerewolfManager().isWerewolf(player))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf, ChatColor.RED));
			return false;
		}

		ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());

		Location location = Werewolf.getClanManager().getHomeForClan(playerClan);
		if (location == null)
		{
			return false;
		}

		player.teleport(location);

		Werewolf.getLanguageManager().setType(Werewolf.getClanManager().getClanName(playerClan));
		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouTeleportedToClanHome, ChatColor.GREEN));

		return true;
	}

	// The command that is run manually to toggle the player who ran the command
	// to/from a werewolf
	public boolean commandToggleSelfWerewolf(Player player, String[] args)
	{
		if (player == null)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
			return false;
		}

		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolfself")) && !player.isOp())
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}

		if (!Werewolf.getWerewolfManager().isWerewolf(player))
		{
			Random random = new Random();

			Werewolf.getWerewolfManager().makeWerewolf(player, true, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);

			Werewolf.getWerewolfManager().setInfectedThisNight(player.getUniqueId(), player.getName(), false);

			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NowAWerewolf, ChatColor.AQUA));
		}
		else
		{
			Werewolf.getWerewolfManager().unmakeWerewolf(player.getUniqueId());
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NotAWerewolfAnymore, ChatColor.AQUA));
		}

		if (player != null)
		{
			this.plugin.log(player.getName() + ": /werewolf toggleself");
		}
		return true;
	}

	// The command that is run manually to toggle if a player is a werewolf
	public boolean commandTogglePlayerWerewolf(Player player, String[] args)
	{
		if ((player == null) || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolf")) || player.isOp())
		{
			try
			{
				Player newWerewolf = this.plugin.getServer().getPlayer(args[1]);

				if (newWerewolf == null)
				{
					throw new Exception();
				}

				if (!Werewolf.getWerewolfManager().isWerewolf(newWerewolf))
				{
					Random random = new Random();
					Werewolf.getWerewolfManager().makeWerewolf(newWerewolf, true, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);
					Werewolf.getWerewolfManager().setInfectedThisNight(newWerewolf.getUniqueId(), newWerewolf.getName(), false);
					if (player != null)
					{
						player.sendMessage(ChatColor.AQUA + newWerewolf.getName() + " is now a werewolf! ");

						Werewolf.getLanguageManager().setPlayerName(player.getName());
					}
					else
					{
						this.plugin.log(newWerewolf.getName() + " is now a werewolf! ");

						Werewolf.getLanguageManager().setPlayerName("CONSOLE");
					}

					newWerewolf.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.MadeWerewolf, ChatColor.AQUA));
				}
				else
				{
					Werewolf.getWerewolfManager().unmakeWerewolf(newWerewolf.getUniqueId());
					if (player != null)
					{
						player.sendMessage(ChatColor.AQUA + newWerewolf.getName() + " is no longer a werewolf ...");
						Werewolf.getLanguageManager().setPlayerName(player.getName());
					}
					else
					{
						this.plugin.log(newWerewolf.getName() + " is no longer a werewolf ...");
						Werewolf.getLanguageManager().setPlayerName("CONSOLE");
					}
					newWerewolf.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UnMadeWerewolf, ChatColor.AQUA));
				}

				if (player == null)
				{
					return true;
				}

				this.plugin.log(player.getName() + ": /werewolf toggle " + newWerewolf.getName());
			}
			catch (Exception e)
			{
				this.plugin.sendInfo(player, ChatColor.RED + "Invalid player name...");
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}

		return true;
	}

	// Command to infect a player with the werewolf infection
	public boolean commandInfect(Player player, String[] args)
	{
		if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infect") || player.isOp())
		{
			try
			{
				Player newInfection = this.plugin.getServer().getPlayer(args[1]);
				if (newInfection == null)
				{
					throw new Exception();
				}

				if (!Werewolf.getWerewolfManager().isWerewolf(newInfection))
				{
					Random random = new Random();
					Werewolf.getWerewolfManager().makeWerewolf(newInfection, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);

					this.plugin.sendInfo(player, ChatColor.GREEN + newInfection.getName() + " now has the werewolf infection!");
					if (player == null)
					{
						Werewolf.getLanguageManager().setPlayerName("CONSOLE");
					}
					else
					{
						Werewolf.getLanguageManager().setPlayerName(player.getName());
					}
					newInfection.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Infected, ChatColor.AQUA));
				}
				else
				{
					this.plugin.sendInfo(player, ChatColor.RED + newInfection.getName() + " already is a werewolf! Please toggle his status first...");
				}

				this.plugin.log(player.getName() + ": /werewolf infect " + newInfection.getName());
			}
			catch (Exception e)
			{
				this.plugin.sendInfo(player, ChatColor.RED + "Invalid player name...");
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}
		return true;
	}

	// Command to infect yourself with the werewolf infection
	public boolean commandInfectSelf(Player player, String[] args)
	{
		if (player == null)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
			return false;
		}
		if ((Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectself")) || player.isOp())
		{
			if (!Werewolf.getWerewolfManager().isWerewolf(player))
			{
				Random random = new Random();
				if (Werewolf.getWerewolfManager().makeWerewolf(player, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]))
				{
					player.sendMessage(ChatColor.AQUA + "You now have the werewolf infection!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Could not make you a werewolf!");
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You are already a werewolf! Please toggle your status first...");
			}
			this.plugin.log(player.getName() + ": /werewolf infect");
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}
		return true;
	}

	// Command to check if a player is a werewolf
	public boolean commandCheck(Player player, String[] args)
	{
		if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.check") || player.isOp())
		{
			try
			{
				Player checkPlayer = this.plugin.getServer().getPlayer(args[1]);

				if (checkPlayer != null)
				{
					if (Werewolf.getWerewolfManager().isFullWerewolf(checkPlayer.getUniqueId()))
					{
						if (player != null)
						{
							int level = Werewolf.getWerewolfManager().getNumberOfTransformations(player.getUniqueId());

							Werewolf.getLanguageManager().setPlayerName(player.getName());
							Werewolf.getLanguageManager().setType(String.valueOf(level));
							this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsAFullWerewolf, ChatColor.AQUA));
							// player.sendMessage(ChatColor.AQUA +
							// checkPlayer.getName() + " is a full werewolf");
						}
						else
						{
							this.plugin.log(checkPlayer.getName() + " is a full werewolf");
						}
					}
					else if (Werewolf.getWerewolfManager().isInfectedWerewolf(checkPlayer.getUniqueId()))
					{
						if (player != null)
						{
							Werewolf.getLanguageManager().setPlayerName(player.getName());
							this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsAnInfectedWerewolf, ChatColor.AQUA));
							// player.sendMessage(ChatColor.AQUA +
							// checkPlayer.getName() + " is a infected
							// werewolf");
						}
						else
						{
							this.plugin.log(checkPlayer.getName() + " is a infected werewolf");
						}
					}
					else if (player != null)
					{
						Werewolf.getLanguageManager().setPlayerName(player.getName());
						this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsNotAWerewolf, ChatColor.AQUA));
						// player.sendMessage(ChatColor.AQUA +
						// checkPlayer.getName() + " is not a werewolf");
					}
					else
					{
						this.plugin.log(checkPlayer.getName() + " is not a werewolf");
					}

					if (player != null)
					{
						this.plugin.log(player.getName() + ": /werewolf check " + checkPlayer.getName());
					}
				}
			}
			catch (Exception ex)
			{
				this.plugin.log("'" + player.getName() + "' made a command error");
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}
		return true;
	}

	// Command to check werewolf bounties
	public boolean commandBounty(Player player)
	{
		if (!this.plugin.vaultEnabled)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
		}
		else if ((player == null) || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.bounty")) || player.isOp())
		{
			Werewolf.getLanguageManager().setAmount(Werewolf.getHuntManager().getFormattedBounty());
			if (player == null)
			{
				this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal, ChatColor.WHITE));
			}
			else
			{
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal, ChatColor.AQUA));
			}
			if (player != null)
			{
				this.plugin.log(player.getName() + ": /werewolf bounty");
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
		}
		return true;
	}

	// Command to add bounties to a werewolf
	public boolean commandAddBounty(Player player, String[] args)
	{
		if (!this.plugin.vaultEnabled)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
		}
		else if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.addbounty") || player.isOp())
		{
			try
			{
				if (player == null)
				{
					return true;
				}

				if (Werewolf.getWerewolfManager().isWerewolf(player))
				{
					this.plugin.sendInfo(player, ChatColor.RED + "Werewolves cannot add to the Werewolf bounty!");
					return false;
				}
				else
				{
					int bounty = Integer.parseInt(args[1]);

					if (bounty <= 0)
					{
						this.plugin.sendInfo(player, ChatColor.RED + "How about adding a real amount?");

						return false;
					}

					Werewolf.getHuntManager().addBounty(player.getName(), bounty);

					this.plugin.log(player.getName() + ": /werewolf addbounty " + bounty);

					return true;
				}
			}
			catch (NumberFormatException ex)
			{
				this.plugin.sendInfo(player, ChatColor.RED + "Come on, that is not a valid bounty :/");

				return false;
			}
		}
		else
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));

			return false;
		}

		return false;
	}

	// Command to hunt a werewolf
	public boolean commandHuntWerewolf(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if (!this.plugin.vaultEnabled)
		{
			this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
			return false;
		}

		if (!this.plugin.isWerewolvesAllowedInWorld(player))
		{
			Werewolf.getLanguageManager().setType(player.getWorld().getName());
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolvesInThisWorld, ChatColor.RED));
			return false;
		}

		if ((player != null) && (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.hunt")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}

		if (Werewolf.getWerewolfManager().isWerewolf(player))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolvesCannotHuntWerewolves, ChatColor.RED));
			return false;
		}

		if (!Werewolf.getHuntManager().isHunting(player.getUniqueId()))
		{
			if (Werewolf.getWerewolfManager().getOnlineWerewolvesInWolfForm(player.getWorld()).size() == 0)
			{
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolvesOnline, ChatColor.RED));
				return false;
			}

			if (player.getInventory().getItemInMainHand().getType() != Material.AIR)
			{
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
				return false;
			}

			player.getInventory().setItemInMainHand(new ItemStack(Material.COMPASS));

			Werewolf.getLanguageManager().setPlayerName(player.getName());
			this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsHuntingWerewolves, ChatColor.GOLD));

			Werewolf.getHuntManager().setHunting(player.getUniqueId(), true);
		}
		else
		{
			if (player.getInventory().getItemInMainHand().getType() != Material.COMPASS)
			{
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TakeCompassInHands, ChatColor.RED));
				return false;
			}
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			Werewolf.getLanguageManager().setPlayerName(player.getName());
			this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsNoLongerHuntingWerewolves, ChatColor.GOLD));
			Werewolf.getHuntManager().setHunting(player.getUniqueId(), false);
		}
		if (player != null)
		{
			this.plugin.log(player.getName() + ": /werewolf hunt");
		}
		return true;
	}

	// Command for a werewolf to growl
	public boolean commandGrowl(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.growl")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWolfForm(player.getUniqueId()))
		{
			this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
			return false;
		}
		Werewolf.getWerewolfManager().growl(player);

		return true;
	}

	// Command for a werewolf to howl
	public boolean commandHowl(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.howl")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWolfForm(player.getUniqueId()))
		{
			this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
			return false;
		}

		Werewolf.getWerewolfManager().howl(player);

		if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
		{
			for (Entity entity : player.getNearbyEntities(17.5D, 17.5D, 17.5D))
			{
				if ((entity instanceof LivingEntity))
				{
					LivingEntity livingEntity = (LivingEntity) entity;

					livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 3));
				}
			}
		}
		return true;
	}

	// Command for an alpha to call a werewolf to him
	public boolean commandCall(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.call")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWolfForm(player.getUniqueId()))
		{
			this.plugin.sendInfo(player, "You are not in wolf form");
			return false;
		}
		ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());
		if (!Werewolf.getClanManager().isAlpha(playerClan, player.getUniqueId()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouAreNotTheAlphaOfTheClan, ChatColor.RED));
			return false;
		}
		Werewolf.getClanManager().setLastCall(playerClan);

		Werewolf.getWerewolfManager().howl(player);
		for (String playerName : Werewolf.getWerewolfManager().getWerewolfClanMembers(playerClan))
		{
			Player clanPlayer = this.plugin.getServer().getPlayer(playerName);
			if (clanPlayer != null)
			{
				Werewolf.getLanguageManager().setPlayerName(player.getName());
				this.plugin.sendInfo(clanPlayer, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouAreBeingCalled, ChatColor.RED));

				Werewolf.getLanguageManager().setPlayerName(clanPlayer.getName());
				this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouCalledClanMember, ChatColor.RED));
			}
		}
		return true;
	}

	// Command to accept the call
	public boolean commandAcceptCall(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.acceptcall")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if (!Werewolf.getWerewolfManager().isWolfForm(player.getUniqueId()))
		{
			this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
			return false;
		}
		if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.ClanAlphaCannotAnswerCall, ChatColor.RED));
			return false;
		}
		ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());
		if (!Werewolf.getClanManager().hasRecentCall(playerClan))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoRecentAlphaCall, ChatColor.RED));
			return false;
		}

		UUID alphaId = Werewolf.getClanManager().getAlpha(playerClan);
		Player alphaPlayer = this.plugin.getServer().getPlayer(alphaId);

		player.teleport(alphaPlayer);

		return true;
	}

	// Command to get an infection potion
	public boolean commandInfectionPotion(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.infection.create")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if ((player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
			return false;
		}

		ItemStack potionItem = Werewolf.getItemManager().newInfectionPotion();

		player.getInventory().setItemInMainHand(potionItem);

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfInfectionPotion, ChatColor.GREEN));

		return true;
	}

	// Command to get a cure potion
	public boolean commandCurePotion(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.cure.create")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if ((player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
			return false;
		}

		ItemStack potionItem = Werewolf.getItemManager().newCurePotion();

		player.getInventory().setItemInMainHand(potionItem);

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfCurePotion, ChatColor.GREEN));

		return true;
	}

	// Command to get the wolfbane potion
	public boolean commandWolfbanePotion(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.wolfbane.create")) && (!player.isOp()))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}
		if ((player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
			return false;
		}

		ItemStack potionItem = Werewolf.getItemManager().newWolfbanePotion();

		player.getInventory().setItemInMainHand(potionItem);

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfWolfbanePotion, ChatColor.GREEN));

		return true;
	}

	// Command to get a silver sword
	public boolean commandSilverSword(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}

		if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.silversword.create") && !player.isOp())
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}

		if ((player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
			return false;
		}

		ItemStack swordItem = Werewolf.getItemManager().newSilverSword(1);

		player.getInventory().setItemInMainHand(swordItem);

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfSilverSword, ChatColor.GREEN));

		return true;
	}

	public boolean commandLoreBook(Player player)
	{
		if (player == null)
		{
			this.plugin.log("You cannot use this command from the console.");
			return false;
		}

		if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.lorebook.create") && !player.isOp())
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
			return false;
		}

		if ((player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
		{
			this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree, ChatColor.RED));
			return false;
		}

		ItemStack bookItem = Werewolf.getItemManager().newLoreBook();

		player.getInventory().setItemInMainHand(bookItem);

		this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfLoreBook, ChatColor.GREEN));

		return true;
	}

	// What happens when a command is run?
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;

			// Is Werewolf compatible with this version of the server?
			if (!Werewolf.isCompatible())
			{
				player.sendMessage(ChatColor.RED + "Your server version is incompatible with this version of the Werewolf plugin");
				player.sendMessage(ChatColor.RED + "This version of the Werewolf plugin is compatible with " + ChatColor.GOLD + Werewolf.MIN + ChatColor.RED + " to " + ChatColor.GOLD + Werewolf.MAX);
				return false;
			}
		}

		if (label.equalsIgnoreCase("growl"))
		{
			commandGrowl(player);
		}
		if (label.equalsIgnoreCase("howl"))
		{
			commandHowl(player);
		}

		if ((label.equalsIgnoreCase("werewolf")) || (label.equalsIgnoreCase("ww")))
		{
			if (args.length == 0)
			{
				commandInfo(player);
				return true;
			}
			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("toggle"))
				{
					commandTogglePlayerWerewolf(player, args);
				}
				else if (args[0].equalsIgnoreCase("infect"))
				{
					commandInfect(player, args);
				}
				else if (args[0].equalsIgnoreCase("addbounty"))
				{
					commandAddBounty(player, args);
				}
				else if (args[0].equalsIgnoreCase("check"))
				{
					commandCheck(player, args);
				}
				else
				{
					this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InvalidCommand, ChatColor.RED));
				}
				return true;
			}

			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					commandReload(player);
				}
				else if (args[0].equalsIgnoreCase("save"))
				{
					commandSave(player);
				}
				else if (args[0].equalsIgnoreCase("help"))
				{
					commandHelp(player);
				}
				else if (args[0].equalsIgnoreCase("clan"))
				{
					commandClan(player);
				}
				else if (args[0].equalsIgnoreCase("list"))
				{
					commandList(player);
				}
				else if (args[0].equalsIgnoreCase("transform"))
				{
					commandTransform(player);
				}
				else if (args[0].equalsIgnoreCase("toggle"))
				{
					commandToggleSelfWerewolf(player, args);
				}
				else if (args[0].equalsIgnoreCase("infect"))
				{
					commandInfectSelf(player, args);
				}
				else if (args[0].equalsIgnoreCase("bounty"))
				{
					commandBounty(player);
				}
				else if (args[0].equalsIgnoreCase("home"))
				{
					commandHome(player);
				}
				else if (args[0].equalsIgnoreCase("sethome"))
				{
					commandSetHome(player);
				}
				else if (args[0].equalsIgnoreCase("call"))
				{
					commandCall(player);
				}
				else if (args[0].equalsIgnoreCase("top"))
				{
					commandTopHunters(player);
				}
				else if (args[0].equalsIgnoreCase("hunt"))
				{
					commandHuntWerewolf(player);
				}
				else if (args[0].equalsIgnoreCase("infectionpotion"))
				{
					commandInfectionPotion(player);
				}
				else if (args[0].equalsIgnoreCase("curepotion"))
				{
					commandCurePotion(player);
				}
				else if (args[0].equalsIgnoreCase("wolfbane"))
				{
					commandWolfbanePotion(player);
				}
				else if (args[0].equalsIgnoreCase("silversword"))
				{
					commandSilverSword(player);
				}
				else if (args[0].equalsIgnoreCase("lorebook"))
				{
					commandLoreBook(player);
				}
				else if (args[0].equalsIgnoreCase("on"))
				{
					if (!player.isOp())
					{
						this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
						return false;
					}
					Werewolf.pluginEnabled = true;

					this.plugin.log(" has been enabled");
					if (player != null)
					{
						player.sendMessage(ChatColor.AQUA + "Werewolves are now enabled");
					}
					for (World world : this.plugin.getServer().getWorlds())
					{
						this.plugin.loadSettings();
						if (this.plugin.isNightInWorld(world))
						{
							for (Player werewolf : world.getPlayers())
							{
								Werewolf.getWerewolfManager().setWerewolfSkin(werewolf);
							}
						}
					}
				}
				else if (args[0].equalsIgnoreCase("off"))
				{
					if (!player.isOp())
					{
						this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand, ChatColor.RED));
						return false;
					}

					Werewolf.pluginEnabled = true;
					this.plugin.log(" has been disabled");

					if (player != null)
					{
						player.sendMessage(ChatColor.AQUA + "Werewolves are now disabled");
					}

					for (World world : this.plugin.getServer().getWorlds())
					{
						this.plugin.saveSettings();

						for (Player werewolfPlayer : world.getPlayers())
						{
							Werewolf.getWerewolfManager().unsetWerewolfSkin(werewolfPlayer.getUniqueId(), true);
						}
					}
				}
				else
				{
					this.plugin.sendInfo(player, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InvalidCommand, ChatColor.RED));
				}
				return true;
			}
			return false;
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");

		List<String> result = new ArrayList<String>();

		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}

		if (args.length == 1 && (cmd.getName().equalsIgnoreCase("werewolf") || cmd.getName().equalsIgnoreCase("ww")))
		{
			List<String> arg1 = new ArrayList<String>();
			arg1.add("help");
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top"))
			{
				arg1.add("top");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.hunt"))
			{
				arg1.add("hunt");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.check"))
			{
				arg1.add("check");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.bounty"))
			{
				arg1.add("bounty");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.addbounty"))
			{
				arg1.add("addbounty");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.toggle"))
			{
				arg1.add("toggle");
			}
			if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infect"))
			{
				arg1.add("infect");
			}
			if (player != null)
			{
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.transform"))
				{
					arg1.add("transform");
				}
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectionpotion"))
				{
					arg1.add("infectionpotion");
				}
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.curepotion"))
				{
					arg1.add("curepotion");
				}
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.wolfbane"))
				{
					arg1.add("wolfbane");
				}
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.silversword"))
				{
					arg1.add("silversword");
				}
				if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.lorebook"))
				{
					arg1.add("lorebook");
				}
				if (this.plugin.useClans)
				{
					if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.clan"))
					{
						arg1.add("clan");
					}
					if (player == null || player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top"))
					{
						arg1.add("home");
					}
					if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
					{
						if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.call"))
						{
							arg1.add("call");
						}
						if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.sethome"))
						{
							arg1.add("sethome");
						}
					}
				}
			}
			Iterable<String> FIRST_ARGUMENTS = arg1;
			StringUtil.copyPartialMatches(args[0], FIRST_ARGUMENTS, result);
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("addbounty"))
		{
			List<String> arg2 = new ArrayList<String>();

			arg2.add("100");
			arg2.add("200");
			arg2.add("300");
			arg2.add("500");
			arg2.add("1000");
			arg2.add("2000");
			arg2.add("2500");
			arg2.add("5000");

			Iterable<String> SECOND_ARGUMENTS = arg2;
			StringUtil.copyPartialMatches(args[1], SECOND_ARGUMENTS, result);
		}
		else if (args.length == 2 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("infect") || args[0].equalsIgnoreCase("toggle")))
		{
			return null;
		}

		Collections.sort(result);
		return result;
	}

	public class Hunter
	{
		public String	name;
		public int		kills;

		Hunter(String hunterName, int k)
		{
			this.name = hunterName;
			this.kills = k;
		}
	}

	public class TopHuntersComparator implements Comparator<Commands.Hunter>
	{
		public int compare(Commands.Hunter object1, Commands.Hunter object2)
		{
			Commands.Hunter h1 = object1;
			Commands.Hunter h2 = object2;

			return h2.kills - h1.kills;
		}
	}
}