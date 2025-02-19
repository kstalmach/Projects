package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val REQUEST_CODE_PERMISSION = 200

class RecordingActivity : AppCompatActivity() {

    private var permission = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var audioRecorder: AudioRecord
    private lateinit var saveButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var recordButton: ImageButton
    private lateinit var filenameInput: EditText
    private lateinit var timerTextView: TextView

    private var dirPath = ""
    private var filename = ""
    private var isRecording = false

    // Counter
    private var seconds = 0
    private var running = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recordButton = findViewById(R.id.recordButton)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
        filenameInput = findViewById(R.id.filenameInput)
        timerTextView = findViewById(R.id.timer)

        permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            permission[0]
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE_PERMISSION)
        }

        filenameInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(filenameInput.windowToken, 0)
                filenameInput.clearFocus()
                true
            } else {
                false
            }
        }

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
                stopTimer()
            } else {
                startRecording()
                startTimer()
            }
        }

        saveButton.setOnClickListener {
            if (!isRecording && filename.isNotEmpty()) {
                showToast("Nagranie zapisane w: $filename")
            } else {
                showToast("Brak nagrania do zapisania.")
            }
        }

        deleteButton.setOnClickListener {
            val file = File(filename)
            if (file.exists()) {
                file.delete()
                showToast("Plik usunięty")
                resetTimer()
                filename = ""  // Clear the filename after deletion
            } else {
                showToast("Brak pliku do usunięcia")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {
        if (!permissionGranted) {
            showToast("Brak uprawnień do nagrywania.")
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE_PERMISSION)
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            showToast("Brak uprawnień do nagrywania.")
            return
        }
        try {
            dirPath = Environment.getExternalStorageDirectory().absolutePath + "/Recordings"
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val userInputFilename = filenameInput.text.toString().trim()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            filename = if (userInputFilename.isNotEmpty()) {
                "$dirPath/$userInputFilename.wav"
            } else {
                "$dirPath/REC_$timeStamp.wav"
            }

            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecorder.startRecording()
            isRecording = true

            val audioData = ShortArray(bufferSize)
            val recordingThread = Thread {
                writeAudioDataToWavFile(audioData, bufferSize, sampleRate)
            }
            recordingThread.start()

            recordButton.setImageResource(R.drawable.pause_button)
            showToast("Nagranie rozpoczęte.")

        } catch (e: SecurityException) {
            showToast("Wystąpił błąd bezpieczeństwa podczas próby nagrywania: ${e.message}")
        } catch (e: IOException) {
            showToast("Wystąpił błąd podczas nagrywania: ${e.message}")
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            isRecording = false
            audioRecorder.stop()
            audioRecorder.release()
            recordButton.setImageResource(R.drawable.record_button)
            showToast("Nagrywanie zatrzymane. Zapisz nagranie za pomocą przycisku zapisu.")
        }
    }

    private fun writeAudioDataToWavFile(audioData: ShortArray, bufferSize: Int, sampleRate: Int) {
        val wavFile = File(filename)
        var outputStream: FileOutputStream? = null

        try {
            outputStream = FileOutputStream(wavFile)
            writeWavHeader(outputStream, sampleRate, bufferSize)

            while (isRecording) {
                val readSize = audioRecorder.read(audioData, 0, bufferSize)
                if (readSize > 0) {
                    val byteBuffer = ByteBuffer.allocate(readSize * 2)
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    byteBuffer.asShortBuffer().put(audioData, 0, readSize)
                    outputStream.write(byteBuffer.array(), 0, readSize * 2)
                }
            }

            updateWavHeader(outputStream)
        } catch (e: IOException) {
            Log.e("RecordingActivity", "Error writing to file", e)
        } finally {
            outputStream?.close()
        }
    }

    private fun writeWavHeader(outputStream: FileOutputStream, sampleRate: Int, bufferSize: Int) {
        val channels = 1
        val byteRate = sampleRate * 2 * channels

        outputStream.write(byteArrayOf(82, 73, 70, 70)) // "RIFF"
        outputStream.write(intToByteArray(36 + bufferSize * 2)) // Chunk size
        outputStream.write(byteArrayOf(87, 65, 86, 69)) // "WAVE"
        outputStream.write(byteArrayOf(102, 109, 116, 32)) // "fmt "
        outputStream.write(intToByteArray(16)) // Subchunk1 size
        outputStream.write(shortToByteArray(1)) // Audio format (1 = PCM)
        outputStream.write(shortToByteArray(channels.toShort())) // Number of channels
        outputStream.write(intToByteArray(sampleRate)) // Sample rate
        outputStream.write(intToByteArray(byteRate)) // Byte rate
        outputStream.write(shortToByteArray((2 * channels).toShort())) // Block align
        outputStream.write(shortToByteArray(16)) // Bits per sample
        outputStream.write(byteArrayOf(100, 97, 116, 97)) // "data"
        outputStream.write(intToByteArray(bufferSize * 2)) // Subchunk2 size
    }

    private fun updateWavHeader(outputStream: FileOutputStream) {
        outputStream.channel.position(4)
        outputStream.write(intToByteArray((outputStream.channel.size() - 8).toInt()))
        outputStream.channel.position(40)
        outputStream.write(intToByteArray((outputStream.channel.size() - 44).toInt()))
    }

    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()
    }

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (running) {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val secs = seconds % 60
                val time =
                    String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
                timerTextView.text = time

                seconds++
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startTimer() {
        running = true
        handler.post(updateTimerRunnable)
    }

    private fun stopTimer() {
        running = false
    }

    private fun resetTimer() {
        stopTimer()
        seconds = 0
        timerTextView.text = "00:00:00"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
