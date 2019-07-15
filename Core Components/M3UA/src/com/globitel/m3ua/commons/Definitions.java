package com.globitel.m3ua.commons;


public class Definitions {

	public static class MTP3Data
	{
		public byte serviceInfo;
		public int serviceInfo_ni;
		public int serviceInfo_si;
		public int sls;
		public int opc;
		public int dpc;
		
		public MTP3Data(byte serviceInfo, int serviceInfo_ni, int serviceInfo_si, int sls, int opc, int dpc)
		{
			this.serviceInfo = serviceInfo;
			this.serviceInfo_ni = serviceInfo_ni;
			this.serviceInfo_si = serviceInfo_si;
			this.sls = sls;
			this.opc = opc;
			this.dpc = dpc;
		}
	}
}
