package me.berkesongur.hesapmakinesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import me.berkesongur.hesapmakinesi.islemler.Islem

fun String.replace(vararg replacements: Pair<String, String>): String {
    var result = this
    replacements.forEach { (l, r) -> result = result.replace(l, r) }
    return result
}


class MainActivity : AppCompatActivity() {

    private var current: String = ""
    private var input: String = ""
    private var current_process: String = ""
    private var cursor: String = "left"

    private lateinit var hScreen: TextView

    private var process_list: ArrayList<String> = arrayListOf(
        "sum", "sub", "mult", "divide"
    )

    private var process_btns: HashMap<String, Button> = HashMap()
    private var numeric_btns: ArrayList<Button> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hScreen = findViewById(R.id.h_screen)

        resetScreen()

        numeric_btns = arrayListOf(
            findViewById(R.id.btn_zero),
            findViewById(R.id.btn_one),
            findViewById(R.id.btn_two),
            findViewById(R.id.btn_three),
            findViewById(R.id.btn_four),
            findViewById(R.id.btn_five),
            findViewById(R.id.btn_six),
            findViewById(R.id.btn_seven),
            findViewById(R.id.btn_eight),
            findViewById(R.id.btn_nine)
        )

        process_btns["sum"] = findViewById(R.id.btn_sum)
        process_btns["sub"] = findViewById(R.id.btn_sub)
        process_btns["mult"] = findViewById(R.id.btn_mult)
        process_btns["divide"] = findViewById(R.id.btn_divide)
        process_btns["equals"] = findViewById(R.id.btn_equals)
        process_btns["clear"] = findViewById(R.id.btn_clear)
        process_btns["dot"] = findViewById(R.id.btn_dot)
        process_btns["erase"] = findViewById(R.id.btn_erase)


        registerNumericInputs()
        registerProcess()

    }

    private fun registerNumericInputs(): Unit {
        for(i in numeric_btns.indices) {
            numeric_btns[i].setOnClickListener {
                if(current.isNotEmpty()) {
                    val fc: List<String> = current.split(".")
                    var _f = fc[0]
                    var _s = ""
                    for(j in fc.indices) {
                        when(cursor) {
                            "left" -> {
                                _f = fc[0] + i
                            }
                            "right" -> {
                                if(fc.indices.count() == 1) _s = "" + i
                                else _s = fc[1] + i
                            }
                        }
                    }
                    current = if(_s.isNotEmpty()) "$_f.$_s" else _f
                }
                if(current.isEmpty()) {
                    current += i
                }
                hScreen.text = current
            }
        }
    }

    private fun registerProcess(): Unit {

        process_btns.getValue("clear").setOnClickListener {
            resetCursor()
            clearInputValue()
            clearCurrentValue()
            clearCurrentProcess()
            resetScreen()
        }

        process_btns.getValue("dot").setOnClickListener {
            cursor = "right"
            hScreen.text = current + "."
        }

        process_btns.getValue("erase").setOnClickListener {
            eraseChar()
        }

        process_btns.getValue("equals").setOnClickListener {
            if(current_process.isEmpty() || input.isEmpty() || current.isEmpty()) return@setOnClickListener
            processCurrent()
        }

        for(proc in process_list) {
            process_btns.getValue(proc).setOnClickListener {
                if(current.isEmpty() && input.isEmpty()) return@setOnClickListener
                if(current.isNotEmpty() && input.isNotEmpty() && current_process.isNotEmpty()) processCurrent()
                if(current.isNotEmpty() && input.isEmpty()) {
                    current_process = proc
                    input = current
                    clearCurrentValue()
                }
            }
        }
    }

    private fun processCurrent() {
        when(current_process) {
            "sum" -> {
                current = Islem().Sum(input.toFloat(), current.toFloat()).toString()
            }
            "sub" -> {
                current = Islem().Sub(input.toFloat(), current.toFloat()).toString()
            }
            "mult" -> {
                current = Islem().Mult(input.toFloat(), current.toFloat()).toString()
            }
            "divide" -> {
                if(current.toFloat() == 0f) {
                    clearInputValue()
                    clearCurrentValue()
                    clearCurrentProcess()
                    hScreen.text = "NaN"
                    return
                }
                current = Islem().Divide(input.toFloat(), current.toFloat()).toString()
                cursor = "left"
            }
        }


        val cs = current.split(".")
        if(cs.indices.count() == 2 && cs[1] == "0") hScreen.text = current.toFloat().toInt().toString()
        else hScreen.text = current

        clearCurrentProcess()
        clearInputValue()
   }
   private fun clearCurrentValue() {
       current = ""
   }
   private fun clearInputValue() {
       input = ""
   }
   private fun clearCurrentProcess() {
       current_process = ""
   }
   private fun resetScreen() {
       hScreen.text = "0.0"
   }
   private fun resetCursor() {
       cursor = "left"
   }
   private fun eraseChar() {
       if(current.isEmpty()) return
       current = current.substring(0, current.length - 1)
       hScreen.text = current
   }
}