package esirem.com.etunfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;

public class ReadDataCardActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {

    private CardNfcAsyncTask mCardNfcAsyncTask;
    private LinearLayout mCardReadyContent;
    private TextView mPutCardContent;
    private TextView mCardNumberText;
    private TextView mExpireDateText;
    private ImageView mCardLogoIcon;
    private NfcAdapter mNfcAdapter;
    private AlertDialog mTurnNfcDialog;
    private ProgressDialog mProgressDialog;
    private String mDoNotMoveCardMessage;
    private String mUnknownEmvCardMessage;
    private String mCardWithLockedNfcMessage;
    private boolean mIsScanNow;
    private boolean mIntentFromCreate;
    private CardNfcUtils mCardNfcUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_read_data_card );

        //On va vérifier si l'appareil dispose de la technologie NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter( this );

        //Si le tél ne dispose pas de NFC
        if( mNfcAdapter == null){
            //On affiche un message lui disant qu'il ny'a pas de NFC présente
            TextView noNfc = (TextView)findViewById(R.id.NotHaveNFC_tv);
            noNfc.setVisibility( View.VISIBLE);
            Toast.makeText( this, "Votre téléphone ne dispose pas de la technologie NFC", Toast.LENGTH_LONG ).show();
        }else { //Si le tél dispose du module NFC

            //On lie nos variable au GUI
            mCardNfcUtils = new CardNfcUtils( this );
            mPutCardContent = (TextView) findViewById(R.id.content_putCard);
            mCardReadyContent = (LinearLayout) findViewById(R.id.content_cardReady);
            mCardNumberText = (TextView) findViewById(android.R.id.text1);
            mExpireDateText = (TextView) findViewById(android.R.id.text2);
            mCardLogoIcon = (ImageView) findViewById(android.R.id.icon);
            //On crée la barre de progression via l'appel de cette méthode
            createProgressDialog();
            initNfcMessages();
            // les lignes suivantes sont nécessaires ici au cas où nous scannerions la carte de crédit lorsque l'application est fermée
            mIntentFromCreate = true;
            onNewIntent( getIntent() );
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        mIntentFromCreate = false;
        //Si le tél a NFC mais qu'il est désactivé
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()){
            //On ouvre la boîte de dialogue qui va permettre à l'utilisateur d'activer le NFC
            showTurnOnNfcDialog();
            mPutCardContent.setVisibility(View.GONE);

        }else if (mNfcAdapter != null){
            //Si la carte n'a pas encore été scannée
            if (!mIsScanNow){

                mPutCardContent.setVisibility( View.VISIBLE );
                mCardReadyContent.setVisibility( View.GONE );
            }
            //Activation de l'envoi
            mCardNfcUtils.enableDispatch();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Lorsque l'activiyé se met en pause on désactive l'envoi (le champ)
        if (mNfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        //Si le NFC présent dans l'appareil est activé
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            //On construit la variable lié à notre interface que l'on a lié à cette classe, interface qui contient des méthodes à coder
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                    .build();
        }
    }


// Todo ---- LORSQUE LA LECTURE NFC COMMENCE
    @Override
    public void startNfcReadCard() {
        //On passe notre booléen à True
        mIsScanNow = true;
        //On affiche notre progress dialog
        mProgressDialog.show();
    }

    //----TODO lorsque la carte est prête à être lue
    @Override
    public void cardIsReadyToRead() {

        //On rend invisible cet image
        mPutCardContent.setVisibility(View.GONE);
        //Et visible celle-ci
        mCardReadyContent.setVisibility(View.VISIBLE);
        //On récupère le numéro de carte
        String card = mCardNfcAsyncTask.getCardNumber();
        //On appelle cette méthode pour obtenir le numéro de carte avec des "-" après chaque 04 chiffres
        card = getPrettyCardNumber(card);
        //Date d'expiration de la carte
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        //On récupère le type de carte (VISA, MASTERCARD, etc...)
        String cardType = mCardNfcAsyncTask.getCardType();
        //On affiche le numéro de carte et la date d'expiration sur l'interface graphique
        mCardNumberText.setText(card);
        mExpireDateText.setText(expiredDate);
        parseCardType(cardType);

    }

    //Méthode pour dire à l'utilisateur "ne déplacez pas la carte si vite!!" LOOOL
    //En fait elle détecte si l'utilisateur a déplacé la carte
    @Override
    public void doNotMoveCardSoFast() {

        showLongToast(mDoNotMoveCardMessage);
    }

    //Carte Emulation inconnue
    @Override
    public void unknownEmvCard() {
        showLongToast(mUnknownEmvCardMessage);
    }

    //Si le NFC est bloqué dans cette carte
    @Override
    public void cardWithLockedNfc() {

        showLongToast(mCardWithLockedNfcMessage);
    }

    //Lorsque la lecture de la carte est terminée
    @Override
    public void finishNfcReadCard() {

        mProgressDialog.dismiss();
        mCardNfcAsyncTask = null;
        mIsScanNow = false;
    }

    //TODO ----------------------------------------- CREATION DE NOTRE BOITE DE DIALOGUE ----------------------------------------------------------------------
    private void createProgressDialog(){

        String title = getString(R.string.ad_progressBar_title);
        String mess = getString(R.string.ad_progressBar_mess);
        mProgressDialog = new ProgressDialog(this);
        //titre
        mProgressDialog.setTitle(title);
        //message
        mProgressDialog.setMessage(mess);
        mProgressDialog.setIndeterminate(true);
        //On ne peut pas enlever tant qe le processus n'est pas terminé
        mProgressDialog.setCanceledOnTouchOutside( false );
        mProgressDialog.setCancelable(false);
    }
    //todo ------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //TODO ----------------------------------------------------------------------- AFFICHAGE DE NOTRE SNACKBAR --------------------------------------------------------------------------------------------------------------------
    private void showSnackBar(String message){
        View view = new View( this );
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
    //todo-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //TODO --------------------------------------------------------------- INIT MESSAGES NFC ----------------------------------------------------------------------------------------------------------------------------
    //Initialisation des messages que l'on pourrait afficher à l'écran
    private void initNfcMessages(){

        mDoNotMoveCardMessage = getString(R.string.snack_doNotMoveCard);
        mCardWithLockedNfcMessage = getString(R.string.snack_lockedNfcCard);
        mUnknownEmvCardMessage = getString(R.string.snack_unknownEmv);
    }
//todo----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //TODO -------------------------------------------- ON RECUPERE LE TYPE DE CARTE SCANNEE -----------------------------------------------------------------------------------------------------------------------------------------------

    private void parseCardType(String cardType){
        View view = new View( this );
        //I le type de la carte est inconnu
        if (cardType.equals(CardNfcAsyncTask.CARD_UNKNOWN)){
            //On affiche un message
            Snackbar.make(view, R.string.snack_unknown_bank_card, Snackbar.LENGTH_SHORT).show();

        } else if (cardType.equals(CardNfcAsyncTask.CARD_VISA)){ //si le type est VISA
            //On affiche son logo
            mCardLogoIcon.setImageResource(R.drawable.visa_logo);

        } else if (cardType.equals(CardNfcAsyncTask.CARD_MASTER_CARD)){ //si le type est MASTER CARD
            //On affiche son logo
            mCardLogoIcon.setImageResource(R.drawable.master_logo);
        }
    }
    //todo------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //TODO ---------------------------------- ON SEPARE LE NUMERO DE CARTE BANCAIRE PAR DES "-" , AVEC UN SAUT DE 04 chiffres -----------------------------------------------------------------------------------------------------------------------
    private String getPrettyCardNumber(String card){

        String div = " - ";

        return  card.substring(0,4) + div + "XXXX" + div + "XXXX"
                +div + card.substring(12,16);
        /*return  card.substring(0,4) + div + card.substring(4,8) + div + card.substring(8,12)
                +div + card.substring(12,16);*/
    }
    //todo----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //todo ----------------------------------------------- BOITE DE DIALOGUE POUR ACTIVER LE NFC DE L'APPAREIL ---------------------------------------------------------------------------------------------
    private void showTurnOnNfcDialog(){

        if (mTurnNfcDialog == null) {
            String title = getString(R.string.ad_nfcTurnOn_title);
            String mess = getString(R.string.ad_nfcTurnOn_message);
            String pos = getString(R.string.ad_nfcTurnOn_pos);
            String neg = getString(R.string.ad_nfcTurnOn_neg);

            //Construction de notre boîte de dialogue
            mTurnNfcDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(mess)
                    //Bouton positif
                    .setPositiveButton(pos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Envoyez l'utilisateur à la page des paramètres et espérez qu'il l'activera
                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            } else {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    //Bouton négatif
                    .setNegativeButton(neg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Appel de la fonction retour en arrière
                            onBackPressed();
                        }
                    }).create();
        }
        mTurnNfcDialog.show();
    }
    // todo ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
