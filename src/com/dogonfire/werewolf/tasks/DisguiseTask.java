package com.dogonfire.werewolf.tasks;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.LanguageManager;
import com.dogonfire.werewolf.Werewolf;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DisguiseTask implements Runnable
{
	private Werewolf	plugin;
	private Player		player	= null;

	public DisguiseTask(Werewolf instance, Player p)
	{
		this.plugin = instance;
		this.player = p;
	}

	private void dropArmor()
	{
		PlayerInventory inventory = this.player.getInventory();
		for (ItemStack stack : inventory.getArmorContents())
		{
			if (stack != null && !stack.getType().equals(Material.AIR) && stack.getAmount() != 0)
			{
				if (Werewolf.getWerewolfManager().hasToDropItems(this.player.getUniqueId()))
				{
					this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
				}
				else
				{
					int slot = this.player.getInventory().firstEmpty();
					if (slot > -1)
					{
						this.player.getInventory().setItem(slot, stack);
					}
					else
					{
						this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
					}
				}
			}
		}

		this.player.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
	}

	private void dropMainHandItem()
	{
		PlayerInventory inventory = this.player.getInventory();

		ItemStack stack = inventory.getItemInMainHand();
		if ((stack == null) || (stack.getAmount() == 0) || (stack.getType().equals(Material.AIR)))
		{
			return;
		}
		if (Werewolf.getWerewolfManager().hasToDropItems(this.player.getUniqueId()))
		{
			this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
		}
		else
		{
			int slot = inventory.firstEmpty();
			if (slot > -1)
			{
				inventory.setItem(slot, stack);
			}
			else
			{
				this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
			}
		}
		inventory.setItemInMainHand(null);
	}

	private void dropOffHandItem()
	{
		PlayerInventory inventory = this.player.getInventory();

		ItemStack stack = inventory.getItemInOffHand();
		if ((stack == null) || (stack.getAmount() == 0) || (stack.getType().equals(Material.AIR)))
		{
			return;
		}
		if (Werewolf.getWerewolfManager().hasToDropItems(this.player.getUniqueId()))
		{
			this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
		}
		else
		{
			int slot = inventory.firstEmpty();
			if (slot > -1)
			{
				inventory.setItem(slot, stack);
			}
			else
			{
				this.player.getWorld().dropItemNaturally(this.player.getLocation(), stack);
			}
		}
		this.player.getInventory().setItemInOffHand(null);
	}

	public void run()
	{
		if (this.player == null)
		{
			this.plugin.logDebug("DisguiseTask::Run(): Player is null!");
			return;
		}

		/*
		 * if (Werewolf.getWerewolfManager().hasWerewolfSkin(this.player.
		 * getUniqueId())) { return; }
		 */

		ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(this.player.getUniqueId());

		Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.CONFUSION, 100, 1)), 1L);
		Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.HUNGER, 32000, 1)), 8L);
		Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.NIGHT_VISION, 32000, 0)), 16L);
		
		switch (clan)
		{
		case Potion: // Witherfang
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.JUMP, 32000, 2)), 16L);
			// Walkspeed works
			//Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.SPEED, 32000, 3)), 32L);

			this.player.setWalkSpeed(1.0F);
			break;
		case WildBite: // Silvermane
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.JUMP, 32000, 1)), 16L);
			// Walkspeed works
			//Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.SPEED, 32000, 1)), 32L);
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.REGENERATION, 32000, 0)), 64L);

			this.player.setWalkSpeed(0.5F);
			break;
		case WerewolfBite: // Bloodmoon
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.JUMP, 32000, 1)), 16L);
			// Walkspeed works
			// Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.SPEED, 32000, 1)), 32L);
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, this.player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 32000, 1)), 64L);

			this.player.setWalkSpeed(0.5F);
			break;
		default:
			break;
		}

		dropArmor();
		dropOffHandItem();
		dropMainHandItem();
		
		Boolean renamed = false;

		if (plugin.werewolfNamesEnabled)
		{
			String werewolfName = Werewolf.getWerewolfManager().getWerewolfName(player.getUniqueId());

			if (werewolfName != "")
			{
				Boolean inUse = false;

				// We check if anyone else online maybe already uses this
				// randomly generated name...
				for (Player otherPlayer : plugin.getServer().getOnlinePlayers())
				{
					if (otherPlayer.getPlayerListName().contains(werewolfName))
					{
						inUse = true;
					}
				}

				// if there are noone else with the name right now, just use it
				if (inUse == false)
				{
					player.setPlayerListName(ChatColor.GOLD + werewolfName);
					renamed = true;
				}
				else
				{ // oh boy, someone already use it. Time to use integers
					int n = 1;

					while (!renamed)
					{
						renamed = true;

						// Go through all online players and check for
						// WerewolfName1, WerewolfName2 etc. until one is found
						// where the number isn't taken
						for (Player otherPlayer : plugin.getServer().getOnlinePlayers())
						{
							if (otherPlayer.getPlayerListName().contains(werewolfName + n))
							{
								n++;
							}
						}

						// Now let's try to use this number :))
						try
						{
							player.setPlayerListName(ChatColor.GOLD + werewolfName + n);
						}
						catch (Exception ex)
						{
							n++;

							renamed = false;
						}
					}
				}
			}
		}

		if (renamed == false)
		{
			int n = 1;

			while (!renamed)
			{
				renamed = true;

				// Go through all online players and check for Werewolf1,
				// Werewolf2 etc. until one is found where the number isn't
				// taken
				for (Player otherPlayer : plugin.getServer().getOnlinePlayers())
				{
					if (otherPlayer.getPlayerListName().contains("Werewolf" + n))
					{
						n++;
					}
				}

				// Now let's try to use this number :))
				try
				{
					player.setPlayerListName(ChatColor.GOLD + "Werewolf" + n);
				}
				catch (Exception ex)
				{
					n++;

					renamed = false;
				}
			}
		}

		Werewolf.getWerewolfManager().pushPlayerData(this.player);
		Werewolf.getStatisticsManager().clearStatistics(this.player.getUniqueId());

		if (plugin.useScoreboards)
		{
			Werewolf.getWerewolfScoreboardManager().newPlayerHuntingScoreboard(this.player);
		}

		// Before trying to disguise, check if Disguises are enabled...
		if (plugin.disguisesEnabled)
		{
			Werewolf.getSkinManager().setWerewolfSkin(this.player, this.player.getPlayerListName());
		}

		if (this.plugin.isFullMoonInWorld(this.player.getWorld()) && !Werewolf.getWerewolfManager().hasRecentTransform(this.player.getUniqueId()))
		{
			Werewolf.getWerewolfManager().incrementNumberOfFullMoonTransformations(this.player.getUniqueId());
		}

		Werewolf.getWerewolfManager().setLastTransformation(this.player.getUniqueId());

		// if (this.plugin.healthBarEnabled)
		// {
		// ScoreboardManager localScoreboardManager =
		// Bukkit.getScoreboardManager();
		// }

		if (this.plugin.useWerewolfGroupName)
		{
			String originalGroup = Werewolf.getPermissionsManager().getGroup(this.player.getName());
			Werewolf.getWerewolfManager().setOriginalPermissionGroup(this.player.getUniqueId(), originalGroup);
			Werewolf.getPermissionsManager().setGroup(this.player.getName(), this.plugin.werewolfGroupName);
		}

		Werewolf.getLanguageManager().setAmount("" + Werewolf.getWerewolfManager().getNumberOfTransformations(this.player.getUniqueId()));

		this.player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Transform, ChatColor.LIGHT_PURPLE));

		if (Werewolf.getPermissionsManager().hasPermission(this.player, "werewolf.howl"))
		{
			Werewolf.getLanguageManager().setType("/howl");
			String message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfoCommandHowl, ChatColor.AQUA);
			this.player.sendMessage(message);
		}

		if (Werewolf.getPermissionsManager().hasPermission(this.player, "werewolf.growl"))
		{
			Werewolf.getLanguageManager().setType("/growl");
			String message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfoCommandGrowl, ChatColor.AQUA);
			this.player.sendMessage(message);
		}

		this.plugin.log(this.player.getName() + " turned into a werewolf!");
	}
}
