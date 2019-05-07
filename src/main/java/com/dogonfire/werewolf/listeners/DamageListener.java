package com.dogonfire.werewolf.listeners;

import java.util.Random;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.DamageManager;
import com.dogonfire.werewolf.LanguageManager;
import com.dogonfire.werewolf.Werewolf;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class DamageListener implements Listener
{

	private Werewolf		plugin;

	public DamageListener(Werewolf plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamage1(EntityDamageEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		
		if (!event.isCancelled())
		{
			if (event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				
				if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
				{
					PlayerDisguise skin = Werewolf.getSkinManager().getSkin(player);
					if (skin != null)
					{
						Werewolf.getWerewolfManager().setPouncing(player.getUniqueId());
					}
					else
					{
						this.plugin.logDebug("onEntityDamage: Skin is null for " + player.getName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage2(EntityDamageEvent event)
	{
		if (!Werewolf.pluginEnabled)
		{
			return;
		}
		
		if (event.getEntity() instanceof Player)
		{
			Player werewolfPlayer = (Player) event.getEntity();
			
			if (Werewolf.getWerewolfManager().hasWerewolfSkin(werewolfPlayer.getUniqueId()))
			{
				double damage = event.getDamage();
				switch (event.getCause())
				{
					case FALL: damage = 0; 
						break;
					case CONTACT:
					case LAVA:
					case LIGHTNING:
					case BLOCK_EXPLOSION :
					case ENTITY_ATTACK:
						damage *= DamageManager.SilverArmorMultiplier;
						break;
						
					default:
						break;
				}
				
				if (Werewolf.getClanManager().isAlpha(werewolfPlayer.getUniqueId()))
				{
					damage /= 2.0D;
				}
				
				event.setDamage(damage);
				
				if (damage == 0.0D)
				{
					event.setCancelled(true);
					return;
				}
				
				PlayerDisguise skin = Werewolf.getSkinManager().getSkin(werewolfPlayer);
				
				if(skin!=null)
				{
					werewolfPlayer.playEffect(EntityEffect.HURT);

					Werewolf.getWerewolfManager().growl(werewolfPlayer);
				}
			}
		}
		
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
			if (damageEvent.getDamager() instanceof Player)
			{
				Player player = (Player) damageEvent.getDamager();

				this.plugin.logDebug(player.getName() + " is doing damage to " + damageEvent.getEntityType().name());
				
				if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()))
				{
					double damage = 0.0D;
					
					if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
					{
						damage = DamageManager.werewolfHandDamage;
					}
					else
					{
						damage = DamageManager.werewolfItemDamage;
					}
					
					if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
					{
						damage *= 2.0D;
					}
					
					event.setDamage(damage);
				}
			}
			
			if (event.getEntity() != null && (event.getEntity() instanceof Player))
			{
				Player player = (Player) event.getEntity();
				if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getUniqueId()) && (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.becomeinfected") || player.isOp()))
				{
					if (!Werewolf.getWerewolfManager().isWerewolf(player) && (damageEvent.getDamager() instanceof Wolf) && Math.random() < this.plugin.wildWolfInfectionRisk)
					{
						Tameable tameable = (Tameable)damageEvent.getDamager();
						
						if(!tameable.isTamed())
						{
							Random random = new Random();
							Werewolf.getWerewolfManager().makeWerewolf(player, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);

							player.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BiteVictim, ChatColor.LIGHT_PURPLE));
						
							plugin.log(player.getName() + " contracted the werewolf infection from a wild wolf!");
						}
					}
				}
			}
		}
		
		if (event.getDamage() <= 0.0D)
		{
			event.setDamage(1.0D);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (!this.plugin.vaultEnabled)
		{
			return;
		}
		if (event.getEntity().getKiller() == null)
		{
			return;
		}
		if (!(event.getEntity().getKiller() instanceof Player))
		{
			return;
		}
		
		Player killer = event.getEntity().getKiller();
		
		if (Werewolf.getWerewolfManager().hasWerewolfSkin(killer.getUniqueId()))
		{
			int health = 0;
			switch (event.getEntity().getType())
			{
				case PLAYER:
					health = 4;
					break;
				case HORSE:
					health = 4;
					break;
				case COW:
					health = 4;
					break;
				case ENDERMAN:
					health = 3;
					break;
				case ZOMBIE:
					health = 3;
					break;
				case PIG_ZOMBIE:
					health = 3;
					break;
				case SHEEP:
					health = 2;
					break;
				case OCELOT:
					health = 2;
					break;
				case RABBIT:
					health = 2;
					break;
				case PIG:
					health = 2;
					break;
				case WOLF:
					health = 2;
					break;
				case SPIDER:
					health = 2;
					break;
				case SKELETON:
					health = 1;
					break;
				case CHICKEN:
					health = 1;
					break;
				case SILVERFISH:
					health = 1;
					break;
				default:
					break;
			}
			
			if (health > 0 && killer.getHealth() < killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue())
			{
				Werewolf.getLanguageManager().setAmount("" + health);
				killer.sendMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledMob, ChatColor.LIGHT_PURPLE));
				
				if (killer.getHealth() + health > killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue())
				{
					killer.setHealth(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
				}
				else
				{
					killer.setHealth(killer.getHealth() + health);
				}
			}
			
			if (this.plugin.useClans)
			{
				ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(killer.getUniqueId());
				Werewolf.getClanManager().handleMobKill(killer, clan, event.getEntity().getType());
			}
			
			if (event.getEntity() instanceof Player)
			{
				
				Werewolf.getWerewolfScoreboardManager().setHumanKillsForPlayer(killer, Werewolf.getStatisticsManager().increaseHumanKills(killer.getUniqueId()));
			}
			else
			{
				Werewolf.getWerewolfScoreboardManager().setMobKillsForPlayer(killer, Werewolf.getStatisticsManager().increaseMobKills(killer.getUniqueId()));				
			}
		}
		
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		Player werewolf = (Player) event.getEntity();
		
		if (!Werewolf.getWerewolfManager().hasWerewolfSkin(werewolf.getUniqueId()))
		{
			return;
		}
		
		if (this.plugin.vaultEnabled)
		{
			if (!Werewolf.getWerewolfManager().hasWerewolfSkin(killer.getUniqueId()))
			{
				Werewolf.getHuntManager().handleKill(killer.getUniqueId(), killer.getName());
			}
		}
	}
}