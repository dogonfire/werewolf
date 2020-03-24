package com.dogonfire.werewolf;

import java.util.UUID;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.dogonfire.werewolf.Werewolf;

/* From https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/PlaceholderExpansion#internal-class */

class WerewolfPlaceholderExpansion extends PlaceholderExpansion
{
	private Werewolf plugin;

	WerewolfPlaceholderExpansion(Werewolf plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean persist()
	{
		return true;
	}

	@Override
	public boolean canRegister()
	{
		return true;
	}

	@Override
	public String getAuthor()
	{
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier()
	{
		return "werewolf";
	}

	@Override
	public String getVersion()
	{
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier)
	{

		if (player == null)
		{
			return "";
		}

		// %werewolf_clan%
		if ("clan".equals(identifier))
		{
			UUID playerUUID = player.getUniqueId();
			String clan = "";
			if (Werewolf.getClanManager().getClanName(playerUUID) != null)
			{
				clan = Werewolf.getClanManager().getClanName(playerUUID);
			}
			return clan;
		}

		// %werewolf_werewolf%
		if ("werewolf".equals(identifier))
		{
			String isWerewolf = "";
			if (Werewolf.getWerewolfManager().isWerewolf(player))
			{
				isWerewolf = "Werewolf";
			}
			return isWerewolf;
		}

		// We return null if an invalid placeholder (f.e. %werewolf_fafafaf%)
		// was provided
		return null;
	}
}