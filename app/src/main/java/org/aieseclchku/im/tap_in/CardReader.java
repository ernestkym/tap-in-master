package org.aieseclchku.im.tap_in;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;
import java.lang.ref.WeakReference;

/**
 * Created by Alan on 29/12/14.
 */
public class CardReader implements NfcAdapter.ReaderCallback{

    private WeakReference<AccountCallback> mAccountCallback;

    public interface AccountCallback {
        public void onAccountReceived(String account);
    }

    public CardReader(AccountCallback accountCallback) {
        mAccountCallback = new WeakReference<AccountCallback>(accountCallback);
    }


    @Override
    public void onTagDiscovered(Tag tag) {
        String TagID =  ByteArrayToHexString(tag.getId());
        mAccountCallback.get().onAccountReceived(TagID);
        Log.d("UID:", TagID);

    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
