package jp.co.topgate.android.nfc;

import java.lang.reflect.Field;

import com.google.common.primitives.UnsignedBytes;

/**
 * {@link android.nfc.Tag} の簡易ラッパです.
 * 
 * @author vvakame
 */
public class TagWrapper {
	/**
	 * ISO 14443-3A technology.
	 * <p>
	 * Includes Topaz (which is -3A compatible)
	 */
	public static final String TARGET_ISO_14443_3A = "iso14443_3a";

	/**
	 * ISO 14443-3B technology.
	 */
	public static final String TARGET_ISO_14443_3B = "iso14443_3b";

	/**
	 * ISO 14443-4 technology.
	 */
	public static final String TARGET_ISO_14443_4 = "iso14443_4";

	/**
	 * ISO 15693 technology, commonly known as RFID.
	 */
	public static final String TARGET_ISO_15693 = "iso15693";

	/**
	 * JIS X-6319-4 technology, commonly known as Felica.
	 */
	public static final String TARGET_JIS_X_6319_4 = "jis_x_6319_4";

	/**
	 * Any other technology.
	 */
	public static final String TARGET_OTHER = "other";

	/* package */final boolean mIsNdef;
	/* package */final byte[] mId;
	/* package */final String[] mRawTargets;
	/* package */final byte[] mPollBytes;
	/* package */final byte[] mActivationBytes;
	/* package */final int mServiceHandle; // for use by NFC service, 0
											// indicates a mock

	/**
	 * コンストラクタ. {@link android.nfc.Tag} を引数にとるが、 {@code @hide} が指定されているため参照できない.
	 * 
	 * @param tag
	 *            {@link android.nfc.Tag}
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public TagWrapper(Object tag) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {

		Field f;

		f = tag.getClass().getDeclaredField("mIsNdef");
		f.setAccessible(true);
		mIsNdef = (Boolean) f.get(tag);

		f = tag.getClass().getDeclaredField("mId");
		f.setAccessible(true);
		mId = (byte[]) f.get(tag);

		f = tag.getClass().getDeclaredField("mRawTargets");
		f.setAccessible(true);
		mRawTargets = (String[]) f.get(tag);

		f = tag.getClass().getDeclaredField("mPollBytes");
		f.setAccessible(true);
		mPollBytes = (byte[]) f.get(tag);

		f = tag.getClass().getDeclaredField("mActivationBytes");
		f.setAccessible(true);
		mActivationBytes = (byte[]) f.get(tag);

		f = tag.getClass().getDeclaredField("mServiceHandle");
		f.setAccessible(true);
		mServiceHandle = (Integer) f.get(tag);

		toString();
	}

	/**
	 * 検知したデバイスがFelicaチップかどうかを判定します.
	 * 
	 * @return Felicaチップの場合 {@code true}
	 */
	public boolean isFelica() {
		if (mRawTargets == null) {
			return false;
		}
		for (String rawTarget : mRawTargets) {
			if (TARGET_JIS_X_6319_4.equals(rawTarget)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * FelicaのIDmを取ってるはず多分
	 * 
	 * @return FelicaのIDm
	 */
	public String getHexIDString() {
		if (mId == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (byte id : mId) {
			String hexString = Integer.toHexString(UnsignedBytes.toInt(id));
			if (hexString.length() == 1) {
				builder.append("0");
			}
			builder.append(hexString);
		}
		return builder.toString();
	}

	/**
	 * モックタグかどうかを判定して返す.
	 * 
	 * @return モックタグなら {@code true}
	 */
	public boolean isMockTag() {
		return mServiceHandle == 0;
	}
}