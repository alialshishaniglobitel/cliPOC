package com.globitel.Application;

import java.util.Date;

import com.globitel.Application.Program;

import com.globitel.common.utils.Common;
import com.globitel.utilities.commons.AppEnumerations.TRANSACTION_STATUS;
import com.globitel.utilities.commons.logger.MyLoggerFactory;
import com.globitel.xmlrpc.XmlRPCRequestImp;

public class CDR {

	public Date _date = new Date();
	public long returnValue = TRANSACTION_STATUS.TRYING_TO_GET_LOCATION.get();
	public byte[] _tid;
	public String msisdn = "";
	public String networkNodeNumber = "";
	public String imsi = "";
	public String typeOfShape = "";
	public String latitude = "";
	public String longitude = "";
	public String cellsac = "";
	public String servingNode = "";
	public String additionalServingNode = "";
	public _4GLocInfo _4GlocInfo = new _4GLocInfo();

	public CDR(byte[] tid) {
		_tid = tid;		
	}

	public void ReportCDR() {
		MyLoggerFactory.getInstance().getAppLogger().info(String.format(
				"reporting (writing cdr)  Return Value: [%s], TID: [%s], msisdn: [%s], networkNodeNumber: [%s], imsi: [%s], typeOfShape: [%s], latitude: [%s], longitude: [%s], cellsac: [%s], servingNode: [%s], additionalServingNode: [%s], locationEstimation: [%s], accuracyFulfilmentIndicator: [%s], eutranPositioningData: [%s], ecgi: [%s], ecgi.mcc: [%s], ecgi.mnc: [%s], ecgi.cellID: [%s], ecgi.plmnId: [%s]",
				returnValue, Common.byteArrayToString(_tid), msisdn, networkNodeNumber, imsi, typeOfShape, latitude,
				longitude, cellsac, servingNode, additionalServingNode, _4GlocInfo.locationEstimation, _4GlocInfo.accuracyFulfilmentIndicator, _4GlocInfo.eutranPositioningData, _4GlocInfo.ecgi, _4GlocInfo.mcc, _4GlocInfo.mnc, _4GlocInfo.cellID, _4GlocInfo.plmnId));

		StringBuilder strBuilder = new StringBuilder();

		_date = new Date();

		// Current Date
		strBuilder.append(Common.convertJavaDateToMySQLDate(_date) + ",");

		strBuilder.append(returnValue + ",");

		strBuilder.append(Common.byteArrayToString(_tid) + ",");

		strBuilder.append(msisdn + ",");

		strBuilder.append(networkNodeNumber + ",");

		strBuilder.append(imsi + ",");

		strBuilder.append(typeOfShape + ",");

		strBuilder.append(latitude + ",");

		strBuilder.append(longitude + ",");

		strBuilder.append(cellsac + ",");

		strBuilder.append(servingNode + ",");

		strBuilder.append(additionalServingNode + ",");

		strBuilder.append(_4GlocInfo.locationEstimation + ",");

		strBuilder.append(_4GlocInfo.accuracyFulfilmentIndicator + ",");

		strBuilder.append(_4GlocInfo.eutranPositioningData + ",");

		strBuilder.append(_4GlocInfo.ecgi + ",");

		strBuilder.append(_4GlocInfo.mcc + ",");

		strBuilder.append(_4GlocInfo.mnc + ",");

		strBuilder.append(_4GlocInfo.cellID + ",");

		strBuilder.append(_4GlocInfo.plmnId);

		Program.tdrLogger.logDataRecord(strBuilder.toString());
		XmlRPCRequestImp.getInstance().concurrentHashMap.replace(Common.byteArrayToString(_tid), this);
	}

	public class _4GLocInfo {
		public String locationEstimation = "";
		public String accuracyFulfilmentIndicator = "";
		public String eutranPositioningData = "";
		public String ecgi = "";
		public String mcc = "";
		public String mnc = "";
		public int cellID = 0;
		public long plmnId = 0;		
		public _4GLocInfo() {
		}
		
		public void decode(String _locationEstimation, String _accuracyFulfilmentIndicator, String _eutranPositioningData, String _ecgi) {
			locationEstimation = _locationEstimation;
			accuracyFulfilmentIndicator = _accuracyFulfilmentIndicator;
			eutranPositioningData = _eutranPositioningData;
			ecgi = _ecgi;
			// TODO Auto-generated method stub
			long inputInHex = Long.parseLong(_ecgi, 16);
			plmnId = (inputInHex >> 8 + 8 + 8 + 8);
			String plmnIdStr = Long.toHexString(plmnId);
			String newStr = "";
			for (int i = 0; i < plmnIdStr.length(); i += 2) {
				if (i + 1 < plmnIdStr.length()) {
					char digit2 = plmnIdStr.charAt(i + 1);
					newStr += Character.toString(digit2);
				}
				char digit = plmnIdStr.charAt(i);
				newStr += Character.toString(digit);
			}
			String arr[] = newStr.split("[f,F]");

			mcc = arr[0];
			mnc = arr[1];
			cellID = (int) (inputInHex & 0x00000000000ffl);
		}

	}
}
