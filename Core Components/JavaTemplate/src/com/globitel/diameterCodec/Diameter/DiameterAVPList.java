package com.globitel.diameterCodec.Diameter;


import java.util.LinkedList;
import java.util.List;
public class DiameterAVPList
{
	// Map<Integer,AVP> list = new LinkedHashMap<>();
	public List<AVP> list = new LinkedList<AVP>();

	public void add(AVP pCreatedAVP)
	{
		list.add(pCreatedAVP);
	}

	@Override
	public void finalize()
	{
		try
		{
			// TODO Auto-generated method stub
			list.clear();
			list = null;

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

	int find(int code)
	{
		for (int i = 0; i < list.size(); i++)
		{
			AVP item = list.get(i);
			if (item.getAvpCode() == code)
			{
				return i;
			}
		}

		return -1;
	}

	AVP searchItem(int code)
	{
		for (int i = 0; i < list.size(); i++)
		{
			AVP item = list.get(i);
			if (item.getAvpCode() == code)
			{
				return item;
			}
		}
		return null;
	}

	public int getNumberOfItems()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	public AVP GetItem(int index)
	{
		// return (AVP) list.values().toArray()[index];
		return list.get(index);
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	public void remove(AVP avp_tmp)
	{
		list.remove(avp_tmp);
	}

	public void remove(int avpCode)
	{
		AVP avpToRemove = null;
		for (AVP e : list)
		{
			if (e.getAvpCode() == avpCode)
			{
				avpToRemove = e;
			}
		}
		if (avpToRemove != null)
		{
			list.remove(avpToRemove);
		}
	}

	public void clear()
	{
		// TODO Auto-generated method stub
		list.clear();
	}
}
