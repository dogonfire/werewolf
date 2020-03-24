package com.dogonfire.werewolf;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PacketUtils
{

	PacketUtils()
	{
	}

	
	public void addPotionEffectNoGraphic(Player player, PotionEffect pe)
	{
		short duration = 32767;
		if (pe.getDuration() < 32767)
		{
			duration = (short) pe.getDuration();
		}
		PotionEffect peNoEffect = new PotionEffect(pe.getType(), duration, pe.getAmplifier(), pe.isAmbient(), false);
		
		player.addPotionEffect(peNoEffect);
	}

	public void removePotionEffectNoGraphic(Player player, PotionEffectType pe)
	{
		player.removePotionEffect(pe);
	}
}
