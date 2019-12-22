package esirem.com.etunfc

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var projet_type: TextView? = null
    private val edouard_logo: ImageView? = null
    private val chantou_logo: ImageView? = null
    private val daphnie_logo: ImageView? = null
    private val primous_logo: ImageView? = null
    private val mLogo: ImageView? = null

    private val tabImageView = arrayOf(edouard_logo, chantou_logo, daphnie_logo, primous_logo, mLogo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //LIAISON CODE-GUI
        projet_type = findViewById(R.id.projet_type)
        tabImageView[0] = findViewById(R.id.edouard_logo)
        tabImageView[1] = findViewById(R.id.chantou_logo)
        tabImageView[2] = findViewById(R.id.daphnie_logo)
        tabImageView[3] = findViewById(R.id.primous_logo)
        tabImageView[4] = findViewById(R.id.logo)

        //Police
        val FONT_BugLife = Typeface.createFromAsset(this.assets, "fonts/A Bug s Life.ttf")
        projet_type!!.setTypeface(FONT_BugLife, Typeface.BOLD)

        //Chargement de notre ressource d'animation
        val myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition)

        //On anime nos ImageView
        for (i in 0..4) {
            tabImageView[i]!!.setAnimation(myanim)
        }

        val i = Intent(this@MainActivity, PrincipalActivity::class.java)

        val timer: Thread = object : Thread() {

            override fun run() {

                try {
                    sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()

                } finally {
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(i)
                    finish()
                }
            }
        }
        timer.start()
    }
}