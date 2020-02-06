package esirem.com.etunfc.NewReadEmulate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.TextView;

import esirem.com.etunfc.R;

public class ReadData extends AppCompatActivity {

    private NfcAdapter mAdapter;

    private static final String MY_CARDIO_APP_TOKEN = "f5c575f4b0da4410838660f5d3e962f8";
    private Button mScanButton;
    private TextView mCardDataView;
    private String mCardDetails;

    private int MY_SCAN_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_read_data );

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        setupUI();
    }


    private void setupUI() {
        mCardDataView = (TextView)findViewById(R.id.texView);
        mScanButton = (Button)findViewById(R.id.readBtn);
    }

}
