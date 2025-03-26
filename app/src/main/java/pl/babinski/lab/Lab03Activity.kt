package pl.babinski.lab

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import java.util.Stack
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val columns = intent.getIntExtra("columns", 3)
        val rows = intent.getIntExtra("rows", 3)

        var mBoard:GridLayout=findViewById(R.id.main_Grid)
        mBoard.columnCount=columns
        mBoard.rowCount=rows


        val mBoardModel = MemoryBoardView(mBoard, columns, rows)

        /*runOnUiThread() {
            mBoardModel.setOnGameChangeListener { e ->
                run {
                    when (e.state) {
                        GameStates.Matching -> {
                            e.tiles.map { tile: Tile -> tile.revealed = true }
                        }

                        GameStates.Match -> {
                            e.tiles.map { tile: Tile -> tile.revealed = true }
                        }

                        GameStates.NoMatch -> {
                            e.tiles.map { tile: Tile -> tile.revealed = true }
                            Timer().schedule(2000) {
                                // kod wykonany po 2000 ms
                                e.tiles.map { tile: Tile -> tile.revealed = false }
                            }
                        }

                        GameStates.Finished -> {
                            Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }*/

        /*for (col in 0 until columns){
            for (row in 0 until rows){


                val btn = ImageButton(this).also {
                    it.tag = "${col}x${row}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(R.drawable.baseline_star_border_24)
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams

                    mBoard.addView(it)
                }

            }
        }*/
    }


}

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_24,
        R.drawable.baseline_water_drop_24,
        R.drawable.baseline_star_border_24,
        R.drawable.baseline_air_24,
        // dodaj kolejne identyfikatory utworzonych ikon
    )
    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        // tu umieść kod pętli tworzący wszystkie karty, który jest obecnie
        // w aktywności Lab03Activity
        for (col in 0 until cols){
            for (row in 0 until rows){


                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${col}x${row}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(R.drawable.baseline_star_border_24)
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams


                }
                gridLayout.addView(btn)
                addTile(btn,shuffledIcons.last())
                shuffledIcons.removeLast()
            }
        }
    }
    private val deckResource: Int = R.drawable.baseline_hourglass_full_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        Log.e("tile","tile clicked")
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource?:-1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)

        tiles[button.tag.toString()] = tile
    }
}

//logika karty
data class Tile(val button: ImageButton, val tileResource: Int, val deckResource: Int) {
    init {
        button.setImageResource(deckResource)
    }
    private var _revealed: Boolean = false
    var revealed: Boolean
        get() {
            return _revealed
        }
        set(value){
            _revealed = value
            if(_revealed){
                button.setImageResource(tileResource)
            }else{
                button.setImageResource(deckResource)
            }
        }
    fun removeOnClickListener(){
        button.setOnClickListener(null)
    }
}
enum class GameStates {
    Matching, Match, NoMatch, Finished
}

//logika przebiegu  gry
class MemoryGameLogic(private val maxMatches: Int) {

    private var valueFunctions: MutableList<() -> Int> = mutableListOf()

    private var matches: Int = 0

    fun process(value: () -> Int):  GameStates{
        if (valueFunctions.size < 1) {
            valueFunctions.add(value)
            return GameStates.Matching
        }
        valueFunctions.add(value)
        val result = valueFunctions[0]() == valueFunctions[1]()
        matches += if (result) 1 else 0
        valueFunctions.clear()
        return when (result) {
            true -> if (matches == maxMatches) GameStates.Finished else GameStates.Match
            false -> GameStates.NoMatch
        }
    }
}
data class MemoryGameEvent(
    val tiles: List<Tile>,
    val state: GameStates) {
}

