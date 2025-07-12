package com.goforest.sanasanasystem

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import com.goforest.sanasanasystem.api.ApiClient
import com.goforest.sanasanasystem.api.MedicalHistoryItem
import com.goforest.sanasanasystem.api.MedicalHistoryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat

class MyAppointmentsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitasAdapter
    private val TAG = "MyAppointmentsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            Log.d(TAG, "Iniciando MyAppointmentsActivity")
            
            setContentView(R.layout.activity_my_appointments)
            Log.d(TAG, "Layout cargado correctamente")

            // Configurar el botón de volver
            findViewById<ImageButton>(R.id.btnVolver)?.let { button ->
                button.setOnClickListener {
                    finish()
                }
                Log.d(TAG, "Botón volver configurado")
            } ?: run {
                Log.e(TAG, "No se pudo encontrar el botón volver")
            }

            // Configurar RecyclerView
            recyclerView = findViewById(R.id.citasRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            Log.d(TAG, "RecyclerView configurado")
            
            try {
                // Crear adaptador y asignarlo
                adapter = CitasAdapter(listOf()) { cita -> showExpandedQRDialog(cita) }
                recyclerView.adapter = adapter
                Log.d(TAG, "Adaptador asignado correctamente")
                fetchAppointments()
            } catch (e: Exception) {
                Log.e(TAG, "Error al crear o asignar el adaptador: ${e.message}")
                Toast.makeText(this, "Error al cargar las citas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            Toast.makeText(this, "Error al iniciar la actividad", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchAppointments() {
        val service = ApiClient.getClient(this).create(MedicalHistoryService::class.java)
        service.getMedicalHistory().enqueue(object : Callback<List<MedicalHistoryItem>> {
            override fun onResponse(call: Call<List<MedicalHistoryItem>>, response: Response<List<MedicalHistoryItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    val citas = response.body()!!.map {
                        Cita(
                            fecha = it.fecha,
                            especialidad = it.especialidad,
                            medico = it.medico?.let { m -> "${m["nombres"]} ${m["apellidos"]}" } ?: "-",
                            estado = it.estado,
                            qrCodigo = it.qr_codigo
                        )
                    }
                    adapter.updateData(citas)
                } else {
                    Toast.makeText(this@MyAppointmentsActivity, "No se pudo obtener las citas", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<MedicalHistoryItem>>, t: Throwable) {
                Toast.makeText(this@MyAppointmentsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showExpandedQRDialog(cita: Cita) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_qr_expanded)

            dialog.findViewById<ImageButton>(R.id.btnCerrar)?.setOnClickListener {
                dialog.dismiss()
            }

            // Poblar los campos del modal con los datos de la cita
            dialog.findViewById<TextView>(R.id.fechaExpandedTextView)?.text = formatFecha(cita.fecha)
            dialog.findViewById<TextView>(R.id.horaExpandedTextView)?.text = "10:30 AM" // O el valor real si lo tienes
            dialog.findViewById<TextView>(R.id.ubicacionExpandedTextView)?.text = "Clínica SanaSana"
            dialog.findViewById<TextView>(R.id.medicoExpandedTextView)?.text = cita.medico
            dialog.findViewById<TextView>(R.id.especialidadExpandedTextView)?.text = cita.especialidad

            // Generar y mostrar QR expandido
            val qrExpandedImageView = dialog.findViewById<ImageView>(R.id.qrExpandedImageView)
            val qrBitmap = generateQRCode(cita.qrCodigo ?: "", 180)
            qrExpandedImageView?.setImageBitmap(qrBitmap)

            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al mostrar el código QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateQRCode(text: String, size: Int): Bitmap? {
        return try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            Log.d(TAG, "QR generado correctamente")
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error al generar QR: ${e.message}")
            null
        }
    }

    private fun formatFecha(fecha: String): String {
        // Formato de fecha amigable
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("d MMM yyyy", Locale("es"))
            val date = parser.parse(fecha)
            formatter.format(date!!)
        } catch (e: Exception) {
            fecha
        }
    }
}

// Modelo de datos para Cita
data class Cita(
    val fecha: String,
    val especialidad: String,
    val medico: String,
    val estado: String,
    val qrCodigo: String?
)

// Adaptador para el RecyclerView
class CitasAdapter(
    private var citas: List<Cita>,
    private val onItemClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CitaViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.bind(cita)
        holder.btnVerDetalles.setOnClickListener { onItemClick(cita) }
    }

    override fun getItemCount() = citas.size

    fun updateData(newCitas: List<Cita>) {
        citas = newCitas
        notifyDataSetChanged()
    }

    class CitaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        private val especialidadTextView: TextView = itemView.findViewById(R.id.especialidadTextView)
        private val medicoTextView: TextView = itemView.findViewById(R.id.medicoTextView)
        private val estadoBadgeTextView: TextView = itemView.findViewById(R.id.estadoBadgeTextView)
        private val qrImageView: ImageView = itemView.findViewById(R.id.qrImageView)
        val btnVerDetalles: android.widget.Button = itemView.findViewById(R.id.btnVerDetalles)

        fun bind(cita: Cita) {
            fechaTextView.text = formatFecha(cita.fecha)
            especialidadTextView.text = cita.especialidad
            medicoTextView.text = cita.medico
            // Estado como badge de color
            android.util.Log.d("CitasAdapter", "Estado recibido: '${cita.estado}'")
            estadoBadgeTextView.text = cita.estado.capitalize(Locale.getDefault())
            when (cita.estado.lowercase()) {
                "pendiente" -> estadoBadgeTextView.setBackgroundResource(R.drawable.bg_estado_badge)
                "cancelada", "no asistí" -> estadoBadgeTextView.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F"))
                "asistí", "atendida", "finalizada" -> estadoBadgeTextView.setBackgroundColor(android.graphics.Color.parseColor("#388E3C"))
                else -> estadoBadgeTextView.setBackgroundColor(android.graphics.Color.GRAY)
            }
            // Solo generar QR si el contenido no está vacío
            if (!cita.qrCodigo.isNullOrBlank()) {
                val qrBitmap = generateQRCode(cita.qrCodigo, 80)
                qrImageView.setImageBitmap(qrBitmap)
            } else {
                qrImageView.setImageBitmap(null)
            }
        }

        private fun formatFecha(fecha: String): String {
            // Formato de fecha amigable
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("d MMM yyyy", Locale("es"))
                val date = parser.parse(fecha)
                formatter.format(date!!)
            } catch (e: Exception) {
                fecha
            }
        }

        private fun generateQRCode(text: String, size: Int): Bitmap? {
            return try {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix: BitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
                val barcodeEncoder = BarcodeEncoder()
                barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: Exception) {
                null
            }
        }
    }
} 