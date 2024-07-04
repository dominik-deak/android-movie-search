// Attention: This app was tested with a Pixel 5 with API 30

package com.example.coursework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * The actor search screen.
 * The user can enter the name of an actor and the application will retrieve all movies
 * from the local database that contain that actor.
 * @property actorSearchField The search field.
 * @property searchActorsButton The button that searches the database.
 * @property actorSearchResults The displayed details of the retrieved movies.
 * @property database the SQLite database.
 * @property movieDao The Dao of the database.
 * @author Dominik Deak - w1778659
 */
class ActorSearch : AppCompatActivity() {
    private lateinit var actorSearchField: EditText
    private lateinit var searchActorsButton: Button
    private lateinit var actorSearchResults: TextView
    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor_search)

        actorSearchField = findViewById(R.id.actorSearchField)
        searchActorsButton = findViewById(R.id.searchActorsButton)
        actorSearchResults = findViewById(R.id.actorSearchResults)

        // Loads the displayed movies from the previous screen orientation
        if (savedInstanceState != null) {
            actorSearchResults.text = savedInstanceState.getString("actorSearchResults")
        }

        database = Room.databaseBuilder(this, AppDatabase::class.java, "movieDatabase").build()
        movieDao = database.movieDao()

        searchActorsButton.setOnClickListener {
            retrieveMovies()
        }
    }

    /**
     * Uses the user input to search the local SQLite database.
     * Displays all movies where the entered name matches one of the actors' names.
     */
    private fun retrieveMovies() {
        var result = ""
        runBlocking {
            launch {
                val input = actorSearchField.text.toString().trim()
                if (input.isEmpty()) {
                    result = "You must enter at least one character to search!"
                } else {
                    val allMovies = movieDao.getAllMovies()
                    for (movie in allMovies) {
                        if (movie.actors.contains(input, true)) {
                            result += "Title: ${movie.title}" +
                                    "\nYear: ${movie.year}" +
                                    "\nRated: ${movie.rated}" +
                                    "\nReleased: ${movie.released}" +
                                    "\nRuntime: ${movie.runtime}" +
                                    "\nGenre: ${movie.genre}" +
                                    "\nDirector: ${movie.director}" +
                                    "\nWriter: ${movie.writer}" +
                                    "\nActors: ${movie.actors}" +
                                    "\nPlot: ${movie.plot}\n\n"
                        }
                    }
                }
            }
        }
        actorSearchResults.text = result
    }

    /**
     * Saves the currently displayed search results.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("actorSearchResults", actorSearchResults.text.toString())
    }
}
