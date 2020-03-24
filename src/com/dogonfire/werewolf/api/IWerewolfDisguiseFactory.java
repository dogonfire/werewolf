package com.dogonfire.werewolf.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.dogonfire.werewolf.Werewolf;
import com.dogonfire.werewolf.ClanManager.ClanType;


// Let's abstract disguise commands away, so that we don't depend on a particular Disguise plugin
public interface IWerewolfDisguiseFactory
{	
	public abstract class WerewolfDisguise
	{
		String	werewolfName;
		UUID	accountId;
		String	accountName;

		public WerewolfDisguise(UUID accountId, String accountName)
		{
			Werewolf.instance().logDebug("Constructer Start! - Werewolfname: " + werewolfName + " - AccountId: " + accountId + " - AccountName: " + accountName);
			if (werewolfName == null || werewolfName.isEmpty())
			{
				werewolfName = "Werewolf";
			}

			if (accountId == null)
			{
				// Account called Werewolf. No clan has this skin...
				accountId = UUID.fromString("c21f069b-137c-4992-8edc-e1a12995ff0f");
			}

			if (werewolfName == null || accountName.isEmpty())
			{
				// Account called Werewolf. No clan has this skin...
				accountName = "Werewolf";
			}

			this.accountId = accountId;
			this.accountName = accountName;
		}

		public WerewolfDisguise(String werewolfName, String accountName)
		{
			if (werewolfName == null || werewolfName.isEmpty())
			{
				werewolfName = "Werewolf";
			}

			if (werewolfName == null || accountName.isEmpty())
			{
				// Account called Werewolf. No clan has this skin...
				accountName = "Werewolf";
				this.accountId = UUID.fromString("c21f069b-137c-4992-8edc-e1a12995ff0f");
			}
			else
			{
				// TODO create a way to load the correct UUID's from
				// Werewolf.java, if even possible...
				switch (accountName)
				{
				case "WerewolfAlpha":
					this.accountId = UUID.fromString("e0d074bd-6722-47fc-95d3-f28e2899e155");
					break;
				case "WF_Werewolf":
					this.accountId = UUID.fromString("c61647e5-fc2f-4536-abe9-c851911ad22f");
					break;
				case "SM_Werewolf":
					this.accountId = UUID.fromString("b68a8f00-7d24-4c52-b6ad-1423bfbe26ee");
					break;
				case "BM_Werewolf":
					this.accountId = UUID.fromString("da508ecc-dbd9-46c5-8095-47b91aa4ff5f");
					break;
				default:
					// Account called Werewolf. No clan has this skin...
					this.accountId = UUID.fromString("c21f069b-137c-4992-8edc-e1a12995ff0f");
					break;
				}
			}

			this.werewolfName = werewolfName;
			this.accountName = accountName;
		}

		public WerewolfDisguise(String werewolfName, ClanType clanType)
		{
			if (werewolfName == null || werewolfName.isEmpty())
			{
				werewolfName = "Werewolf";
			}

			if (clanType == null)
			{
				// Account called Werewolf. No clan has this skin...
				this.accountName = "Werewolf";
				this.accountId = UUID.fromString("c21f069b-137c-4992-8edc-e1a12995ff0f");
			}
			else
			{
				this.accountName = Werewolf.getClanManager().getWerewolfAccountForClan(clanType);
				this.accountId = Werewolf.getClanManager().getWerewolfAccountIdForClan(clanType);
			}

			this.werewolfName = werewolfName;
		}

		public String getWerewolfName()
		{
			return werewolfName;
		}

		public String getSkinAccountName()
		{
			return accountName;
		}

		public String getSkinTextureValue(Player player)
		{
			if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
			{
				return Werewolf.getClanManager().getWerewolfTextureForAlpha(Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId()));
			}

			if (Werewolf.getWerewolfManager().isWerewolf(player.getUniqueId()) && Werewolf.instance().useClans)
			{
				return Werewolf.getClanManager().getWerewolfTextureForClan(Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId()));
			}
			return "eyJ0aW1lc3RhbXAiOjE1NjUxMjM3NTE5NDEsInByb2ZpbGVJZCI6IjQ1NjZlNjlmYzkwNzQ4ZWU4ZDcxZDdiYTVhYTAwZDIwIiwicHJvZmlsZU5hbWUiOiJUaGlua29mZGVhdGgiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRkMWUwOGIwYmI3ZTlmNTkwYWYyNzc1ODEyNWJiZWQxNzc4YWM2Y2VmNzI5YWVkZmNiOTYxM2U5OTExYWU3NSJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBjYzA4ODQwNzAwNDQ3MzIyZDk1M2EwMmI5NjVmMWQ2NWExM2E2MDNiZjY0YjE3YzgwM2MyMTQ0NmZlMTYzNSJ9fX0=";
		}

		public String getSkinTextureSignature(Player player)
		{
			if (Werewolf.getClanManager().isAlpha(player.getUniqueId()))
			{
				return Werewolf.getClanManager().getWerewolfTextureSignatureForAlpha(Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId()));
			}

			if (Werewolf.getWerewolfManager().isWerewolf(player.getUniqueId()) && Werewolf.instance().useClans)
			{
				return Werewolf.getClanManager().getWerewolfTextureSignatureForClan(Werewolf.getWerewolfManager().getWerewolfClan(player.getUniqueId()));
			}
			return "ef4Zb4mm9alUQUjk28gO85/Rug8DFYPyVAjT1hq+csjw23FVpCIQPvl3Bc4/zcDGIUiGGX/hi9G1U6LMcGyQg/4ONbMLFZtB7P1i9Dak/sIqZstMuBnWblK0//cdnFwEpZh+psdvsZUHOq6k37omwwc1wboIsqbe6r+23EZzZ9fBCWnk3qiYoWK+CMMhSJrhiamYIxjoMafJWeIkKhCUWiKrDJQw47NVnVTeEG4B8t4pRKs4L1wMsUavRgKQpWD9Lb/ivE0mNhQrLFT0IxEu7v5+e47OaEWhyL8OZ8/hcY/D7+Mf86M4jOP1AcKsvh4KdklHHZyrwSlGFcHKMvA6q1BLdj2BT4DlsqGF9eTv8aGwwXHxTi5L5JzeIwaE/6wZebolNWvZs/aXop2gMEn9yBFju82KeB89yrC75vUxHzH2zT6393/BV6le6m3mWRFAMJfXzLVSUaOK8CgBGnEOkc1Gy+LSXLayCgvBPzF0Rn0m0elIf/h4NV4pPhCe6Prh0lFKVpc5u2keihOhm7LpweBOBq8e4JN7eOUmojVKkAvAXWXYfAqjg49M0Uau3s+UeNqt+REcB9Di7J1++GKKtBBErJDChU6jt+kmkMJ+7frbIEV/1U2TA7FMIYQbvvZR3JJj9W9xI2L0GYadO/sAa1uYxjHiOTa12Jy4k6meobw=";
		}

		public UUID getSkinAccountUUID()
		{
			return accountId;
		}

		public void setWerewolfName(String customName)
		{
			werewolfName = customName;
		}

		public void setSkinAccountName(String skinAccountname)
		{
			accountName = skinAccountname;
		}

		public void setSkinAccountUUID(UUID skinAccountId)
		{
			accountId = skinAccountId;
		}

		public void setSkinAccountUUID(String skinAccountId)
		{
			accountId = UUID.fromString(skinAccountId);
		}
		
		public abstract boolean disguise(Player player, String werewolfName);

		public abstract boolean undisguise(Player player);
	}

	public WerewolfDisguise newDisguise(UUID disguiseAccountId, String disguiseAccountName);		
} 
