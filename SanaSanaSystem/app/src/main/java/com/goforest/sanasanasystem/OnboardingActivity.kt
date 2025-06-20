package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var skipText: TextView
    private lateinit var nextButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val items = listOf(
            OnboardingItem(R.drawable.ic_hospital, "Bienvenido a Sana Sana", "Accede a servicios médicos y gestiona tus citas desde la comodidad de tu celular."),
            OnboardingItem(R.drawable.ic_hospital, "Agenda tus Citas Fácilmente", "Encuentra la especialidad y el médico que necesitas. ¡Reserva en segundos!"),
            OnboardingItem(R.drawable.ic_hospital, "Historial Médico al Alcance", "Consulta tu historial, resultados y signos vitales en cualquier momento y lugar.")
        )

        viewPager = findViewById(R.id.viewPager)
        indicator = findViewById(R.id.dotsIndicator)
        skipText = findViewById(R.id.skipText)
        nextButton = findViewById(R.id.nextButton)

        viewPager.adapter = OnboardingAdapter(items)
        indicator.setViewPager(viewPager)

        skipText.setOnClickListener {
            navigateToLogin()
        }

        nextButton.setOnClickListener {
            if (viewPager.currentItem + 1 < items.size) {
                viewPager.currentItem += 1
            } else {
                navigateToLogin()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == items.size - 1) {
                    nextButton.text = "Empezar"
                    skipText.visibility = View.GONE
                } else {
                    nextButton.text = "Siguiente"
                    skipText.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem -= 1
        }
    }
}