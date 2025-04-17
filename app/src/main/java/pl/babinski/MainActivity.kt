package pl.babinski

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.babinski.lab.Lab01Activity
import pl.babinski.lab.Lab02Activity
import pl.babinski.lab.Lab06.Lab06Activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun onClickMainBtnRunLab01(v: View){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lab01Activity::class.java)
        startActivity(intent)
    }
    fun onClickMainBtnRunLab02(v: View){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lab02Activity::class.java)
        startActivity(intent)
    }
    fun onClickMainBtnRunLab06(v: View){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lab06Activity::class.java)
        startActivity(intent)
    }
}