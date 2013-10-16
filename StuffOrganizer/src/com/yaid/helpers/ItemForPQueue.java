package com.yaid.helpers;

public class ItemForPQueue implements Comparable<ItemForPQueue>{
	
	private String key;
	private long value;
	
	public ItemForPQueue(String key, long value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(ItemForPQueue another) {
		//return Long.valueOf(value).compareTo(another.value);
		return Long.valueOf(another.value).compareTo(value);
	}
	
	@Override
	public boolean equals(Object another)
	{
		if (another instanceof  ItemForPQueue)
			return key.equals(((ItemForPQueue)another).key);
		else
			return false;
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}
	
	public String getKey(){
		return key;
	}

}
