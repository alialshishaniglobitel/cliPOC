package com.globitel.common.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class FileCreationUtilities
{
	protected String filePath;
	protected String fileName;
	protected PrintWriter pw;
	protected File f;
	public FileCreationUtilities(String _newFilePath)
	{
		// TODO Auto-generated constructor stub
		filePath=_newFilePath;
	}
	public abstract String getNewFileNameForComparing();
	public abstract String getNewFileNameForCreating();
	public void create()
	{
		f = getlastFileModified();
		if (f == null || needCreating(f)) 
		{
			fileName = getNewFileNameForCreating();
			newWriter();
		}
	}
	private boolean compare(String a, String b)
	{
		int a_index = a.lastIndexOf(".");
		int b_index = b.lastIndexOf(".");
		return a.substring(0,a_index).equals(b.substring(0,b_index));
	}
	public boolean needCreating(File f)
	{
		String newFile = getNewFileNameForComparing();
		if (compare(f.getName(),newFile)==false)
		{
			return true;
		}
		return false;
	}
	protected void newWriterIfClosed()
	{
		try
		{
			if (pw == null)
			{
				pw = new PrintWriter(new FileWriter(f, true));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception, creating writer");
		}
	}
	protected void newWriter()
	{
		try
		{
			f = new File(filePath + "/" + fileName);
			if (pw != null)
			{
				synchronized (pw)
				{
					pw.close();
					pw = new PrintWriter(new FileWriter(f, true));
				}
			}
			else
			{
				pw = new PrintWriter(new FileWriter(f, true));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception, creating writer");
		}
	}
	public File getlastFileModified()
	{
		File fl = new File(filePath);
		File[] files = fl.listFiles(new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choise = null;
		if (files != null)
		{
			for (File file : files)
			{
				if (file.lastModified() > lastMod)
				{
					choise = file;
					lastMod = file.lastModified();
				}
			}
		}
		return choise;
	}
}
