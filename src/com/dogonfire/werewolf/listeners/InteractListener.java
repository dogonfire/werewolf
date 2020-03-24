package com.dogonfire.werewolf.listeners;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.LanguageManager;
import com.dogonfire.werewolf.Werewolf;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener
{
	private Werewolf	plugin	= null;

	public InteractListener(Werewolf plugin)
	{
		this.plugin = plugin;
	}

	private boolean checkForEatingFood(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		Material type = player.getInventory().getItemInMainHand().getType();
		switch (type)
		{
			case APPLE:
			case BREAD:
			case PUMPKIN:
			case MELON:
			case MUSHROOM_STEW:
			case BEETROOT_SOUP:
			case BEETROOT:
			case BAKED_POTATO:
			case PUMPKIN_PIE:
			case POTATO:
			case POISONOUS_POTATO:
			case GOLDEN_APPLE:
			case ENCHANTED_GOLDEN_APPLE:
			case DRIED_KELP:
			case CAKE:
			case COOKIE:
			case CARROT:
			case MILK_BUCKET:
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfTryEat, ChatColor.RED));
				event.setCancelled(true);
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	private boolean checkForDefense(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		Material type = player.getInventory().getItemInMainHand().getType();
		if (isForbiddenDefense(type))
		{
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfTryDefense, ChatColor.RED));
			event.setCancelled(true);
			return true;
		}
		
		return false;
	}

	private boolean checkForDrinkingWerewolfCurePotion(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItemInMainHand();
		
		if(!Werewolf.getItemManager().isCurePotion(handItem))
		{
			return false;
		}

		/*
		if (!handItem.hasItemMeta())
		{
			return false;
		}
		
		if (handItem.getItemMeta().getDisplayName().length() < 10)
		{
			return false;
		}
		
		if (!handItem.getItemMeta().getDisplayName().substring(2).contains("Werewolf cure"))
		{
			return false;
		}
*/		
		if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.cure.drink") && !player.isOp())
		{
			return false;
		}
		
		if (!Werewolf.getWerewolfManager().isWerewolf(player.getUniqueId()))
		{
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkCureFailure, ChatColor.LIGHT_PURPLE));
		}
		else if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
		{
			if (Math.random() < this.plugin.cureChance)
			{
				Werewolf.getWerewolfManager().unmakeWerewolf(player.getUniqueId());
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkCureSuccess, ChatColor.LIGHT_PURPLE));
			}
			else
			{
				player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkCureFailure, ChatColor.LIGHT_PURPLE));
			}
		}
		else
		{
			return false;
		}
		
		if (player.getInventory().getItemInMainHand().getAmount() == 1)
		{
			player.getInventory().remove(handItem);
		}
		else
		{
			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		}
		return false;
	}

	private boolean checkForDrinkingWerewolfInfectionPotion(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItemInMainHand();

		if(!Werewolf.getItemManager().isInfectionPotion(handItem))
		{
			return false;
		}

/*		
		if (!handItem.hasItemMeta())
		{
			return false;
		}
		if (!(player instanceof Player))
		{
			this.plugin.logDebug("Non-player tried to drink a werewolf potion!");
			return false;
		}
		if (handItem.getItemMeta().getDisplayName().length() < 10)
		{
			return false;
		}

		
		if (!handItem.getItemMeta().getDisplayName().substring(2).contains("Werewolf infection"))
		{
			return false;
		}
*/
		if ((!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.infection.drink")) && (!player.isOp()))
		{
			return false;
		}
		
		if (Werewolf.getWerewolfManager().isWerewolf(player))
		{
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkInfectionFailure, ChatColor.LIGHT_PURPLE));
		}
		else if (this.plugin.isFullMoonInWorld(player.getWorld()))
		{
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkInfectionSuccess, ChatColor.LIGHT_PURPLE));
			plugin.log(player.getName() + " contracted the werewolf infection by drinking a potion!");
			
			final Player targetPlayer = player;

			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
			{
				public void run()
				{
					Werewolf.getWerewolfManager().makeWerewolf(targetPlayer, true, ClanManager.ClanType.Potion);
				}
			}, 140L);
		}
		else
		{
			player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.DrinkInfectionFailure, ChatColor.LIGHT_PURPLE));
		}
		
		if (player.getInventory().getItemInMainHand().getAmount() == 1)
		{
			player.getInventory().remove(handItem);
		}
		else
		{
			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		}
		
		return true;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		Player player = event.getPlayer();
		if (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) && (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId())))
		{
			checkForEatingFood(event);
			checkForDefense(event);
		}
	}

	@EventHandler
	public void onPlayerConsumeEvent(PlayerItemConsumeEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		if (Werewolf.getWerewolfManager().isWerewolf(event.getPlayer()))
		{
			if (!checkForDrinkingWerewolfCurePotion(event))
			{
				checkForDrinkingWerewolfInfectionPotion(event);
			}
		}
		else if (!checkForDrinkingWerewolfInfectionPotion(event))
		{
			checkForDrinkingWerewolfCurePotion(event);
		}
	}

	@EventHandler
	public void dispenseArmorEvent(BlockDispenseArmorEvent event)
	{
		if (event.getTargetEntity() instanceof Player)
		{
			Player player = (Player) event.getTargetEntity();

			if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	public boolean isForbiddenDefense(Material material)
	{
		switch (material)
		{
		case GOLDEN_CHESTPLATE:
		case LEATHER_CHESTPLATE:
		case DIAMOND_CHESTPLATE:
		case CHAINMAIL_CHESTPLATE:
		case IRON_CHESTPLATE:
		case CHAINMAIL_BOOTS:
		case GOLDEN_BOOTS:
		case LEATHER_BOOTS:
		case IRON_BOOTS:
		case DIAMOND_BOOTS:
		case GOLDEN_LEGGINGS:
		case CHAINMAIL_LEGGINGS:
		case LEATHER_LEGGINGS:
		case IRON_LEGGINGS:
		case DIAMOND_LEGGINGS:
		case CHAINMAIL_HELMET:
		case GOLDEN_HELMET:
		case LEATHER_HELMET:
		case IRON_HELMET:
		case DIAMOND_HELMET:
		case TURTLE_HELMET:
		case ELYTRA:
		case SHIELD:
			return true;
		default:
			return false;
		}
	}
}
