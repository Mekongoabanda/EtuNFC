package esirem.com.etunfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;
    //Tableau de String pour nos permissions
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.NFC,
            Manifest.permission.NFC_TRANSACTION_EVENT,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_principal );

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {


        }

    }

    //Obtenir des informations des intentions
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String tagSTR = tag.toString();
        showLongToast( tagSTR );

        // On vérifie que l' ACTION_NDEF_DISCOVERED  obtient les messages NDEF à partir d'une intention supplémentaire
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
                String finalMessage = "";
                for (int i = 0; i < rawMessages.length; i++) {
                    finalMessage += " "+ messages[i] ;
                }
                Toast.makeText( this, finalMessage, Toast.LENGTH_SHORT ).show();

            }
        }
    }

    //TODO--------------------------------------------------------------------- PERMISSIONS DE L'APPLICATION -------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Procédure checkSelfPermissions
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
            }else {
                super.onBackPressed();
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
}
