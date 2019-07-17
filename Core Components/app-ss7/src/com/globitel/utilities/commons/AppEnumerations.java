package com.globitel.utilities.commons;

public class AppEnumerations {
	public enum NumberingPlan {
		Unknown((byte) 0x00), ISDN_Telephony((byte) 0x01), Generic((byte) 0x02), Data((byte) 0x03), Telex((byte) 0x04),
		Maritime_Mobile((byte) 0x05), Land_Mobile((byte) 0x06), ISDN_Mobile((byte) 0x07);

		byte numberingPlan;

		private NumberingPlan(byte numberingPlan) {
			this.numberingPlan = numberingPlan;
		}

		public byte get() {
			return this.numberingPlan;
		}
	}

	public enum SubSystem {
		HLR((byte) 0x06), VLR((byte) 0x07), MSC((byte) 0x08), EIR((byte) 0x09), CAP((byte) 0x92), SCF((byte) 0x93),
		SGSN((byte) 0x95), GGSN((byte) 0x96), SIN((byte) 0xf1);

		byte subSystem;

		private SubSystem(byte subSystem) {
			this.subSystem = subSystem;
		}

		public byte get() {
			return this.subSystem;
		}
	}

	public enum AddressIndicator {
		RouteOnGlobalTitle((byte) 0x12), RouteOnSubSystemNumber((byte) 0x07);

		byte addressIndicator;

		private AddressIndicator(byte addressIndicator) {
			this.addressIndicator = addressIndicator;
		}

		public byte get() {
			return this.addressIndicator;
		}
	}

	public enum NatureOfAddress {
		Unknown((byte) 0x00), SubscriberNumber((byte) 0x01), UnknownNationalUse((byte) 0x02),
		NationalNumber((byte) 0x03), InternationalNumber((byte) 0x04), NetworkSpecificNumber((byte) 0x05);

		byte natureOfAddress;

		private NatureOfAddress(byte natureOfAddress) {
			this.natureOfAddress = natureOfAddress;
		}

		public byte get() {
			return this.natureOfAddress;
		}
	}

	public enum LocationEstimateType {
		CurrentLocation((byte) 0x00), CurrentOrLastKnownLocation((byte) 0x01), InitialLocation((byte) 0x02);

		private byte locationEstimateType;

		private LocationEstimateType(byte locationEstimateType) {
			this.locationEstimateType = locationEstimateType;
		}

		public byte get() {
			return this.locationEstimateType;
		}

	}

	public enum TRANSACTION_STATUS {		
		TRYING_TO_GET_LOCATION((long)Math.pow(2, 0)), SEND_SRIForLCS((long)Math.pow(2, 1)), RCV_SRIForLCS_ACK((long)Math.pow(2, 2)),
		SEND_PSL((long)Math.pow(2, 3)), RCV_PSL_ACK((long)Math.pow(2, 4)), SEND_RIR((long)Math.pow(2, 5)), RCV_RIA((long)Math.pow(2, 6)),
		RCV_RIA_SUCCESS((long)Math.pow(2, 7)), RCV_RIA_WITH_MME((long)Math.pow(2, 8)), SEND_PLR((long)Math.pow(2, 9)),
		RCV_PLA((long)Math.pow(2, 10)), RCV_PLA_SUCCESS((long)Math.pow(2, 11)), RCV_PLA_WITH_ECGI((long)Math.pow(2, 12));
		private long status;
		private TRANSACTION_STATUS(long _status) {
			this.status = _status;
		}
		public long get() {
			return this.status;
		}		
		
	}

	public enum RIR_ROUTING_MODES {
		RIR_HLR_PC_ROUTING(1), RIR_IMSI_GT_ROUTING(2), RIR_HLR_SSN_ROUTING(3);

		private int number;

		private RIR_ROUTING_MODES(int number) {
			this.number = number;
		}

		public int get() {
			return this.number;
		}
	}

}