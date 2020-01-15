package esirem.com.etunfc.NFC

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import esirem.com.etunfc.R

class EmulationNFC : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private var mTurnNfcDialog: AlertDialog? = null
    private var write_btn: Button? = null
    private var txtTagContent: TextView? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emulation_nfc)

        //On vérifie si la méthode "checkSelfPermission" renvoit True sur les trois demandes de permissions
//Si OUI on execute notre code dans ce "if"
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) { //On va vérifier si l'appareil dispose de la technologie NFC

            mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
            //Si le tél ne dispose pas de NFC
            if (mNfcAdapter == null) {
                Toast.makeText(this, "Votre téléphone ne dispose pas de la technologie NFC", Toast.LENGTH_LONG).show()
            } else if (!mNfcAdapter!!.isEnabled) { //Si le tél dispose du module NFC et NFC non activé
//On ouvre la boîte de dialogue qui va permettre à l'utilisateur d'activer le NFC
                showTurnOnNfcDialog()
            } else if (mNfcAdapter!!.isEnabled) { //Si le tél dispose du module NFC et NFC activé
                write_btn = findViewById(R.id.tglReadWrite)
                txtTagContent = findViewById(R.id.txtTagContent)
            }
        }
    }

    //TODO--------------------------------------------------------------------- CYCLE DE VIE -------------------------------------------------------------------------------------------------------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        //Activation du système de répartition du premier plan
//enableForegroundDispatchSystem();
    }

    override fun onPause() {
        super.onPause()
        //Désactivation du système de répartition du premier plan
//disableForegroundDispatchSystem();
    }

    //TODO---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//TODO--------------------------------------------------------------------- PERMISSIONS DE L'APPLICATION -------------------------------------------------------------------------------------------------------------------------------------------------------------
//Procédure checkSelfPermissions qui prend en paramètre un String (type de permission) et un Int (requestID)
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("WARNING! Need permissions : " + Manifest.permission.NFC_TRANSACTION_EVENT +
                        "/" + Manifest.permission.NFC + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE)
                finish()
                return
            }
        }
    }

    //TODO--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//Long Toast
    private fun showLongToast(msg: String) {
        runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
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

    companion object {
        //On donne un id à nos demandes de permission
        private const val PERMISSION_REQ_ID = 22
        //Tableau de String pour nos permissions
        private val REQUESTED_PERMISSIONS = arrayOf(
                Manifest.permission.NFC,
                Manifest.permission.NFC_TRANSACTION_EVENT,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}