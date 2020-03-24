package com.dogonfire.werewolf;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

import com.dogonfire.werewolf.api.IWerewolfDisguiseFactory.WerewolfDisguise;
import com.dogonfire.werewolf.api.WerewolfDisguiseAPI;

public class SkinManager
{
	private Werewolf						plugin;
	private HashMap<UUID, WerewolfDisguise>	skins	= new HashMap<UUID, WerewolfDisguise>();
	protected int							nextID	= -2147483648;

	SkinManager(Werewolf p)
	{
		this.plugin = p;
	}

	public WerewolfDisguise getSkin(Player player)
	{
		return this.skins.get(player.getUniqueId());
	}

	public WerewolfDisguise getDisguise(Player player)
	{
		return this.skins.get(player.getUniqueId());
	}

	public int getNextAvailableID()
	{
		return this.nextID++;
	}

	public boolean setWerewolfSkin(Player player, String werewolfName)
	{
		if (this.skins.containsKey(player.getUniqueId()))
		{
			return true;
		}

		UUID werewolfAccountId;
		String werewolfAccountName;

		ClanManager.ClanType clantype = Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId());

		// Get the correct account, if alpha, alphaskin, else the clanskin
		if (plugin.useClans && Werewolf.getClanManager().isAlpha(player.getUniqueId()))
		{
			werewolfAccountId = Werewolf.getClanManager().getWerewolfAccountIdForAlpha(clantype);
			werewolfAccountName = Werewolf.getClanManager().getWerewolfAccountForAlpha(clantype);
		}
		else
		{
			werewolfAccountId = Werewolf.getClanManager().getWerewolfAccountIdForClan(clantype);
			werewolfAccountName = Werewolf.getClanManager().getWerewolfAccountForClan(clantype);
		}

		if (werewolfName.isEmpty())
		{
			werewolfName = "Werewolf";
		}

		WerewolfDisguise skin = WerewolfDisguiseAPI.getDisguise(werewolfAccountId, werewolfAccountName);
		
		if (skin == null)
		{
			plugin.log("WerewolfDisguise Skin is null!");
		}
		
		plugin.logDebug("Skin: " + skin.toString() + ". SkinName: " + skin.getSkinAccountName() + " - SkinUUID: " + skin.getSkinAccountUUID());
		
		if(WerewolfDisguiseAPI.disguise(player, skin, werewolfName))
		{
			plugin.logDebug("Disguised player " + player.getName() + "!");
			Werewolf.getWerewolfManager().howl(player);
		}
		else
		{
			plugin.logDebug("Could not disguise " + player.getName());
		}

		this.skins.put(player.getUniqueId(), skin);

		return true;
	}

	public void unsetWerewolfSkin(Player player)
	{
		if (!this.skins.containsKey(player.getUniqueId()))
		{
			return;
		}

		if(WerewolfDisguiseAPI.undisguise(player))
		{
			plugin.logDebug("Undisguised player " + player.getName() + "!");
		}
		else
		{
			plugin.logDebug("Could not undisguise " + player.getName());
		}

		this.skins.remove(player.getUniqueId());
	}
}
