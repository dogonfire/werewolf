package com.dogonfire.werewolf;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionManager
{
  
  PotionManager()
  {
  }
  
  public ItemStack createWerewolfInfectionPotion()
  {
    ItemStack potion = new ItemStack(Material.POTION);
    
    PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
    
    potionMeta.setDisplayName(ChatColor.GOLD + "Werewolf infection potion");
    
    List<String> pages = new ArrayList<String>();
    
    pages.add(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfectionPotionDescription1, ChatColor.GRAY));
    pages.add(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfectionPotionDescription2, ChatColor.GRAY));
    
    potionMeta.setLore(pages);
    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 2), true);
    
    potion.setItemMeta(potionMeta);
    
    return potion;
  }
  
  public ItemStack createWerewolfCurePotion()
  {
    ItemStack potion = new ItemStack(Material.POTION);
    
    PotionMeta potionMeta = (PotionMeta)potion.getItemMeta();
    
    potionMeta.setDisplayName(ChatColor.GOLD + "Werewolf cure potion");
    
    List<String> pages = new ArrayList<String>();
    
    pages.add(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CurePotionDescription1, ChatColor.GRAY));
    pages.add(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CurePotionDescription2, ChatColor.GRAY));
    
    potionMeta.setLore(pages);
    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 2), true);
    
    potion.setItemMeta(potionMeta);
    
    return potion;
  }
}
