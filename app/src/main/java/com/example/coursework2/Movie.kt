package com.example.coursework2

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The data class representing the Movie entities in the database.
 */
@Entity
data class Movie(
    @PrimaryKey val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String
)
