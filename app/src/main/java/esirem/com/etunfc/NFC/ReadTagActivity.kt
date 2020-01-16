package esirem.com.etunfc.NFC

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import esirem.com.etunfc.R
import esirem.com.etunfc.Utils


@RequiresApi(Build.VERSION_CODES.KITKAT)
class ReadTagActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private var textView: TextView? = null
    private var mTurnNfcDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_tag)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Si le tél ne dispose pas de NFC
        if (nfcAdapter == null) {

            Toast.makeText(this, "Votre téléphone ne dispose pas de la technologie NFC", Toast.LENGTH_LONG).show()
        } else if (!nfcAdapter!!.isEnabled) { //Si le tél dispose du module NFC et NFC non activé
//On ouvre la boîte de dialogue qui va permettre à l'utilisateur d'activer le NFC

            showTurnOnNfcDialog()

        } else if (nfcAdapter!!.isEnabled) { //Si le tél dispose du module NFC et NFC activé

            textView = findViewById(R.id.texView)

        }

    }

    //TODO------------------------------------ CYCLE DE VIE ----------------------------------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A
                or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    //todo-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onTagDiscovered(tag: Tag?) {

        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007A0000002471001"))
        runOnUiThread { textView?.append("\nCard Response: "
                + Utils.toHex(response)) }
        isoDep.close()
    }

    private fun showTurnOnNfcDialog() {
        if (mTurnNfcDialog == null) {
            val title = getString(R.string.ad_nfcTurnOn_title)
            val mess = getString(R.string.ad_nfcTurnOn_message)
            val pos = getString(R.string.ad_nfcTurnOn_pos)
            val neg = getString(R.string.ad_nfcTurnOn_neg)
            //Construction de notre boîte de dialogue
            mTurnNfcDialog = AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(mess) //Bouton positif
                    .setPositiveButton(pos) { dialogInterface, i ->
                        // Envoyez l'utilisateur à la page des paramètres et espérez qu'il l'activera
                        if (Build.VERSION.SDK_INT >= 16) {
                            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        } else {
                            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        }
                    } //Bouton négatif
                    .setNegativeButton(neg) { dialogInterface, i ->
                        //Appel de la fonction retour en arrière
                        onBackPressed()
                    }.create()
        }
        mTurnNfcDialog!!.show()
    }
}