package pl.babinski.lab

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.babinski.R

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLab02)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun gameHandler(v:View){
        val tag: String? = v.tag as String?
        val tokens: List<String>? = tag?.split(" ")
        val rows = tokens?.get(1)?.toInt()
        val columns = tokens?.get(0)?.toInt()
        //Toast.makeText(this, "rows: ${rows}, columns: ${columns}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, Lab03Activity::class.java)

        intent.putExtra("columns", columns)
        intent.putExtra("rows",rows)

        startActivity(intent)
    }
}