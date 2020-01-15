package esirem.com.etunfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import esirem.com.etunfc.NFC.EmulationNFC;
import esirem.com.etunfc.NFC.NFCInterface;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    //On donne un id à nos demandes de permission
    private static final int PERMISSION_REQ_ID = 22;
    //Tableau de String pour nos permissions
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.NFC,
            Manifest.permission.NFC_TRANSACTION_EVENT,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private TextView txtNFCID, readTxt, writeTxt;
    private Subscription nfcSubscription;
    private LinearLayout readBankCard_btn, writeBankCard_btn, EmulationNfc;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_principal );

        //On va vérifier si l'appareil dispose de la technologie NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter( this );

        //On vérifie si la méthode "checkSelfPermission" renvoit True sur les trois demandes de permissions
        //Si OUI on execute notre code dans ce "if"
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {

            readTxt=(TextView)findViewById(R.id.txt_read);
            writeTxt=(TextView)findViewById(R.id.txt_write);
            readBankCard_btn=(LinearLayout)findViewById(R.id.read_bank_card);
            writeBankCard_btn=(LinearLayout)findViewById(R.id.read_tag);
            EmulationNfc=(LinearLayout)findViewById(R.id.EmulationNFC);

            //Police
            Typeface FONT_BugLife = Typeface.createFromAsset( getAssets(), "fonts/MavenPro-Medium.ttf");
            readTxt.setTypeface(FONT_BugLife, Typeface.BOLD);
            readBankCard_btn.setOnClickListener( this );
            EmulationNfc.setOnClickListener( this );

        }

    }

    //TODO--------------------------------------------------------------------- PERMISSIONS DE L'APPLICATION -------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Procédure checkSelfPermissions qui prend en paramètre un String (type de permission) et un Int (requestID)
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("WARNING! Need permissions : " + Manifest.permission.NFC_TRANSACTION_EVENT +
                        "/" + Manifest.permission.NFC + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE );
                finish();
                return;
            }
        }
    }

    //TODO--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Long Toast
    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == readBankCard_btn){

            Intent i  = new Intent( PrincipalActivity.this, ReadDataCardActivity.class );
            startActivity( i );

        }

        if (v == readBankCard_btn){

            Intent i  = new Intent( PrincipalActivity.this, EmulationNFC.class );
            startActivity( i );

        }

    }
}
