package com.dogonfire.werewolf;

import java.util.HashMap;
import java.util.UUID;


public class StatisticsManager
{
	private HashMap<UUID, WerewolfStatistics> werewolfStatistics = new HashMap<UUID, WerewolfStatistics>();

	public class WerewolfStatistics
	{
		public int numberOfMobKills;
		public int numberOfHumanKills;
		public int numberOfVampireKills;
		
		WerewolfStatistics()
		{}
	}
		
	StatisticsManager()
	{	
	}

	public void clearStatistics(UUID playerId)
	{
		werewolfStatistics.remove(playerId);
	}
	
	public int increaseHumanKills(UUID playerId)
	{
		if(!werewolfStatistics.containsKey(playerId))
		{
			werewolfStatistics.put(playerId, new WerewolfStatistics());
		}
		
		WerewolfStatistics statistics = werewolfStatistics.get(playerId);
		
		statistics.numberOfHumanKills++;

		werewolfStatistics.put(playerId, statistics);
		
		return statistics.numberOfHumanKills;
	}	
	
	public int increaseMobKills(UUID playerId)
	{
		if(!werewolfStatistics.containsKey(playerId))
		{
			werewolfStatistics.put(playerId, new WerewolfStatistics());
		}
		
		WerewolfStatistics statistics = werewolfStatistics.get(playerId);
		
		statistics.numberOfMobKills++;

		werewolfStatistics.put(playerId, statistics);
		
		return statistics.numberOfMobKills;
	}	
	
	public int increaseVampireKills(UUID playerId)
	{
		if(!werewolfStatistics.containsKey(playerId))
		{
			werewolfStatistics.put(playerId, new WerewolfStatistics());
		}
		
		WerewolfStatistics statistics = werewolfStatistics.get(playerId);
		
		statistics.numberOfVampireKills++;

		werewolfStatistics.put(playerId, statistics);
		
		return statistics.numberOfVampireKills;
	}	
}
