package pl.babinski.lab

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.Image
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem


import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
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
import kotlin.random.Random

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoardModel: MemoryBoardView
    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePLayer: MediaPlayer
    var isSound:Boolean=true

    override fun onCreateOptionsMenu(menu: Menu): Boolean  {
       /* val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.board_menu_activity, menu)*/
        menuInflater.inflate(R.menu.board_menu_activity,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                if (isSound) {
                    Toast.makeText(this, "Sound turned off", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_alarm_off_24)
                } else {
                    Toast.makeText(this, "Sound turned on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_alarm_24)
                }
                isSound = !isSound
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override protected fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }


    override protected fun onPause() {
        super.onPause();
        completionPlayer.release()
        negativePLayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gameState=mBoardModel.getState()
        outState.putSerializable("savedGameState",gameState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)
        setSupportActionBar(findViewById(R.id.toolbar))
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


        mBoardModel = MemoryBoardView(mBoard, columns, rows)
        if (savedInstanceState != null){
            val savedGameInstace=savedInstanceState.getSerializable("savedGameState") as? HashMap<String, String>
            /*Log.e("konrtola",savedGameInstace.toString())*/
            if (savedGameInstace != null) {
                for(entry in savedGameInstace){
                    val tag:String=entry.key
                    val value=entry.value.split(",")
                    val resourceId=value[0].toInt()
                    var revealed:Boolean
                    if(value[1]=="true"){
                        revealed=true;
                    }else{
                        revealed=false;
                    }

                    mBoardModel.setFromSave(tag, resourceId, revealed)

                }
            }
        }

        runOnUiThread() {
            mBoardModel.setOnGameChangeListener { e ->
                run {
                    when (e.state) {
                        GameStates.Matching -> {

                            e.tiles.map { tile: Tile ->

                                tile.flipTile()
                                tile.revealed = true
                            }
                        }

                        GameStates.Match -> {
                            if (isSound) {
                                completionPlayer.start()
                            }
                            e.tiles.map { tile: Tile ->

                                tile.flipTile()
                                tile.revealed = true
                                tile.paired=true
                                tile.animatePairedButton(Runnable {
                                    tile.button.visibility = View.INVISIBLE
                                })
                                tile.removeOnClickListener()
                              }
                        }

                        GameStates.NoMatch -> {
                            if (isSound) {
                                negativePLayer.start()
                            }
                            e.tiles.map { tile: Tile ->

                                tile.flipTile()
                                tile.revealed = true
                                tile.shakeTile()
                            }
                            Timer().schedule(1000) {
                                e.tiles.map { tile: Tile -> if(tile.paired!=true)tile.revealed = false }
                            }
                        }

                        GameStates.Finished -> {
                            if (isSound) {
                                completionPlayer.start()
                            }
                            e.tiles.map { tile: Tile ->

                                tile.flipTile()
                                tile.revealed = true
                                tile.paired=true
                                tile.animatePairedButton(Runnable {
                                    tile.button.visibility = View.INVISIBLE
                                })
                                tile.removeOnClickListener()
                            }
                            Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

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


    private val deckResource: Int= R.drawable.baseline_hourglass_full_24
    private val tiles: MutableMap<String, Tile> = mutableMapOf()

    fun setFromSave(tag:String,resourceId:Int,reveal:Boolean){
        val tile = tiles[tag] ?: return
        tile.tileResource = resourceId
        tile.revealed = reveal
        tile.paired = reveal
       /* Log.e("kontrolpairedcheck",tiles[tag]?.paired.toString())*/
        /*if (reveal) {
            tile.button.setImageResource(resourceId)
        } else {
            tile.button.setImageResource(deckResource)
        }*/
    }
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_24,
        R.drawable.baseline_water_drop_24,
        R.drawable.baseline_star_border_24,
        R.drawable.baseline_air_24,
        R.drawable.baseline_public_24,
        R.drawable.baseline_workspace_premium_24,
        R.drawable.baseline_whatshot_24,
        R.drawable.baseline_sunny_24,
        R.drawable.baseline_cloud_24,
        R.drawable.baseline_timer_24,
        R.drawable.baseline_battery_saver_24,
        R.drawable.baseline_directions_car_24,
        R.drawable.baseline_computer_24,
        R.drawable.baseline_person_24,
        R.drawable.baseline_front_hand_24,
        R.drawable.baseline_back_hand_24,
        R.drawable.baseline_attach_money_24,
        R.drawable.baseline_menu_book_24

        // dodaj kolejne identyfikatory utworzonych ikon
    )
    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

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

    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag.toString()]?:return

        matchedPair.push(tile)
        if(matchedPair.size<1)return

        val matchResult = logic.process { tile.tileResource }
        matchedPair.map{tile:Tile->
            Log.e("kontrola",tile.button.tag.toString())}
        if(matchedPair.size==2 && matchedPair[0].button.tag==matchedPair[1].button.tag){
            onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), GameStates.NoMatch))
            matchedPair.clear()
            return
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
    fun getState():HashMap<String, String>{
        val serializableState = HashMap<String, String>()
        for(entry in tiles){
            val state:String=entry.value.tileResource.toString()+","+entry.value.paired.toString()
            serializableState[entry.key]=state
        }

        return serializableState
    }

}

//logika karty
data class Tile(val button: ImageButton, var tileResource: Int, val deckResource: Int) {
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
    private var _paired:Boolean=false
    var paired:Boolean
        get(){
            return _paired
        }
        set(value){
            _paired=value
        }
    fun removeOnClickListener(){
        button.setOnClickListener(null)
    }
    fun animatePairedButton(action: Runnable ) {
        val button=this.button
        val set = AnimatorSet()
        /*button.pivotX = 200f
        button.pivotY = 200f*/

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scallingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.5f)
        val scallingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.5f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scallingX, scallingY, fade)
        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0f
               /* button.backgroundTintList = ColorStateList.valueOf(Color.CYAN)*/

            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }
    fun flipTile() {
       /* button.animate()
            .rotationY(90f) // Rotate halfway
            .setDuration(150)
            .withEndAction {
                // Change image when halfway through
                button.setImageResource(if (revealed) tileResource else deckResource)
                button.rotationY = -90f // Reverse rotation
                button.animate().rotationY(0f).setDuration(150).start() // Rotate back to 0°
            }
            .start()*/
        val button=this.button
        val set = AnimatorSet()
        button.rotationY = 0f

        val rotationY = ObjectAnimator.ofFloat(button, "rotationY", 180f)
        set.play(rotationY)
        set.duration = 150

        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }
            override fun onAnimationEnd(animator: Animator) {
                /*button.setImageResource(if (revealed) tileResource else deckResource)*/
                /*if(this@Tile.revealed==false) {
                    this@Tile.revealed = true
                }else{
                    this@Tile.revealed = false
                }*/
            }
            override fun onAnimationCancel(animator: Animator) {
            }
            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }
    fun shakeTile() {
        val shake = ObjectAnimator.ofFloat(button, "translationX", -30f, 30f, -20f, 20f, -10f, 10f, 0f)
        shake.duration = 500
        shake.interpolator = DecelerateInterpolator()
        shake.start()
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

