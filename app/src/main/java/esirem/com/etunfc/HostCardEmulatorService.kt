package esirem.com.etunfc

import android.nfc.cardemulation.HostApduService
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.KITKAT)
class HostCardEmulatorService: HostApduService() {

    companion object {
        val TAG = "Host Card Emulator"
        val STATUS_SUCCESS = "9000"
        val STATUS_FAILED = "6F00"
        val CLA_NOT_SUPPORTED = "6E00"
        val INS_NOT_SUPPORTED = "6D00"
        val AID = "A0000002471001"
        val MESSAGE_ANAIS = "Je vous salut, mOn nom Naya"
        val SELECT_INS = "A4"
        val DEFAULT_CLA = "00"
        val MIN_APDU_LENGTH = 12
    }

    //La méthode `onDeactiveted` sera appelée lorsqu'un AID différent a été sélectionné ou que la connexion NFC a été perdue.
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: " + reason)
        TODO("not implemented") // Pour changer le corps des
        // fonctions créées , utilisez File | Settings | File Templates.
    }



    //La méthode `processCommandApdu` sera appelée à chaque fois qu'un lecteur de carte
    // envoie une commande APDU qui est filtrée par notre filtre manifeste.
    override fun processCommandApdu(commandApdu: ByteArray?,
                                    extras: Bundle?): ByteArray {


        TODO("not implemented") // Pour changer le corps des
        // fonctions créées , utilisez File | Settings | File Templates.

        //Ce n'est évidemment qu'une maquette pour créer notre première émulation.
        // Vous pouvez personnaliser cela à votre guise, mais ce que j'ai créé ici est une simple vérification
        // de la longueur et du CLA et INS qui renvoie un APDU (9000) réussi uniquement lorsque nous sélectionnons l'AID prédéfini.

        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)
        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(10, 24) == AID)  {
            return Utils.hexStringToByteArray(MESSAGE_ANAIS)
        } else {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }


}