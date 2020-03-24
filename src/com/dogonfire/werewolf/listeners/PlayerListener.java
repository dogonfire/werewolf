package com.dogonfire.werewolf.listeners;

import java.util.Collection;
import java.util.Random;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.DamageManager;
import com.dogonfire.werewolf.LanguageManager;
import com.dogonfire.werewolf.Werewolf;
import com.dogonfire.werewolf.tasks.CheckTransformationTask;
import com.dogonfire.werewolf.tasks.PotionEffectTask;
import com.dogonfire.werewolf.versioning.UpdateNotifier;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener
{
	private Werewolf	plugin	= null;
	private Random		random	= new Random();

	public PlayerListener(Werewolf plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		Werewolf.getWerewolfManager().unsetWerewolfSkin(event.getPlayer().getUniqueId(), true);
		
		if (Werewolf.getWerewolfManager().isWerewolf(event.getPlayer().getUniqueId())) {
			// We want to be sure potion effects are cleared...
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.CONFUSION);
			// Walkspeed works sooo, but let's still remove it...
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.SPEED);
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.HUNGER);
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.NIGHT_VISION);
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.INCREASE_DAMAGE);
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.REGENERATION);
			Werewolf.pu.removePotionEffectNoGraphic(event.getPlayer(), PotionEffectType.JUMP);
			
			event.getPlayer().setWalkSpeed(0.2F);
		}

		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new CheckTransformationTask(plugin, event.getPlayer().getUniqueId()), 20L);
		
		if (this.plugin.useUpdateNotifications && (event.getPlayer().isOp() || Werewolf.getPermissionsManager().hasPermission(event.getPlayer(), "werewolf.updates")))
		{
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new UpdateNotifier(this.plugin, event.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event)
	{
		Werewolf.getWerewolfManager().unsetWerewolfSkin(event.getPlayer().getUniqueId(), true);
	}

	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}

		if (!event.isCancelled())
		{
			if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
			{
				return;
			}

			Player nearestWerewolf = Werewolf.getWerewolfManager().getNearestWerewolf(event.getPlayer().getUniqueId());
			if (nearestWerewolf != null && DamageManager.canPlayerHit(event.getPlayer(), nearestWerewolf.getLocation()))
			{
				double damage = DamageManager.getDamageFromItemInHand(event.getPlayer());

				plugin.logDebug("Doing " + damage + " damage to " + nearestWerewolf.getName());

				nearestWerewolf.damage(damage, event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onFlightAttempt(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();
		if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
		{
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		if (!Werewolf.getWerewolfManager().hasWerewolfSkin(event.getPlayer().getUniqueId()))
		{
			return;
		}
		Player player = event.getPlayer();
		
		if (Math.random() < 0.05D)
		{
			for (Entity entity : player.getNearbyEntities(this.plugin.wolfdistance, this.plugin.wolfdistance, this.plugin.wolfdistance))
			{
				if (entity instanceof Wolf)
				{
					Wolf wolf = (Wolf) entity;
					if ((!wolf.isTamed()) || (wolf.getOwner() == null))
					{
						player.sendMessage(ChatColor.LIGHT_PURPLE + "A wild wolf joins your pack!");

						double maxHealth = 20;
						AttributeInstance healthAttribute = wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH);
						healthAttribute.setBaseValue(maxHealth);
						wolf.setHealth(maxHealth);
						wolf.setOwner(player);
						wolf.setTamed(true);

						Werewolf.getWerewolfManager().increaseNumberOfPackWolvesForPlayer(player.getUniqueId());
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (event.getEntity().getKiller() == null || event.getEntity().getKiller().getUniqueId() == null)
		{
			if (this.plugin.renameWerewolves && Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getUniqueId()))
			{
				String werewolfName = "Werewolf";
				if (plugin.werewolfNamesEnabled) {
					werewolfName = Werewolf.getWerewolfManager().getWerewolfName(event.getEntity().getUniqueId());
				}
				switch (this.random.nextInt(3))
				{
					case 0:
						event.setDeathMessage(werewolfName + " died");
						break;
					case 1:
						event.setDeathMessage(werewolfName + " was killed");
						break;
					case 2:
						event.setDeathMessage(werewolfName + " died");
						break;
					default:
						break;
				}
			}
			return;
		}

		String killerName = event.getEntity().getKiller().getName();
		String victimName = event.getEntity().getName();

		if (this.plugin.renameWerewolves)
		{
			if (Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getKiller().getUniqueId()))
			{
				String werewolfName = "Werewolf";
				if (plugin.werewolfNamesEnabled) {
					werewolfName = Werewolf.getWerewolfManager().getWerewolfName(event.getEntity().getKiller().getUniqueId());
				}
				switch (this.random.nextInt(4))
				{
					case 0:
						event.setDeathMessage(victimName + " was slaughtered by " + werewolfName);
						break;
					case 1:
						event.setDeathMessage(victimName + " was ripped apart by " + werewolfName);
						break;
					case 2:
						event.setDeathMessage(victimName + " was torn to pieces by " + werewolfName);
						break;
					case 3:
						event.setDeathMessage(victimName + " was eaten by " + werewolfName);
						break;
					default:
						break;
				}
			}
			else if (Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getUniqueId()))
			{
				String werewolfName = "Werewolf";
				if (plugin.werewolfNamesEnabled) {
					werewolfName = Werewolf.getWerewolfManager().getWerewolfName(event.getEntity().getUniqueId());
				}
				if (killerName != null)
				{
					event.setDeathMessage(werewolfName + " was slain by " + killerName);
				}
				else
				{
					event.setDeathMessage(werewolfName + " died");
				}
			}
		}

		if (this.plugin.useTrophies && Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getUniqueId()))
		{
			Player killer = this.plugin.getServer().getPlayer(killerName);

			if (killer != null)
			{
				switch (killer.getInventory().getItemInMainHand().getType())
				{
					case DIAMOND_SWORD:
					case GOLDEN_SWORD:
					case IRON_SWORD:
					case STONE_SWORD:
					case WOODEN_SWORD:
					{
						org.bukkit.inventory.ItemStack trophy = Werewolf.getTrophyManager().getTrophyFromWerewolfPlayer(killer.getUniqueId(), event.getEntity().getUniqueId());
						event.getDrops().add(trophy);

						event.getEntity().getKiller().sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouCutTheHeadOfWerewolf, ChatColor.WHITE));
					} break;

					default:
					  break;
				}
			}
		}

		if (this.plugin.useClans && Werewolf.getWerewolfManager().isWerewolf(event.getEntity().getUniqueId()) && Werewolf.getWerewolfManager().isWerewolf(event.getEntity().getKiller().getUniqueId()))
		{
			ClanManager.ClanType killerClan = Werewolf.getWerewolfManager().getWerewolfClan(event.getEntity().getKiller().getUniqueId());
			ClanManager.ClanType victimClan = Werewolf.getWerewolfManager().getWerewolfClan(event.getEntity().getUniqueId());
			
			if (killerClan.equals(victimClan) && Werewolf.getClanManager().isAlpha(victimClan, event.getEntity().getUniqueId()))
			{
				Werewolf.getClanManager().assignAlphaInClan(killerClan, event.getEntity().getKiller().getUniqueId());

				Werewolf.getLanguageManager().setPlayerName(killerName);
				Werewolf.getWerewolfManager().sendMessageToClan(killerClan, Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NewClanAlpha, ChatColor.WHITE));
				
				// Apply the new alpha skin if player is in werewolf form
				if (Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getKiller().getUniqueId()))
				{
					Werewolf.getSkinManager().unsetWerewolfSkin(event.getEntity().getKiller());
					Werewolf.getSkinManager().setWerewolfSkin(event.getEntity().getKiller(), event.getEntity().getKiller().getPlayerListName());
				}
			}
		}

		if (Werewolf.getWerewolfManager().hasWerewolfSkin(event.getEntity().getUniqueId()))
		{
			if (this.plugin.cureWerewolfWhenSlain)
			{
				Werewolf.getWerewolfManager().unmakeWerewolf(event.getEntity().getUniqueId());
			}
			else
			{
				Werewolf.getWerewolfManager().unsetWerewolfSkin(event.getEntity().getUniqueId(), false);

				Werewolf.getWerewolfManager().setInfectedThisNight(event.getEntity().getUniqueId(), event.getEntity().getName(), true);
			}
		}
	}
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent event)
	{
		if (!Werewolf.getItemManager().isWolfbanePotion(event.getPotion().getItem())) 
		{
			return;
		}
		
		Collection<LivingEntity> affectedEntity = event.getAffectedEntities();
		for (LivingEntity livingEntity : affectedEntity)
		{
			if (livingEntity instanceof Player)
			{
				Player player = (Player) livingEntity;

				if (Werewolf.getWerewolfManager().isWolfForm(player.getUniqueId()) && random.nextInt(100) < plugin.wolfbaneUntransformChance)
				{
					Werewolf.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new PotionEffectTask(this.plugin, player, new PotionEffect(PotionEffectType.POISON, 4 * 20, 2)), 16L);
					
					player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100);
					DustOptions dustOptions = new DustOptions(Color.BLACK, 1);
					player.spawnParticle(Particle.REDSTONE, player.getLocation().add(new Vector(0, 1, 0)), 100, dustOptions);
					player.spawnParticle(Particle.REDSTONE, player.getLocation().add(new Vector(0, 2, 0)), 100, dustOptions);

					plugin.untransform(player);
				}
			}
		}

		event.setCancelled(true);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event)
	{
		if (Werewolf.getWerewolfManager().isWolfForm(event.getPlayer().getUniqueId()))
		{
			plugin.disguiseWerewolf(event.getPlayer());
		}
	}
}
