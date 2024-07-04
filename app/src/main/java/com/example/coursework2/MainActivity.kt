// Attention: This app was tested with a Pixel 5 with API 30

package com.example.coursework2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * The home screen of the application, contains 4 buttons.
 * @property addToDbButton The button that saves the details of 5 movies to a database.
 * @property searchMoviesButton The button that takes the user to an activity where they can search for a movie
 * using the OMDb API web service.
 * @property searchActorsButton The button that takes the user to an activity where they can search the
 * local database for movies using an actor name.
 * @property searchMoviesWithSubstringButton The button that takes the user to an activity where they can search for movies
 * using the OMDb API web service. The activity will search the web service for all movies that contain
 * the entered sub-string as part of their title.
 * @property database The SQLite database local to the device.
 * @property movieDao The Dao used for the database.
 * @author Dominik Deak - w1778659
 */
class MainActivity : AppCompatActivity() {
    private lateinit var addToDbButton: Button
    private lateinit var searchMoviesButton: Button
    private lateinit var searchActorsButton: Button
    private lateinit var searchMoviesWithSubstringButton: Button
    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addToDbButton = findViewById(R.id.addToDbButton)
        searchMoviesButton = findViewById(R.id.searchMoviesButton)
        searchActorsButton = findViewById(R.id.searchActorsButton)
        searchMoviesWithSubstringButton = findViewById(R.id.searchMoviesWithSubstringButton)

        // Builds the database and creates the Dao object
        database = Room.databaseBuilder(this, AppDatabase::class.java, "movieDatabase").build()
        movieDao = database.movieDao()

        // Adds the click listeners to the three buttons
        addToDbButton.setOnClickListener {
            saveMoviesToDB()
        }
        searchMoviesButton.setOnClickListener {
            val movieSearchIntent = Intent(this, MovieSearch::class.java)
            startActivity(movieSearchIntent)
        }
        searchActorsButton.setOnClickListener {
            val actorSearchIntent = Intent(this, ActorSearch::class.java)
            startActivity(actorSearchIntent)
        }
        searchMoviesWithSubstringButton.setOnClickListener {
            val movieSubstringSearch = Intent(this, MovieSearchWithSubstring::class.java)
            startActivity(movieSubstringSearch)
        }
    }

    /**
     * Saves the details of 5 movies in an SQLite database locally.
     * Uses the Room library.
     * Creates a popup message, informing user that the movies were added to the database.
     */
    private fun saveMoviesToDB() {
        runBlocking {
            launch {
                val movie0 = Movie(
                    "The Shawshank Redemption", "1994", "R", "14 Oct 1994", "142 min",
                    "Drama", "Frank Darabont", "Stephen King, Frank Darabont",
                    "Tim Robbins, Morgan Freeman, Bob Gunton",
                    "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
                )
                val movie1 = Movie(
                    "Batman: The Dark Knight Returns, Part 1", "2012", "PG-13", "25 Sep 2012",
                    "76 min", "Animation, Action, Crime, Drama, Thriller", "Jay Oliva",
                    "Bob Kane (character created by: Batman), Frank Miller (comic book), Klaus Janson (comic book), Bob Goodman",
                    "Peter Weller, Ariel Winter, David Selby, Wade Williams",
                    "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl. But, does he still have what it takes to fight crime in a new era?"
                )
                val movie2 = Movie(
                    "The Lord of the Rings: The Return of the King", "2003", "PG-13", "17 Dec 2003",
                    "201 min", "Action, Adventure, Drama", "Peter Jackson", "J.R.R. Tolkien, Fran Walsh, Philippa Boyens",
                    "Elijah Wood, Viggo Mortensen, Ian McKellen",
                    "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring."
                )
                val movie3 = Movie(
                    "Inception", "2010", "PG-13", "16 Jul 2010", "148 min",
                    "Action, Adventure, Sci-Fi", "Christopher Nolan", "Christopher Nolan",
                    "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
                    "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster."
                )
                val movie4 = Movie(
                    "The Matrix", "1999", "R", "31 Mar 1999", "136 min", "Action, Sci-Fi",
                    "Lana Wachowski, Lilly Wachowski", "Lilly Wachowski, Lana Wachowski",
                    "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                    "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."
                )

                movieDao.insertMovies(movie0, movie1, movie2, movie3, movie4)
            }
        }
        Toast.makeText(this, "Successfully added movies to database", Toast.LENGTH_SHORT).show()
    }
}
