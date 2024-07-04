// Attention: This app was tested with a Pixel 5 with API 30

package com.example.coursework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * The second movie search screen.
 * The user can enter any sub-string and the application will retrieve the first 10 movies
 * (This is a limitation of the API, not the application) from the OMDb API web service
 * that have a title containing the sub-string.
 * Also displays the number of displayed movies and the total number of matches.
 * @property movieSubstringSearchField The search field.
 * @property searchMoviesWithSubstringButton The button that searches the API.
 * @property movieSubstringResults The displayed details of the retrieved movies.
 * @author Dominik Deak - w1778659
 */
class MovieSearchWithSubstring : AppCompatActivity() {
    private lateinit var movieSubstringSearchField: EditText
    private lateinit var searchMoviesWithSubstringButton: Button
    private lateinit var movieSubstringResults: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_search_with_substring)

        movieSubstringSearchField = findViewById(R.id.movieSubstringSearchField)
        searchMoviesWithSubstringButton = findViewById(R.id.searchMoviesWithSubstringButton)
        movieSubstringResults = findViewById(R.id.movieSubstringResults)

        // Loads the displayed movies from the previous screen orientation
        if (savedInstanceState != null) {
            movieSubstringResults.text = savedInstanceState.getString("movieSubstringResults")
        }

        searchMoviesWithSubstringButton.setOnClickListener {
            searchMovies()
        }
    }

    /**
     * Uses the user input to search the API for movies with titles containing the input
     * and display the first 10 matches (This is a limitation of the API, not the application).
     * Also displays the number of displayed movies and the total number of matches.
     */
    private fun searchMovies() {
        val apiKey: String = resources.getString(R.string.omdbApiKey)
        var jsonResult = ""
        var stringResult = ""

        runBlocking {
            launch {
                withContext(Dispatchers.IO) {
                    val input = movieSubstringSearchField.text.toString().trim()
                    if (input.isEmpty()) {
                        stringResult = "You must enter at least one character to search!"
                    } else {
                        val url = URL("https://www.omdbapi.com/?s=*$input*&apikey=$apiKey")
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
                            jsonResult += line
                            line = bufferedReader.readLine()
                        }

                        val searchResult = JSONObject(jsonResult)
                        val response = searchResult.getString("Response")
                        if (response == "True") {
                            val totalResults = searchResult.getString("totalResults")
                            val searchResultArray = searchResult.getJSONArray("Search")
                            var resultsShown = 0
                            for (i in 0 until searchResultArray.length()) {
                                val jsonMovie = searchResultArray[i] as JSONObject
                                val movieTitle = jsonMovie["Title"] as String
                                stringResult += "$movieTitle\n\n"
                                resultsShown++
                            }
                            stringResult += "(Results shown: $resultsShown, Total results: $totalResults)\n"
                        } else {
                            stringResult = searchResult.getString("Error")
                        }
                    }
                }
            }
        }
        movieSubstringResults.text = stringResult
    }

    /**
     * Saves the currently displayed search results.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("movieSubstringResults", movieSubstringResults.text.toString())
    }
}
