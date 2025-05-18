package com.goforest.sanasanasystem
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3
import android.content.Intent

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var skipText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val items = listOf(
            OnboardingItem(R.drawable.ic_hospital, "Bienvenido", "Accede a los servicios del hospital desde tu celular."),
//            OnboardingItem(R.drawable.ic_calendar, "Agenda Citas", "Programa tus citas médicas fácilmente."),
//            OnboardingItem(R.drawable.ic_doctor, "Conoce a Nuestros Médicos", "Consulta el perfil de nuestros especialistas.")
        )

        viewPager = findViewById(R.id.viewPager)
        indicator = findViewById(R.id.dotsIndicator)
        skipText = findViewById(R.id.skipText)

        viewPager.adapter = OnboardingAdapter(items)
        indicator.setViewPager(viewPager)

        skipText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
