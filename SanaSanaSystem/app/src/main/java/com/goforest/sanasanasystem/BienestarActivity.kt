package com.goforest.sanasanasystem

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.cancel
import com.goforest.sanasanasystem.api.ApiClient
import com.goforest.sanasanasystem.api.BienestarApi
import com.goforest.sanasanasystem.api.BienestarRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class BienestarActivity : AppCompatActivity() {

    private var selectedEmotionalState: String? = null
    private val selectedSymptoms = mutableSetOf<String>()

    private lateinit var btnVolver: ImageButton
    private lateinit var btnGuardarBienestar: Button
    private lateinit var inputNota: EditText

    private lateinit var emojiTriste: TextView
    private lateinit var emojiPreocupado: TextView
    private lateinit var emojiNeutral: TextView
    private lateinit var emojiContento: TextView
    private lateinit var emojiMuyContento: TextView

    private lateinit var itemDolorCabeza: LinearLayout
    private lateinit var itemCansancio: LinearLayout
    private lateinit var itemFiebre: LinearLayout
    private lateinit var itemNauseas: LinearLayout
    private lateinit var itemDolorMuscular: LinearLayout
    private lateinit var itemDolorGarganta: LinearLayout
    private lateinit var itemEstornudos: LinearLayout
    private lateinit var itemTos: LinearLayout
    private lateinit var itemProblemasSueno: LinearLayout
    private lateinit var itemEstresAnsiedad: LinearLayout
    private lateinit var itemDolorAbdominal: LinearLayout
    private lateinit var itemOtro: LinearLayout

    private lateinit var emotionalEmojis: List<TextView>
    private lateinit var symptomItems: List<LinearLayout>

    // ¡NUEVAS VARIABLES PARA LA API!
    private lateinit var bienestarApi: BienestarApi
    private val coroutineScope = CoroutineScope(Dispatchers.Main) // Usa un CoroutineScope para gestionar corrutinas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienestar)

        // Inicializa la interfaz de la API con tu ApiClient
        bienestarApi = ApiClient.getClient(this).create(BienestarApi::class.java)

        btnVolver = findViewById(R.id.btnVolver)
        btnGuardarBienestar = findViewById(R.id.btnGuardarBienestar)
        inputNota = findViewById(R.id.inputNota)

        emojiTriste = findViewById(R.id.emojiTriste)
        emojiPreocupado = findViewById(R.id.emojiPreocupado)
        emojiNeutral = findViewById(R.id.emojiNeutral)
        emojiContento = findViewById(R.id.emojiContento)
        emojiMuyContento = findViewById(R.id.emojiMuyContento)

        emotionalEmojis = listOf(
            emojiTriste, emojiPreocupado, emojiNeutral, emojiContento, emojiMuyContento
        )

        itemDolorCabeza = findViewById(R.id.itemDolorCabeza)
        itemCansancio = findViewById(R.id.itemCansancio)
        itemFiebre = findViewById(R.id.itemFiebre)
        itemNauseas = findViewById(R.id.itemNauseas)
        itemDolorMuscular = findViewById(R.id.itemDolorMuscular)
        itemDolorGarganta = findViewById(R.id.itemDolorGarganta)
        itemEstornudos = findViewById(R.id.itemEstornudos)
        itemTos = findViewById(R.id.itemTos)
        itemProblemasSueno = findViewById(R.id.itemProblemasSueno)
        itemEstresAnsiedad = findViewById(R.id.itemEstresAnsiedad)
        itemDolorAbdominal = findViewById(R.id.itemDolorAbdominal)
        itemOtro = findViewById(R.id.itemOtro)

        symptomItems = listOf(
            itemDolorCabeza, itemCansancio, itemFiebre, itemNauseas,
            itemDolorMuscular, itemDolorGarganta, itemEstornudos, itemTos,
            itemProblemasSueno, itemEstresAnsiedad, itemDolorAbdominal, itemOtro
        )

        btnVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val emotionalStatesMap = mapOf(
            R.id.emojiTriste to "Triste",
            R.id.emojiPreocupado to "Preocupado",
            R.id.emojiNeutral to "Neutral",
            R.id.emojiContento to "Contento",
            R.id.emojiMuyContento to "Muy Contento"
        )

        emotionalEmojis.forEach { emojiTextView ->
            emojiTextView.setOnClickListener {
                val state = emotionalStatesMap[emojiTextView.id]
                if (state != null) {
                    selectEmotionalState(emojiTextView, state)
                }
            }
        }

        symptomItems.forEach { symptomLayout ->
            symptomLayout.setOnClickListener {
                toggleSymptomSelection(symptomLayout)
            }
        }

        btnGuardarBienestar.setOnClickListener {
            val nota = inputNota.text.toString().trim()

            if (selectedEmotionalState == null) {
                Toast.makeText(this, "Por favor, selecciona tu estado emocional.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear el objeto de petición para el backend
            val request = BienestarRequest(
                estadoEmocional = selectedEmotionalState!!, // Ya validamos que no es nulo
                sintomasFisicos = if (selectedSymptoms.isEmpty()) null else selectedSymptoms.toList(),
                notaAdicional = if (nota.isEmpty()) null else nota
            )

            // Lanzar una corrutina para hacer la llamada a la red
            coroutineScope.launch {
                try {
                    // La llamada a la red debe hacerse en un hilo de IO
                    val response = withContext(Dispatchers.IO) {
                        bienestarApi.registrarBienestar(request)
                    }

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val message = responseBody?.message ?: "Registro de bienestar guardado exitosamente."
                        Toast.makeText(this@BienestarActivity, message, Toast.LENGTH_LONG).show()
                        resetUI() // Reinicia la UI si el guardado fue exitoso
                        Log.d("BienestarActivity", "Registro exitoso: ${responseBody?.idRegistroBienestar}, Fecha: ${responseBody?.fechaRegistro}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "Error al guardar: ${response.code()} - ${errorBody ?: "Error desconocido"}"
                        Toast.makeText(this@BienestarActivity, errorMessage, Toast.LENGTH_LONG).show()
                        Log.e("BienestarActivity", "Error de API: ${response.code()}, Cuerpo: $errorBody")
                    }
                } catch (e: HttpException) {
                    val errorMessage = "Error HTTP: ${e.code()} - ${e.message()}"
                    Toast.makeText(this@BienestarActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("BienestarActivity", "HttpException: ${e.message()}", e)
                } catch (e: IOException) {
                    val errorMessage = "Error de red: ${e.message}"
                    Toast.makeText(this@BienestarActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("BienestarActivity", "IOException: ${e.message}", e)
                } catch (e: Exception) {
                    val errorMessage = "Error inesperado: ${e.message}"
                    Toast.makeText(this@BienestarActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("BienestarActivity", "Exception: ${e.message}", e)
                }
            }
        }
    }

    private fun selectEmotionalState(selectedTextView: TextView, state: String) {
        emotionalEmojis.forEach { emoji ->
            emoji.setBackgroundResource(android.R.color.transparent)
            emoji.setTextColor(ContextCompat.getColor(this, R.color.textDark))
        }

        selectedTextView.setBackgroundResource(R.drawable.bg_selected_emoji)
        selectedTextView.setTextColor(ContextCompat.getColor(this, R.color.principal))

        selectedEmotionalState = state
    }

    private fun toggleSymptomSelection(symptomLayout: LinearLayout) {
        val symptomTextView = symptomLayout.getChildAt(0) as? TextView
        val symptomName = symptomTextView?.text?.toString()

        if (symptomName != null) {
            if (selectedSymptoms.contains(symptomName)) {
                selectedSymptoms.remove(symptomName)
                symptomLayout.setBackgroundResource(R.drawable.bg_symptom_item_selector)
                symptomTextView.setTextColor(ContextCompat.getColor(this, R.color.textDark))
            } else {
                selectedSymptoms.add(symptomName)
                symptomLayout.setBackgroundResource(R.drawable.bg_symptom_item_selected)
                symptomTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }

    private fun resetUI() {
        emotionalEmojis.forEach { emoji ->
            emoji.setBackgroundResource(android.R.color.transparent)
            emoji.setTextColor(ContextCompat.getColor(this, R.color.textDark))
        }
        selectedEmotionalState = null

        symptomItems.forEach { symptomLayout ->
            symptomLayout.setBackgroundResource(R.drawable.bg_symptom_item_selector)
            val symptomTextView = symptomLayout.getChildAt(0) as? TextView
            symptomTextView?.setTextColor(ContextCompat.getColor(this, R.color.textDark))
        }
        selectedSymptoms.clear()

        inputNota.text.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.coroutineContext.cancel()
    }
}