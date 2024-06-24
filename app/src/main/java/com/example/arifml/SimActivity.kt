package com.example.arifml

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "loan.tflite"

    private lateinit var resultText: TextView
    private lateinit var Gender: EditText
    private lateinit var Married: EditText
    private lateinit var Dependents: EditText
    private lateinit var Education: EditText
    private lateinit var Self_Employed: EditText
    private lateinit var ApplicantIncome: EditText
    private lateinit var CoapplicantIncom: EditText
    private lateinit var LoanAmount: EditText
    private lateinit var Loan_Amount_Term: EditText
    private lateinit var Credit_History: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sim)

        resultText = findViewById(R.id.txtResult)
        Gender = findViewById(R.id.Gender)
        Married = findViewById(R.id.Married)
        Dependents = findViewById(R.id.Dependents)
        Education = findViewById(R.id.Education)
        Self_Employed = findViewById(R.id.Self_Employed)
        ApplicantIncome = findViewById(R.id.ApplicantIncome)
        CoapplicantIncom = findViewById(R.id.CoapplicantIncom)
        LoanAmount = findViewById(R.id.LoanAmount)
        Loan_Amount_Term = findViewById(R.id.Loan_Amount_Term)
        Credit_History = findViewById(R.id.Credit_History)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Gender.text.toString(),
                Married.text.toString(),
                Dependents.text.toString(),
                Education.text.toString(),
                Self_Employed.text.toString(),
                ApplicantIncome.text.toString(),
                CoapplicantIncom.text.toString(),
                LoanAmount.text.toString(),
                Loan_Amount_Term.text.toString(),
                Credit_History.text.toString())

            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Urban"
                }else if (result == 1){
                    resultText.text = "Semiurban"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(11)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String, input10: String): Int{
        val inputVal = FloatArray(10)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        inputVal[9] = input10.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}