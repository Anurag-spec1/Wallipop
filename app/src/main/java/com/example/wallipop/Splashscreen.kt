package com.example.wallipop

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//class Splashscreen : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splashscreen)
//
//        Handler().postDelayed({
//            startActivity(Intent(this, Login::class.java))
//            finish()
//        },3000)
//
//    }
//}
class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val letters = listOf(
            findViewById<TextView>(R.id.letterW),
            findViewById<TextView>(R.id.letterA),
            findViewById<TextView>(R.id.letterL1),
            findViewById<TextView>(R.id.letterL2),
            findViewById<TextView>(R.id.letterI),
            findViewById<TextView>(R.id.letterP),
            findViewById<TextView>(R.id.letterO),
            findViewById<TextView>(R.id.letterP2)
        )

        // Animate each letter with a fire explosion effect
        for ((index, letter) in letters.withIndex()) {
            animateFireLetter(letter, index * 200L)
        }

        // Move to Main Activity after animations
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }

    private fun animateFireLetter(view: View, delay: Long) {
        view.postDelayed({
            val animatorSet = AnimatorSet()

            // Scale explosion effect
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.5f, 1f)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.5f, 1f)

            // Alpha fade-in effect
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

            // Glow effect for text (only if view is a TextView)
            val glow = ValueAnimator.ofFloat(10f, 30f, 10f).apply {
                duration = 500
                addUpdateListener { animation ->
                    (view as? TextView)?.setShadowLayer(animation.animatedValue as Float, 0f, 0f, Color.RED)
                }
            }

            // Play animations together
            animatorSet.playTogether(scaleX, scaleY, fadeIn, glow)
            animatorSet.start()
        }, delay)
    }

}


