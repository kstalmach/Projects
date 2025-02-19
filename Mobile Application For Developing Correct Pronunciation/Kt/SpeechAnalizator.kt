package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.exp

class SpeechAnalizator : AppCompatActivity() {

    private lateinit var fileSelectedText: TextView
    private lateinit var resultText: TextView
    private lateinit var confidenceText: TextView
    private lateinit var signalImageView: SubsamplingScaleImageView
    private lateinit var spectrogramImageView: SubsamplingScaleImageView

    private val LABEL_SZAFA = listOf("Szafa Dobrze", "Szafa Źle")
    private val LABEL_SZOPA = listOf("Szopa Dobrze", "Szopa Źle")
    private val LABEL_SZELKI = listOf("Szelki Dobrze", "Szelki Źle")
    private val LABEL_KASZA = listOf("Kasza Dobrze", "Kasza Źle")
    private val LABEL_NOSZE = listOf("Nosze Dobrze", "Nosze Źle")
    private val LABEL_WIESZAK = listOf("Wieszak Dobrze", "Wieszak Źle")
    private val LABEL_KOSZ = listOf("Kosz Dobrze", "Kosz Źle")
    private val LABEL_AFISZ = listOf("Afisz Dobrze", "Afisz Źle")
    private val LABEL_GULASZ = listOf("Gulasz Dobrze", "Gulasz Źle")

    private val FILE_TYPE_AUDIO = "audio/*"
    private val FILE_SELECTED_PREFIX = "Wybrany plik:"
    private val PYTHON_MODULE_NAME = "model_skrypt"
    private val PYTHON_ANALYZE_METHOD = "analyze"
    private val ERROR_MESSAGE_ANALYSIS = "Błąd podczas analizy pliku"
    private val SUCCESS_MESSAGE_MODEL = "Model załadowany poprawnie"
    private val UNKNOWN_LABEL = "Nieznana etykieta"
    private val RESULT_PREFIX = "Przewidywany wynik"
    private val CONFIDENCE_PREFIX = "Pewność"
    private val FILE_NOT_FOUND_ERROR = "Nie znaleziono pliku"
    private val DEFAULT_AUDIO_FILE_NAME = "input_audio.wav"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analizator)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        fileSelectedText = findViewById(R.id.selectedFile)
        resultText = findViewById(R.id.resultText)
        confidenceText = findViewById(R.id.confidenceText)
        signalImageView = findViewById(R.id.signalImageView)
        spectrogramImageView = findViewById(R.id.spectrogramImageView)

        findViewById<Button>(R.id.goToRecordingButton).setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.selectFileButton).setOnClickListener {
            selectFile()
        }

        findViewById<Button>(R.id.analizeButton).setOnClickListener {
            val selectedFileUri = fileSelectedText.tag as? Uri
            selectedFileUri?.let {
                startAnalysis(it)
            }
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = FILE_TYPE_AUDIO
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri = data?.data
            selectedFileUri?.let {
                fileSelectedText.text = "$FILE_SELECTED_PREFIX ${it.path}"
                fileSelectedText.tag = it
                fileSelectedText.visibility = TextView.VISIBLE
            }
        }
    }

    fun loadSpectrogramValuesFromFile(filePath: String, width: Int, height: Int): ByteBuffer {
        val file = File(filePath)
        val valuesString = file.readText()

        val valuesList = valuesString
            .lines()
            .filter { it.isNotBlank() }
            .mapNotNull {
                try {
                    it.toFloat()
                } catch (e: NumberFormatException) {
                    println("Nieprawidłowa wartość: $it")
                    null
                }
            }

        if (valuesList.size != width * height) {
            throw IllegalArgumentException("Liczba wartości w pliku nie zgadza się z wymiarami spektrogramu")
        }

        val byteBuffer = ByteBuffer.allocateDirect(width * height * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        valuesList.forEach {
            byteBuffer.putFloat(it)
        }

        byteBuffer.rewind()

        return byteBuffer
    }

    private fun startAnalysis(fileUri: Uri) {
        try {
            val filePath = getFilePathFromUri(fileUri)

            val python = Python.getInstance()
            val pythonFile = python.getModule(PYTHON_MODULE_NAME)
            val result = pythonFile.callAttr(PYTHON_ANALYZE_METHOD, filePath)

            val resultList = result.asList()
            if (resultList.size < 3) {
                Log.e("SpeechAnalizator", "Unexpected result size: ${resultList.size}. Expected at least 3.")
                showToast(ERROR_MESSAGE_ANALYSIS)
                return
            }

            val waveformPath = resultList[0].toString()
            val spectrogramPath = resultList[1].toString()
            val spectrogramModel = resultList[2].toString()

            val waveformBitmap = BitmapFactory.decodeFile(waveformPath)
            val spectrogramBitmap = BitmapFactory.decodeFile(spectrogramPath)
            signalImageView.setImage(ImageSource.bitmap(waveformBitmap))
            spectrogramImageView.setImage(ImageSource.bitmap(spectrogramBitmap))

            val word = intent.getStringExtra("word") ?: ""

            val labelMap = mapOf(
                "szafa" to LABEL_SZAFA,
                "szopa" to LABEL_SZOPA,
                "szelki" to LABEL_SZELKI,
                "kasza" to LABEL_KASZA,
                "nosze" to LABEL_NOSZE,
                "wieszak" to LABEL_WIESZAK,
                "kosz" to LABEL_KOSZ,
                "afisz" to LABEL_AFISZ,
                "gulasz" to LABEL_GULASZ
            )

            val classLabels = labelMap[word.lowercase()] ?: throw IllegalArgumentException("Unknown word: $word")

            val formattedWord = formatWordForModelClass(word)
            val modelClassName = "com.example.myapplication.ml.Model$formattedWord"
            val modelClass = Class.forName(modelClassName)
            val modelInstance = modelClass.getMethod("newInstance", Context::class.java).invoke(null, applicationContext)

            val byteBuffer = loadSpectrogramValuesFromFile(spectrogramModel, 124, 129)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 124, 129, 1), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            val processMethod = modelClass.getMethod("process", TensorBuffer::class.java)
            val outputs = processMethod.invoke(modelInstance, inputFeature0)
            val outputTensorBufferMethod = outputs.javaClass.getMethod("getOutputFeature0AsTensorBuffer")
            val outputFeature0 = outputTensorBufferMethod.invoke(outputs) as TensorBuffer

            modelClass.getMethod("close").invoke(modelInstance)
            showToast(SUCCESS_MESSAGE_MODEL)

            val outputArray = outputFeature0.floatArray
            val probabilities = softmax(outputArray)

            val predictedIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            val predictedLabel = classLabels.getOrElse(predictedIndex) { UNKNOWN_LABEL }
            val confidence = probabilities.getOrElse(predictedIndex) { 0.0f } * 100

            val formattedConfidence = String.format("%.2f", confidence)

            resultText.text = "$RESULT_PREFIX: $predictedLabel"
            confidenceText.text = "$CONFIDENCE_PREFIX: $formattedConfidence %"

        } catch (e: Exception) {
            Log.e("SpeechAnalizator", "Error during analysis", e)
            showToast("$ERROR_MESSAGE_ANALYSIS: ${e.message}")
        }
    }

    fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0.0f
        val expValues = logits.map { exp(it - maxLogit) }
        val sumExpValues = expValues.sum()
        return expValues.map { it / sumExpValues }.toFloatArray()
    }

    fun formatWordForModelClass(word: String?): String {
        return word?.lowercase()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: ""
    }

    private fun getFilePathFromUri(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri) ?: throw FileNotFoundException("$FILE_NOT_FOUND_ERROR: $uri")
        val file = File(filesDir, DEFAULT_AUDIO_FILE_NAME)
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file.absolutePath
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_SELECT_FILE = 1001
    }
}