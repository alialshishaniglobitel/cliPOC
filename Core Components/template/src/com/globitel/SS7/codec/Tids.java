package com.globitel.SS7.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.globitel.common.utils.Common;
import com.globitel.utilities.commons.ConfigurationManager;

public class Tids {
//	public long otid = 0;
//	public long dtid = 0;

	public byte[] otid = new byte[4];
	public byte[] dtid = new byte[4];
	private boolean dtidExists = false;

	public boolean DoesDtidExist() {
		return dtidExists;
	}

	long GetBigEndian(byte[] data, int index, int length) {
		if (length > 8) {
			return 1;
		}

		long value = 0;
		int i = 0;

		while (i < length) {
			value = value << 8;
			value += data[(i++) + index] & 0xff;
		}

		return value;
	}

	private void getTids(byte[] msg) // ussd rgs_u
	{
		dtidExists = false;
		otid[0] = 0;
		otid[1] = 0;
		otid[2] = 0;
		otid[3] = 0;
		dtid[0] = 0;
		dtid[1] = 0;
		dtid[2] = 0;
		dtid[3] = 0;
		short usParameterLength = 0;
		ByteBuffer bb = ByteBuffer.wrap(msg);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		for (int iIndex = 0; iIndex < 5; iIndex++) {
			byte tcapType = bb.get();
			if (tcapType == 0x14) // RGS TCAP Flag Id
			{
				bb.position(bb.position() + 2);
				tcapType = bb.get();
				int _index = bb.position();
				switch (tcapType) {
				case TCAP.Begin:
					_index = Common.checkLengthType(msg, _index); // 'tcap-begin'
																	// length not
																	// used, skipped
					_index += 1; // otid t+l
					// long _otid = GetBigEndian(msg, _index + 1, msg[_index]);
					System.arraycopy(msg, _index + 1, otid, 0, msg[_index]);
					break;

				case TCAP.Continue:
					_index = Common.checkLengthType(msg, _index); // 'tcap-continue'
																	// length not
																	// used, skipped
					_index += 1; // otid t+l
					// otid = GetBigEndian(msg, _index + 1, msg[_index]);
					System.arraycopy(msg, _index + 1, otid, 0, msg[_index]);
					_index += msg[_index];
					_index += 1;
					_index += 1; // dtid t+l
					// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
					System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
					dtidExists = true;
					break;

				case TCAP.End:
					_index = Common.checkLengthType(msg, _index); // 'tcap-end'
																	// length not
																	// used, skipped
					_index += 1; // dtid t+l
					// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
					System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
					dtidExists = true;
					break;

				case TCAP.Abort:
					_index = Common.checkLengthType(msg, _index); // 'tcap-abort'
																	// length not
																	// used, skipped
					_index += 1; // dtid t+l
					// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
					System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
					dtidExists = true;
					break;
				}
				return;
			}
			usParameterLength = bb.getShort();
			bb.position(bb.position() + usParameterLength);
		}
	}

	public void GetTids(byte[] msg, int _index) // active rgs
	{
		int rgsType = ConfigurationManager.getInstance().getIntValue("RGS_Type");
		if (rgsType == 0) {
			getTids(msg);			
		} else if (rgsType == 1) {
			getTids(msg, _index);
		}
	}

	private void getTids(byte[] msg, int _index) // active rgs_a
	{
		dtidExists = false;
		otid[0] = 0;
		otid[1] = 0;
		otid[2] = 0;
		otid[3] = 0;
		dtid[0] = 0;
		dtid[1] = 0;
		dtid[2] = 0;
		dtid[3] = 0;
		++_index; // sio

		_index += 4; // routing label
		byte type = msg[_index++]; // msg type
		++_index; // class & handling

		if (type == 0x11)
			++_index; // hop

		_index++; // msg var param
		_index++; // msg var param
		int third = msg[_index]; // msg var param

		_index += third; // jump directly to tcap

		_index++; // msg var param

		switch (msg[_index++]) {
		case TCAP.Begin:
			_index = Common.checkLengthType(msg, _index); // 'tcap-begin'
															// length not
															// used, skipped
			_index += 1; // otid t+l
			// long _otid = GetBigEndian(msg, _index + 1, msg[_index]);
			System.arraycopy(msg, _index + 1, otid, 0, msg[_index]);
			break;

		case TCAP.Continue:
			_index = Common.checkLengthType(msg, _index); // 'tcap-continue'
															// length not
															// used, skipped
			_index += 1; // otid t+l
			// otid = GetBigEndian(msg, _index + 1, msg[_index]);
			System.arraycopy(msg, _index + 1, otid, 0, msg[_index]);
			_index += msg[_index];
			_index += 1;
			_index += 1; // dtid t+l
			// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
			System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
			dtidExists = true;
			break;

		case TCAP.End:
			_index = Common.checkLengthType(msg, _index); // 'tcap-end'
															// length not
															// used, skipped
			_index += 1; // dtid t+l
			// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
			System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
			dtidExists = true;
			break;

		case TCAP.Abort:
			_index = Common.checkLengthType(msg, _index); // 'tcap-abort'
															// length not
															// used, skipped
			_index += 1; // dtid t+l
			// dtid = GetBigEndian(msg, _index + 1, msg[_index]);
			System.arraycopy(msg, _index + 1, dtid, 0, msg[_index]);
			dtidExists = true;
			break;
		}
	}
}
