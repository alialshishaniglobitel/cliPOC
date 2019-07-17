package com.globitel.diameterCodec.Diameter;

import java.util.HashMap;
import java.util.Map;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class DiameterMessageStatus
{
	private int counter;
	private boolean answer;
	private boolean dropped;
	private boolean routedToRouteList;
	private boolean routedToRouteListCluster;
	private boolean routedToPeer;
	private boolean itsCalled;
	private Map<Integer, Integer> avpMap = new HashMap<Integer, Integer>();
	private boolean skipThisRule;
	private boolean filtered;

	@Override
	public void finalize()
	{
		try
		{
			avpMap.clear();
			avpMap = null;

		}
		finally
		{
			try
			{
				super.finalize();
			}
			catch (Throwable e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void ruleCalled()
	{
		counter = ++counter % Integer.MAX_VALUE;
	}

	public boolean isItsCalled()
	{
		return itsCalled;
	}

	public void setItsCalled(boolean itsCalled)
	{
		this.itsCalled = itsCalled;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag ITS called now." + itsCalled);
	}

	public boolean hasAnswer()
	{
		// TODO Auto-generated method stub
		return answer;
	}

	public void answerCreated()
	{
		// TODO Auto-generated method stub
		answer = true;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag answer created now.");
	}

	public boolean isDropped()
	{
		// TODO Auto-generated method stub
		return dropped;
	}

	public void setDropped()
	{
		// TODO Auto-generated method stub
		dropped = true;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag dropped now.");
	}

	public boolean isRoutedToRouteList()
	{
		return routedToRouteList;
	}
	public boolean isRoutedToRouteListCluster()
	{
		return routedToRouteListCluster;
	}
	public void setRoutedToRouteList(boolean routedToRouteList)
	{
		this.routedToRouteList = routedToRouteList;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag routed to route list." + routedToRouteList);
	}
	public void setRoutedToRouteListCluster(boolean routedToRouteListCluster)
	{
		this.routedToRouteListCluster = routedToRouteListCluster;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag routed to route list Cluster" + routedToRouteListCluster);
	}

	public boolean isRoutedToPeer()
	{
		return routedToPeer;
	}

	public void setRoutedToPeer(boolean routedToPeer)
	{
		this.routedToPeer = routedToPeer;
		MyLoggerFactory.getInstance().getAppLogger().debug("set flag routed to peer now." + routedToPeer);
	}

	public boolean isAvpAccessed(int i)
	{
		// TODO Auto-generated method stub
		return avpMap.get(i) != null;
	}

	public void avpAccessed(int avpCode)
	{
		Integer value = avpMap.get(avpCode);
		if (value == null)
			value = new Integer(1);
		else
			value++;

		avpMap.put(avpCode, value);
	}

	public Integer getAvpAccessCount(int i)
	{
		// TODO Auto-generated method stub
		return avpMap.get(i);
	}

	public boolean isOnlyAccessedOnce()
	{
		return counter == 1;
	}

	public boolean isValid()
	{
		// TODO Auto-generated method stub
		boolean fail1 = hasAnswer() || isDropped() || isItsCalled() || isFiltered();
		boolean fail2 = (isRoutedToPeer() && isRoutedToRouteList()) || (isRoutedToRouteList() & isRoutedToRouteListCluster()) || (isRoutedToRouteListCluster() & isRoutedToPeer());
		boolean fail3 = false;
		for (Integer k : avpMap.keySet())
		{
			Integer val = avpMap.get(k);
			if (val != null)
			{
				if (val > 1)
				{
					fail3 = true;
					break;
				}
			}
		}
		boolean result = !((fail1) || (fail2) || (fail3));
		MyLoggerFactory.getInstance().getAppLogger().debug("is message valid: " + result);
		return result;
	}

	public boolean isSkipThisRule()
	{
		// TODO Auto-generated method stub
		return skipThisRule;
	}

	public void setSkipThisRule(boolean b)
	{
		// TODO Auto-generated method stub
		skipThisRule = b;
	}

	public void copy(DiameterMessageStatus diameterMessageStatus)
	{
		// TODO Auto-generated method stub
		answer = diameterMessageStatus.answer;
		dropped = diameterMessageStatus.dropped;
		routedToRouteList = diameterMessageStatus.routedToRouteList;
		routedToRouteListCluster = diameterMessageStatus.routedToRouteListCluster;
		routedToPeer = diameterMessageStatus.routedToPeer;
		itsCalled = diameterMessageStatus.itsCalled;
		avpMap.putAll(diameterMessageStatus.avpMap);
		skipThisRule = diameterMessageStatus.skipThisRule;
	}

	public void reset()
	{
		// TODO Auto-generated method stub
		answer = false;
		dropped = false;
		routedToRouteList = false;
		routedToRouteListCluster = false;
		routedToPeer = false;
		itsCalled = false;
		avpMap.clear();
		skipThisRule = false;
	}

	public boolean isFiltered()
	{
		// TODO Auto-generated method stub
		return filtered;
	}

	public void filtered()
	{
		// TODO Auto-generated method stub
		filtered = true;
	}
}
