// Attention: This app was tested with a Pixel 5 with API 30

package com.example.coursework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * The movie search screen.
 * The user can enter the name of a movie, and it will retrieved from the OMDb API web service if it exists.
 * The user can save the retrieved movie into the local database.
 * @property movieSearchField The search field.
 * @property retrieveMovieButton The button that retrieves the movies.
 * @property saveMovieButton The button that saves the movies.
 * @property movieSearchResult The displayed details of the retrieved movie.
 * @property database the SQLite database.
 * @property movieDao The Dao of the database.
 * @author Dominik Deak - w1778659
 */
class MovieSearch : AppCompatActivity() {
    private lateinit var movieSearchField: EditText
    private lateinit var retrieveMovieButton: Button
    private lateinit var saveMovieButton: Button
    private lateinit var movieSearchResult: TextView
    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao
    private var displayedMovie: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_search)

        movieSearchField = findViewById(R.id.movieSearchField)
        retrieveMovieButton = findViewById(R.id.retrieveMovieButton)
        saveMovieButton = findViewById(R.id.saveMovieButton)
        movieSearchResult = findViewById(R.id.movieSearchResult)

        // Loads the displayed movie from the previous screen orientation
        if (savedInstanceState != null) {
            displayedMovie = savedInstanceState.getString("displayedMovie")?.let { JSONObject(it) }
            val title = displayedMovie!!.getString("Title")
            val year = displayedMovie!!.getString("Year")
            val rated = displayedMovie!!.getString("Rated")
            val released = displayedMovie!!.getString("Released")
            val runtime = displayedMovie!!.getString("Runtime")
            val genre = displayedMovie!!.getString("Genre")
            val director = displayedMovie!!.getString("Director")
            val writer = displayedMovie!!.getString("Writer")
            val actors = displayedMovie!!.getString("Actors")
            val plot = displayedMovie!!.getString("Plot")
            val result = "Title: $title\nYear: $year\nRated: $rated" +
                    "\nReleased: $released\nRuntime: $runtime\nGenre: $genre" +
                    "\nDirector: $director\nWriter: $writer\nActors: $actors" +
                    "\n\nPlot: $plot"
            movieSearchResult.text = result
        }

        database = Room.databaseBuilder(this, AppDatabase::class.java, "movieDatabase").build()
        movieDao = database.movieDao()

        retrieveMovieButton.setOnClickListener {
            retrieveMovie()
        }
        saveMovieButton.setOnClickListener {
            saveMovie()
        }
    }

    /**
     * Uses the user input to retrieve a movie from the API if one exists with the same title.
     * Builds a string with the retrieved JSON object and displays it on the screen.
     */
    private fun retrieveMovie() {
        val apiKey: String = resources.getString(R.string.omdbApiKey)
        val stringBuilder = StringBuilder("")
        var result = ""

        runBlocking {
            launch {
                withContext(Dispatchers.IO) {
                    val input = movieSearchField.text.toString().trim()
                    if (input.isEmpty()) {
                        result = "You must enter at least one character to search!"
                    } else {
                        val url = URL("https://www.omdbapi.com/?t=$input&apikey=$apiKey")
                        val connection = url.openConnection() as HttpURLConnection
                        val bufferedReader: BufferedReader
                        try {
                            bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                            return@withContext
                        }

                        var line = bufferedReader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = bufferedReader.readLine()
                        }

                        val movie = JSONObject(stringBuilder.toString())
                        val response = movie.getString("Response")
                        if (response == "True") {
                            val title = movie.getString("Title")
                            val year = movie.getString("Year")
                            val rated = movie.getString("Rated")
                            val released = movie.getString("Released")
                            val runtime = movie.getString("Runtime")
                            val genre = movie.getString("Genre")
                            val director = movie.getString("Director")
                            val writer = movie.getString("Writer")
                            val actors = movie.getString("Actors")
                            val plot = movie.getString("Plot")
                            result = "Title: $title\nYear: $year\nRated: $rated" +
                                    "\nReleased: $released\nRuntime: $runtime\nGenre: $genre" +
                                    "\nDirector: $director\nWriter: $writer\nActors: $actors" +
                                    "\n\nPlot: $plot"
                            displayedMovie = movie
                        } else {
                            result = movie.getString("Error")
                            displayedMovie = null
                        }
                    }
                }
            }
        }
        movieSearchResult.text = result
    }

    /**
     * Saves the currently displayed movie to the SQLite database and displays a success message.
     * If nothing is displayed, displays a message letting the user know about this.
     */
    private fun saveMovie() {
        if (displayedMovie != null) {
            val movieName = displayedMovie!!.getString("Title")
            runBlocking {
                launch {
                    val movie = Movie(
                        displayedMovie!!.getString("Title"),
                        displayedMovie!!.getString("Year"),
                        displayedMovie!!.getString("Rated"),
                        displayedMovie!!.getString("Released"),
                        displayedMovie!!.getString("Runtime"),
                        displayedMovie!!.getString("Genre"),
                        displayedMovie!!.getString("Director"),
                        displayedMovie!!.getString("Writer"),
                        displayedMovie!!.getString("Actors"),
                        displayedMovie!!.getString("Plot")
                    )
                    movieDao.insertMovies(movie)
                }
            }
            Toast.makeText(this, "Successfully added '$movieName' to database", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You have to search for a movie first", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Saves the currently displayed movie.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (displayedMovie != null) {
            outState.putString("displayedMovie", displayedMovie.toString())
        }
    }
}
