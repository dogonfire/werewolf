package com.dogonfire.werewolf.listeners;

import com.dogonfire.werewolf.Werewolf;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener
{
	private Werewolf	plugin	= null;

	public InventoryListener(Werewolf p)
	{
		this.plugin = p;
	}

	/* Doesn't work, and isn't needed as items are tested when the inventory is closed
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (!Werewolf.getWerewolfManager().hasWerewolfSkin(event.getWhoClicked().getUniqueId()))
		{
			return;
		}
		
		if (event.getInventory().getType() != InventoryType.PLAYER)
		{
			return;
		}

		if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.isShiftClick())
		{
			event.setCancelled(true);
		}
	}*/

	/*
	@EventHandler
	public void onInventory(InventoryCloseEvent event)
	{
		if (!Werewolf.getWerewolfManager().isWolfForm(event.getPlayer().getUniqueId()))
		{
			return;			
		}
				
		if (event.getView().getType() == InventoryType.CRAFTING)
		{
			preventArmor((Player) event.getPlayer());
		}
	}

	private void preventArmor(Player player)
	{
		PlayerInventory pinv = player.getInventory();
		ItemStack helmet = pinv.getHelmet();
		ItemStack chestplate = pinv.getChestplate();
		ItemStack leggings = pinv.getLeggings();
		ItemStack boots = pinv.getBoots();

		if (helmet != null)
		{
			if (pinv.firstEmpty() >= 0)
			{
				pinv.addItem(new ItemStack[] { helmet });
			}
			else
			{
				player.getWorld().dropItem(player.getLocation(), helmet);
			}

			pinv.setHelmet(new ItemStack(Material.AIR));
		}

		if (chestplate != null)
		{
			if (pinv.firstEmpty() >= 0)
			{
				pinv.addItem(new ItemStack[] { chestplate });
			}
			else
			{
				player.getWorld().dropItem(player.getLocation(), chestplate);
			}
			pinv.setChestplate(new ItemStack(Material.AIR));
		}

		if (leggings != null)
		{
			if (pinv.firstEmpty() >= 0)
			{
				pinv.addItem(new ItemStack[] { leggings });
			}
			else
			{
				player.getWorld().dropItem(player.getLocation(), leggings);
			}
			pinv.setLeggings(new ItemStack(Material.AIR));
		}

		if (boots != null)
		{
			if (pinv.firstEmpty() >= 0)
			{
				pinv.addItem(new ItemStack[] { boots });
			}
			else
			{
				player.getWorld().dropItem(player.getLocation(), boots);
			}
			pinv.setBoots(new ItemStack(Material.AIR));
		}
	}
	*/

	/*
	 * @EventHandler public void onPrepareItemCraft(PrepareItemCraftEvent event)
	 * { if (event.getView().getType() != InventoryType.WORKBENCH) {
	 * this.plugin.logDebug("Not workbench. Was " +
	 * event.getView().getType().name()); return; }
	 * 
	 * this.plugin.logDebug("PrepareItemCraftEvent");
	 * 
	 * CraftingInventory craftingInventory = event.getInventory(); if
	 * (!craftingInventory.contains(Material.IRON_SWORD)) {
	 * this.plugin.logDebug("Not sword"); return; }
	 * 
	 * if (!craftingInventory.contains(Material.QUARTZ)) {
	 * this.plugin.logDebug("Not quartz"); return; }
	 * 
	 * ItemStack silverSword = new ItemStack(Material.IRON_SWORD);
	 * 
	 * ItemMeta itemMeta = silverSword.getItemMeta();
	 * 
	 * List<String> list = new ArrayList(); list.add(ChatColor.DARK_GREEN +
	 * "Good for slaying Werewolves!"); itemMeta.setDisplayName(ChatColor.GOLD +
	 * "Werewolf Slayer"); itemMeta.setLore(list);
	 * 
	 * silverSword.setItemMeta(itemMeta);
	 * 
	 * craftingInventory.setResult(silverSword); }
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if ((!Werewolf.pluginEnabled) || (!this.plugin.dropArmorOnTransform))
		{
			return;
		}

		if (!(event.getPlayer() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getPlayer();
		if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
		{
			return;
		}

		removeOffHand(player);
		removeMainHand(player);
		removeArmor(player);
	}

	public void removeMainHand(Player player)
	{
		if (isForbiddenItem(player.getInventory().getItemInMainHand().getType()))
		{
			if (Werewolf.getWerewolfManager().hasToDropItems(player.getUniqueId()))
			{
				player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItemInMainHand());
			}
			else
			{
				ItemStack stack = player.getInventory().getItemInMainHand();
				int slot = player.getInventory().firstEmpty();
				if (slot > -1)
				{
					player.getInventory().setItem(slot, stack);
				}
				else
				{
					player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItemInMainHand());
				}
			}
			player.getInventory().setItemInMainHand(null);
		}
	}

	public void removeOffHand(Player player)
	{
		if (isForbiddenItem(player.getInventory().getItemInOffHand().getType()))
		{
			if (Werewolf.getWerewolfManager().hasToDropItems(player.getUniqueId()))
			{
				player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItemInOffHand());
			}
			else
			{
				ItemStack stack = player.getInventory().getItemInOffHand();
				int slot = player.getInventory().firstEmpty();
				if (slot > -1)
				{
					player.getInventory().setItem(slot, stack);
				}
				else
				{
					player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItemInOffHand());
				}
			}
			player.getInventory().setItemInOffHand(null);
		}
	}

	public void removeArmor(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		for (ItemStack stack : inventory.getArmorContents())
		{
			if (stack != null && !stack.getType().equals(Material.AIR) && stack.getAmount() != 0)
			{
				if (Werewolf.getWerewolfManager().hasToDropItems(player.getUniqueId()))
				{
					player.getWorld().dropItemNaturally(player.getLocation(), stack);
				}
				else
				{
					int slot = player.getInventory().firstEmpty();
					if (slot > -1)
					{
						player.getInventory().setItem(slot, stack);
					}
					else
					{
						player.getWorld().dropItemNaturally(player.getLocation(), stack);
					}
				}
			}
		}

		player.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
	}

	@EventHandler
	public void onPlayerNewHeldItem(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = player;
		if (entity.getType() == EntityType.PLAYER && player != null) {
			if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
			{
				return;
			}

			if (this.plugin.keepWerewolfHandsFree)
			{
				int heldItemSlot = event.getNewSlot();
				if (player.getInventory().getItem(heldItemSlot) != null && player.getInventory().getItem(heldItemSlot).getType() != Material.AIR)
				{
					Material material = player.getInventory().getItem(heldItemSlot).getType();
					if (!isForbiddenItem(material)) { return; }

					ItemStack stack = player.getInventory().getItem(heldItemSlot);
					int inventorySlots = 36; //player.getInventory().getSize();
					Boolean foundSlot = false;

					for (int slot = 0; slot < inventorySlots; slot++) {
						if ((player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR) && slot != heldItemSlot) {
							player.getInventory().setItem(slot, stack);
							player.getInventory().setItem(heldItemSlot, new ItemStack(Material.AIR));
							foundSlot = true;
							break;
						}
					}
					
					if (!foundSlot) {
						event.setCancelled(true);
					}
				}
			}	
		}
	}

	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event)
	{
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.PLAYER)
		{
			Player player = (Player) entity;

			if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
			{
				return;
			}

			Material material = event.getItem().getItemStack().getType();
			// The difference is that, if it is not a forbidden item, we still
			// don't want
			// the item to be in the main hand, but if there is no other choice,
			// then it's fine.
			if (!isForbiddenItem(material))
			{
				if (player.getInventory().getItemInMainHand().getType() == Material.AIR)
				{
					ItemStack stack = event.getItem().getItemStack();

					int heldItemSlot = player.getInventory().getHeldItemSlot();
					int inventorySlots = 36;

					for (int slot = 0; slot < inventorySlots; slot++)
					{
						if ((player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR) && slot != heldItemSlot)
						{
							player.getInventory().setItem(slot, stack);
							event.getItem().remove();
							event.setCancelled(true);
							break;
						}
					}
				}
				return;
			}

			if (this.plugin.keepWerewolfHandsFree)
			{
				if (player.getInventory().getItemInMainHand().getType() == Material.AIR)
				{
					ItemStack stack = event.getItem().getItemStack();

					int heldItemSlot = player.getInventory().getHeldItemSlot();
					int inventorySlots = 36; // player.getInventory().getSize();
					Boolean foundSlot = false;

					for (int slot = 0; slot < inventorySlots; slot++)
					{
						if ((player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR) && slot != heldItemSlot)
						{
							player.getInventory().setItem(slot, stack);
							event.getItem().remove();
							event.setCancelled(true);
							foundSlot = true;
							break;
						}
					}

					if (!foundSlot)
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public boolean isForbiddenItem(Material material)
	{
		switch (material)
		{
		case WOODEN_SWORD:
		case GOLDEN_SWORD:
		case STONE_SWORD:
		case DIAMOND_SWORD:
		case IRON_SWORD:
		case SHIELD:
		case BOW:
		case POTION:
		case SPLASH_POTION:
		case WOODEN_AXE:
		case GOLDEN_AXE:
		case STONE_AXE:
		case DIAMOND_AXE:
		case IRON_AXE:
		case WOODEN_PICKAXE:
		case GOLDEN_PICKAXE:
		case STONE_PICKAXE:
		case DIAMOND_PICKAXE:
		case IRON_PICKAXE:
		case WOODEN_HOE:
		case GOLDEN_HOE:
		case STONE_HOE:
		case DIAMOND_HOE:
		case IRON_HOE:
		case WOODEN_SHOVEL:
		case GOLDEN_SHOVEL:
		case STONE_SHOVEL:
		case DIAMOND_SHOVEL:
		case IRON_SHOVEL:
		case FLINT_AND_STEEL:
		case FISHING_ROD:
		case TRIDENT:
		case CROSSBOW:
			return true;
		default:
			return false;
		}
	}
}
