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
                adapter = CitasAdapter(generateDummyData()) { cita ->
                    showExpandedQRDialog(cita)
                }
                recyclerView.adapter = adapter
                Log.d(TAG, "Adaptador asignado correctamente")
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

    private fun showExpandedQRDialog(cita: Cita) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_qr_expanded)

            // Configurar vistas del diálogo
            dialog.findViewById<ImageButton>(R.id.btnCerrar)?.setOnClickListener {
                dialog.dismiss()
            }

            // Mostrar información expandida
            dialog.findViewById<TextView>(R.id.fechaExpandedTextView)?.text = cita.fecha
            dialog.findViewById<TextView>(R.id.especialidadExpandedTextView)?.text = cita.especialidad
            dialog.findViewById<TextView>(R.id.medicoExpandedTextView)?.text = cita.medico

            // Generar y mostrar QR expandido
            val qrExpandedImageView = dialog.findViewById<ImageView>(R.id.qrExpandedImageView)
            val qrBitmap = generateQRCode(cita.qrCodigo, 300)
            qrExpandedImageView?.setImageBitmap(qrBitmap)

            dialog.show()
            Log.d(TAG, "Diálogo QR mostrado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al mostrar el diálogo QR: ${e.message}")
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

    private fun generateDummyData(): List<Cita> {
        return try {
            listOf(
                Cita("2024-03-20", "Cardiología", "Dr. Juan Pérez", "Pendiente", "CITA-001"),
                Cita("2024-03-22", "Pediatría", "Dra. María García", "Confirmada", "CITA-002"),
                Cita("2024-03-25", "Dermatología", "Dr. Carlos López", "Pendiente", "CITA-003")
            ).also {
                Log.d(TAG, "Datos de prueba generados: ${it.size} citas")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al generar datos de prueba: ${e.message}")
            emptyList()
        }
    }
}

// Modelo de datos para Cita
data class Cita(
    val fecha: String,
    val especialidad: String,
    val medico: String,
    val estado: String,
    val qrCodigo: String
)

// Adaptador para el RecyclerView
class CitasAdapter(
    private val citas: List<Cita>,
    private val onItemClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CitaViewHolder {
        return try {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cita, parent, false)
            CitaViewHolder(view)
        } catch (e: Exception) {
            Log.e("CitasAdapter", "Error en onCreateViewHolder: ${e.message}")
            throw e
        }
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        try {
            val cita = citas[position]
            holder.bind(cita)
            holder.itemView.setOnClickListener { onItemClick(cita) }
        } catch (e: Exception) {
            Log.e("CitasAdapter", "Error en onBindViewHolder: ${e.message}")
        }
    }

    override fun getItemCount() = citas.size

    class CitaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        private val especialidadTextView: TextView = itemView.findViewById(R.id.especialidadTextView)
        private val medicoTextView: TextView = itemView.findViewById(R.id.medicoTextView)
        private val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        private val qrImageView: ImageView = itemView.findViewById(R.id.qrImageView)

        fun bind(cita: Cita) {
            try {
                fechaTextView.text = cita.fecha
                especialidadTextView.text = cita.especialidad
                medicoTextView.text = cita.medico
                estadoTextView.text = cita.estado

                // Generar QR para la vista previa
                val qrBitmap = generateQRCode(cita.qrCodigo, 100)
                qrImageView.setImageBitmap(qrBitmap)
            } catch (e: Exception) {
                Log.e("CitaViewHolder", "Error en bind: ${e.message}")
            }
        }

        private fun generateQRCode(text: String, size: Int): Bitmap? {
            return try {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix: BitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
                val barcodeEncoder = BarcodeEncoder()
                barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: Exception) {
                Log.e("CitaViewHolder", "Error al generar QR: ${e.message}")
                null
            }
        }
    }
} 