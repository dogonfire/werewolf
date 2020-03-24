package com.dogonfire.werewolf.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.dogonfire.werewolf.Werewolf;

import eu.blackfire62.myskin.bukkit.MySkin;
import eu.blackfire62.myskin.shared.SkinProperty;
import eu.blackfire62.myskin.shared.util.MojangAPI;
import eu.blackfire62.myskin.shared.util.MojangAPIException;

public class MySkinFactory implements IWerewolfDisguiseFactory
{	
	private MySkin 		mySkin;
		
	public class MySkinWerewolfDisguise extends WerewolfDisguise
	{
		private SkinProperty disguiseSkinProperty;
		
		public MySkinWerewolfDisguise(UUID disguiseAccountId, String disguiseAccountName)
		{
			super(disguiseAccountId, disguiseAccountName);

			// Load the Disguise skin
			disguiseSkinProperty = mySkin.getCache().loadSkinProperty(getSkinAccountUUID());
		}		
		
		public boolean disguise(Player player, String werewolfName)
		{
			Werewolf.instance().logDebug("Disguising player " + player.getName() + " using MySkin...");

			if(!Werewolf.instance().getServer().getPluginManager().getPlugin("MySkin").isEnabled())
			{
				Werewolf.instance().logDebug("Didn't disguise player... MySkin is not enabled!");
				return false;	
			}

			try
			{
				// Load the Disguise skin from the cache, and if it returns null, load the skin from the MojangAPI and put it in the cache
				Werewolf.instance().logDebug("[MySkin] Started Disguising of player: " + player.getName());
				Werewolf.instance().logDebug("Skinaccountuuid: " + getSkinAccountUUID());
				disguiseSkinProperty = mySkin.getCache().loadSkinProperty(getSkinAccountUUID());
				if (disguiseSkinProperty == null)
				{
					// TODO Remove the SkinTexture parts and move completely to MojangAPI
					Werewolf.instance().logDebug("[MySkin] Using the SkinTexture, since the cached skin returned null!");
					disguiseSkinProperty = new SkinProperty(getSkinTextureValue(player), getSkinTextureSignature(player));
					if (disguiseSkinProperty == null)
					{
						Werewolf.instance().logDebug("[MySkin] Using the MojangAPI, since the newly loaded disguiseSkinProperty returned null!");
						disguiseSkinProperty = MojangAPI.getSkinProperty(getSkinAccountUUID());
						if (disguiseSkinProperty == null)
						{
							Werewolf.instance().log("[MySkin] Tried Disguising as a non-cached werewolf! Disguise failed!");
							return false;
						}
						else
						{
							mySkin.getCache().saveSkinProperty(getSkinAccountUUID(), disguiseSkinProperty);
						}
					}
					else
					{
						mySkin.getCache().saveSkinProperty(getSkinAccountUUID(), disguiseSkinProperty);
					}
				}

				// Get the SkinProperty for the current player and save the player's skin to the cache
				SkinProperty playerSkinProperty = mySkin.getHandler().getSkinProperty(player);
				mySkin.getCache().saveSkinProperty(player.getUniqueId(), playerSkinProperty);

				// Apply the loaded SkinProperty to the player
				mySkin.getHandler().setSkinProperty(player, disguiseSkinProperty);
				mySkin.getHandler().update(player);
				Werewolf.getWerewolfScoreboardManager().showNametagForPlayer(player, false);

				return true;
			}
			catch (NoClassDefFoundError e)
			{
				e.printStackTrace();
				Werewolf.instance().logDebug("Couldn't disguise player... MySkin not found!");
			}
			catch (MojangAPIException e)
			{
				e.printStackTrace();
				Werewolf.instance().log("[ERROR] Couldn't disguise player... MojangAPI Error, check the logs!");
			}
			
			return false;
		}
		
		public boolean undisguise(Player player)
		{
			if(!Werewolf.instance().getServer().getPluginManager().getPlugin("MySkin").isEnabled())
			{
				Werewolf.instance().logDebug("Didn't undisguise player... MySkin not enabled!");
				return false;	
			}

			try
			{
				Werewolf.instance().logDebug("[MySkin] Undisguise - Start undisguise!");

				// Load the Player's original skin from the cache
				SkinProperty playerSkinProperty = mySkin.getCache().loadSkinProperty(player.getUniqueId());
				if (playerSkinProperty == null)
				{
					Werewolf.instance().logDebug("[MySkin] Could not fetch Player's original skin from cache, trying to get from MojangAPI");
					playerSkinProperty = MojangAPI.getSkinProperty(player.getUniqueId());
					if (playerSkinProperty == null)
					{
						Werewolf.instance().log("[MySkin] Undisguise - Tried Disguising as a non-cached player! Undisguise failed!");
						return false;
					}
					Werewolf.instance().logDebug("[MySkin] Sucessfully fetched the player's original skin from MojangAPI");
				}

				// Apply the Player's old skin back
				mySkin.getHandler().setSkinProperty(player, playerSkinProperty);
				mySkin.getHandler().update(player);
				mySkin.getCache().resetSkinOfPlayer(player.getUniqueId());
				Werewolf.getWerewolfScoreboardManager().showNametagForPlayer(player, true);
				
				return true;
			}
			catch (NoClassDefFoundError e)
			{
				e.printStackTrace();
				Werewolf.instance().logDebug("Couldn't undisguise player... MySkin not found!");
			}
			catch (MojangAPIException e)
			{
				e.printStackTrace();
				Werewolf.instance().log("[ERROR] Couldn't undisguise player... MojangAPI Error, check the logs!");
			}
			return false;
		}
	}	
	
	
	public MySkinFactory()
	{
		this.mySkin = (MySkin) Bukkit.getPluginManager().getPlugin("MySkin");

		try
		{
			mySkin.getCache().saveSkinOfPlayer(UUID.fromString(Werewolf.instance().potionAccountUUID), UUID.fromString(Werewolf.instance().potionAccountUUID));
			mySkin.getCache().saveSkinOfPlayer(UUID.fromString(Werewolf.instance().werewolfBiteAccountUUID), UUID.fromString(Werewolf.instance().werewolfBiteAccountUUID));
			mySkin.getCache().saveSkinOfPlayer(UUID.fromString(Werewolf.instance().wildBiteAccountUUID), UUID.fromString(Werewolf.instance().wildBiteAccountUUID));
			mySkin.getCache().saveSkinOfPlayer(UUID.fromString(Werewolf.instance().alphaAccountUUID), UUID.fromString(Werewolf.instance().alphaAccountUUID));
		}
		catch (NullPointerException e)
		{
			Werewolf.instance().log("[ERROR] Couldn't save Werewolf skins to MySkin's cache... Werewolves are disabled!");
			Werewolf.instance().onDisable();
		}
	}
	
	@Override
	public WerewolfDisguise newDisguise(UUID disguiseAccountId, String disguiseAccountName)
	{		
		return new MySkinWerewolfDisguise(disguiseAccountId, disguiseAccountName);
	}	
}