package esirem.com.etunfc.NewReadEmulate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import esirem.com.etunfc.R;

public class WriteData extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private NdefMessage mNdefMessage;
    private Button tglBouton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_write_data );

        mNfcAdapter = NfcAdapter.getDefaultAdapter( this );

        tglBouton  = findViewById( R.id.tglReadWrite );
        editText = findViewById( R.id.button );

        tglBouton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNdefMessage = new NdefMessage(
                        new NdefRecord[] {
                                createNewTextRecord(editText.getText().toString(), Locale.ENGLISH, true)});

            }
        } );

    }

    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes( Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char)(utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundNdefPush(this, mNdefMessage);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundNdefPush(this);
    }


}
