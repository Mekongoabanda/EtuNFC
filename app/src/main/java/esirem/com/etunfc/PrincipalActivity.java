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

import esirem.com.etunfc.NFC.NFCInterface;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener /*NFCInterface*/ {

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
    private LinearLayout readBankCard_btn, writeBankCard_btn;
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

            txtNFCID=(TextView)findViewById(R.id.txtNFCID);
            readTxt=(TextView)findViewById(R.id.txt_read);
            writeTxt=(TextView)findViewById(R.id.txt_write);
            readBankCard_btn=(LinearLayout)findViewById(R.id.read_bank_card);
            writeBankCard_btn=(LinearLayout)findViewById(R.id.read_tag);

            //Police
            Typeface FONT_BugLife = Typeface.createFromAsset( getAssets(), "fonts/MavenPro-Medium.ttf");
            readTxt.setTypeface(FONT_BugLife, Typeface.BOLD);
            onNewIntent( getIntent() );

            readBankCard_btn.setOnClickListener( this );

        }

    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter();
        //On devra permettre ces trois actions sachant que l'ordre de priorité est --> NDEF - TECH - TAG
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        //On récupère le type de technologie NFC utilisée
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //On active la répartition au pemier plan
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // désactivation de la répartition au premier plan
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        //Si l'action est type TAG discovered
        if (intent.getAction().equals( NfcAdapter.ACTION_TAG_DISCOVERED )){

            String type = intent.getType();
            Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
            //On appel la procédure nfcRead de notre interface en lui passant le tag
            nfcRead( tag );

        }else if (intent.getAction().equals( NfcAdapter.ACTION_NDEF_DISCOVERED )) {

            String type = intent.getType();
            Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
            //On appel la procédure nfcRead de notre interface en lui passant le tag
            nfcRead( tag );
        }
        else if (intent.getAction().equals( NfcAdapter.ACTION_TECH_DISCOVERED )) {

            String type = intent.getType();
            Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
            //On appel la procédure nfcRead de notre interface en lui passant le tag
            nfcRead( tag );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unsubscribe(nfcSubscription);
    }

     */




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

    }
/*
    private static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    //------------------------------------------------------ METHODES LIEES A NOTRE INTERFACE NFCINTERFACE  ----------------------------------------------------------------------------------
    @Override
    public String nfcRead(Tag t) {

        try {

            Tag tag = t;
            Ndef ndef = Ndef.get( tag );
            //Si notre ndef est null on return null
            if(ndef == null){
                return null;
            }
            //On récupère un NdefMessage
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            //On récupère le tableau des NDEF records contenu dans notre NDEF Message
            NdefRecord[] records = ndefMessage.getRecords();

            //On parcours notre tableau  "records" de NdefRecords
            for (NdefRecord ndefRecord : records){

                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT))
                {
                    try {return readText(ndefRecord);} catch (UnsupportedEncodingException e) {}
                }

            }

        } catch (Exception e){

            return null;
        }
        return null;
    }

    @Override
    public String readText(NdefRecord record) throws UnsupportedEncodingException {

        byte[] payload = record.getPayload();
        //Si notre byte à l'indice [0] & 128 == 0 alors on encore en UTF-8 sinon en UTF-16
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    public void nfcReader(Tag tag) {

        nfcSubscription= Observable.just(nfcRead(tag))
                .subscribeOn( Schedulers.newThread())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (s != null) {

                            txtNFCID.setText(s);

                        }
                    }
                });

    }

 */
}
