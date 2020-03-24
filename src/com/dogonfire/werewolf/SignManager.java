package com.dogonfire.werewolf;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.dogonfire.werewolf.api.WerewolfAPI;

public class SignManager implements Listener
{
	private Werewolf	plugin;

	SignManager(Werewolf p)
	{
		this.plugin = p;
	}

	private boolean isWerewolfSignEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(0);
		return ((signTitle != null) && (signTitle.trim().equalsIgnoreCase("Werewolf")));
	}

	private boolean isWerewolfSignInfectionPotionEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(1);
		return ((signTitle != null) && signTitle.trim().equalsIgnoreCase("Infection"));
	}

	private boolean isWerewolfSignCurePotionEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(1);
		return ((signTitle != null) && signTitle.trim().equalsIgnoreCase("Cure"));
	}

	private boolean isWerewolfSignSilverSwordEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(1);
		return ((signTitle != null) && signTitle.trim().equalsIgnoreCase("SilverSword"));
	}

	private boolean isWerewolfSignWolfbanePotionEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(1);
		return ((signTitle != null) && signTitle.trim().equalsIgnoreCase("Wolfbane"));
	}

	private boolean isWerewolfSignLoreBookEvent(SignChangeEvent event)
	{
		String signTitle = event.getLine(1);
		return ((signTitle != null) && signTitle.trim().equalsIgnoreCase("LoreBook"));
	}

	public boolean handleNewWerewolfInfectionPotionSign(SignChangeEvent event)
	{
		String priceText = "";
		if (this.plugin.vaultEnabled)
		{
			priceText = Werewolf.getEconomy().format(this.plugin.infectionPrice);
		}
		event.setLine(0, "Werewolf");
		event.setLine(1, "Infection");
		event.setLine(2, priceText);
		event.setLine(3, "");

		return true;
	}

	public boolean handleNewWerewolfCurePotionSign(SignChangeEvent event)
	{
		String priceText = "";
		if (this.plugin.vaultEnabled)
		{
			priceText = Werewolf.getEconomy().format(this.plugin.curePrice);
		}
		event.setLine(0, "Werewolf");
		event.setLine(1, "Cure");
		event.setLine(2, priceText);
		event.setLine(3, "");

		return true;
	}

	public boolean handleNewWerewolfWolfbanePotionSign(SignChangeEvent event)
	{
		String priceText = "";
		if (this.plugin.vaultEnabled)
		{
			priceText = Werewolf.getEconomy().format(this.plugin.wolfbanePrice);
		}
		event.setLine(0, "Werewolf");
		event.setLine(1, "Wolfbane");
		event.setLine(2, priceText);
		event.setLine(3, "");

		return true;
	}

	public boolean handleNewWerewolfSilverSwordSign(SignChangeEvent event)
	{
		String priceText = "";
		if (this.plugin.vaultEnabled)
		{
			priceText = Werewolf.getEconomy().format(this.plugin.silverSwordPrice);
		}
		event.setLine(0, "Werewolf");
		event.setLine(1, "SilverSword");
		event.setLine(2, priceText);
		event.setLine(3, "");

		return true;
	}

	public boolean handleNewWerewolfLoreBookSign(SignChangeEvent event)
	{
		String priceText = "";
		if (this.plugin.vaultEnabled)
		{
			priceText = Werewolf.getEconomy().format(this.plugin.bookPrice);
		}
		event.setLine(0, "Werewolf");
		event.setLine(1, "LoreBook");
		event.setLine(2, priceText);
		event.setLine(3, "");

		return true;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		if (!isWerewolfSignEvent(event))
		{
			return;
		}
		
		if (isWerewolfSignInfectionPotionEvent(event))
		{
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.infection.place"))
			{
				if (!handleNewWerewolfInfectionPotionSign(event))
				{
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				}
				else
				{
					if (this.plugin.vaultEnabled)
					{
						Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.infectionPrice));
					}
					else
					{
						Werewolf.getLanguageManager().setAmount("free");
					}
					
					plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouPlacedAInfectionPotionSign, ChatColor.GREEN, "", (int)this.plugin.infectionPrice, 1);
				}
			}
			else
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}
		
		if (isWerewolfSignCurePotionEvent(event))
		{
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.cure.place"))
			{
				if (!handleNewWerewolfCurePotionSign(event))
				{
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				}
				else
				{
					if (this.plugin.vaultEnabled)
					{
						Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.curePrice));
					}
					else
					{
						Werewolf.getLanguageManager().setAmount("free");
					}
					this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouPlacedACurePotionSign, ChatColor.GREEN, "", (int)this.plugin.curePrice, 1);
				}
			}
			else
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}
		
		if (isWerewolfSignWolfbanePotionEvent(event))
		{
			if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.wolfbane.place"))
			{
				if (!handleNewWerewolfWolfbanePotionSign(event))
				{
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				}
				else
				{
					if (this.plugin.vaultEnabled)
					{
						Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.wolfbanePrice));
					}
					else
					{
						Werewolf.getLanguageManager().setAmount("free");
					}
					this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouPlacedAWolfbanePotionSign, ChatColor.GREEN, "", (int)this.plugin.wolfbanePrice, 1);
				}
			}
			else
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}

		if (isWerewolfSignSilverSwordEvent(event))
		{
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.silversword.place")))
			{
				if (!handleNewWerewolfSilverSwordSign(event))
				{
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				}
				else
				{
					if (this.plugin.vaultEnabled)
					{
						Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.silverSwordPrice));
					}
					else
					{
						Werewolf.getLanguageManager().setAmount("free");
					}
					this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouPlacedASilverSwordSign, ChatColor.GREEN, "", (int)this.plugin.silverSwordPrice, 1);
				}
			}
			else
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}

		if (isWerewolfSignLoreBookEvent(event))
		{
			if (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.lorebook.place")))
			{
				if (!handleNewWerewolfLoreBookSign(event))
				{
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				}
				else
				{
					if (this.plugin.vaultEnabled)
					{
						Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.bookPrice));
					}
					else
					{
						Werewolf.getLanguageManager().setAmount("free");
					}
					this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouPlacedABookSign, ChatColor.GREEN, "", (int)this.plugin.bookPrice, 1);
				}
			}
			else
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	private boolean checkForActivatingSign(PlayerInteractEvent event)
	{
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (event.getClickedBlock().getType() != Material.OAK_WALL_SIGN)
			{
				return false;
			}
			
			BlockState state = event.getClickedBlock().getState();

			Sign sign = (Sign) state;

			String titleLine = sign.getLine(0);
			if ((titleLine == null) || (!titleLine.equals("Werewolf")))
			{
				return false;
			}
			
			Player player = event.getPlayer();
			String typeLine = sign.getLine(1);
			
			if ("Infection".equals(typeLine) && (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.infection.use")))
			{
				double price = 0.0D;
				if (this.plugin.vaultEnabled)
				{
					Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.infectionPrice));

					price = this.plugin.infectionPrice;
					if (!Werewolf.getEconomy().has(player, price))
					{
						this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney, ChatColor.RED, "", (int)price, 1);
						return true;
					}
					Werewolf.getEconomy().withdrawPlayer(player, price);
				}
				else
				{
					Werewolf.getLanguageManager().setAmount("free");
				}
				
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), WerewolfAPI.newWerewolfInfectionPotion());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouBoughtAInfectionPotion, ChatColor.GREEN, "", (int)price, 1);
				this.plugin.log(event.getPlayer().getName() + " bought a werewolf infection potion for " + price);

				return true;
			}
			else if ("Cure".equals(typeLine) && (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.cure.use"))))
			{
				double price = 0.0D;

				Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.curePrice));
				if (this.plugin.vaultEnabled)
				{
					price = this.plugin.curePrice;
					if (!Werewolf.getEconomy().has(player, price))
					{
						this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney, ChatColor.RED, "", (int)price, 1);
						return true;
					}
					Werewolf.getEconomy().withdrawPlayer(player, price);
				}
				else
				{
					Werewolf.getLanguageManager().setAmount("free");
				}
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), WerewolfAPI.newWerewolfCurePotion());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouBoughtACurePotion, ChatColor.GREEN, "", (int)price, 1);
				this.plugin.log(event.getPlayer().getName() + " bought a werewolf cure potion for " + price);

				return true;
			}
			else if ("Wolfbane".equals(typeLine) && (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.wolfbane.use"))))
			{
				double price = 0.0D;

				Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.wolfbanePrice));
				if (this.plugin.vaultEnabled)
				{
					price = this.plugin.wolfbanePrice;
					if (!Werewolf.getEconomy().has(player, price))
					{
						this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney, ChatColor.RED, "", (int)price, 1);
						return true;
					}
					Werewolf.getEconomy().withdrawPlayer(player, price);
				}
				else
				{
					Werewolf.getLanguageManager().setAmount("free");
				}
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), WerewolfAPI.newWerewolfWolfbanePotion());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouBoughtAWolfbanePotion, ChatColor.GREEN, "", (int)price, 1);
				this.plugin.log(event.getPlayer().getName() + " bought a werewolf wolfbane potion for " + price);

				return true;
			}
			else if ("SilverSword".equals(typeLine) && (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.silversword.use"))))
			{
				double price = 0.0D;

				Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.silverSwordPrice));
				if (this.plugin.vaultEnabled)
				{
					price = this.plugin.silverSwordPrice;
					if (!Werewolf.getEconomy().has(player, price))
					{
						this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney, ChatColor.RED, "", (int)price, 1);
						return true;
					}
					Werewolf.getEconomy().withdrawPlayer(player, price);
				}
				else
				{
					Werewolf.getLanguageManager().setAmount("free");
				}
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), WerewolfAPI.newWerewolfSilverSword());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouBoughtASilverSword, ChatColor.GREEN, "", (int)price, 1);
				this.plugin.log(event.getPlayer().getName() + " bought a werewolf lorebook for " + price);

				return true;
			}
			else if ("LoreBook".equals(typeLine) && (player.isOp() || (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.lorebook.use"))))
			{
				double price = 0.0D;

				Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.bookPrice));
				if (this.plugin.vaultEnabled)
				{
					price = this.plugin.bookPrice;
					if (!Werewolf.getEconomy().has(player, price))
					{
						this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney, ChatColor.RED, "", (int)price, 1);
						return true;
					}
					Werewolf.getEconomy().withdrawPlayer(player, price);
				}
				else
				{
					Werewolf.getLanguageManager().setAmount("free");
				}
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), WerewolfAPI.newWerewolfLorebook());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouBoughtALoreBook, ChatColor.GREEN, "", (int)price, 1);
				this.plugin.log(event.getPlayer().getName() + " bought a werewolf lorebook for " + price);

				return true;
			}

			return true;
		}
		return false;
	}
}