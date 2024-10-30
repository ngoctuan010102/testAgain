package dbprovider

import `object`.*

interface DbProvider {
    fun getAllMovies(): List<Movies>
    fun getNowMovies(): List<Movies>
    fun getComingSoonMovies(): List<Movies>
    fun getAvailableMoviesByDate(): List<Movies>
    fun bookTicket()

    //set
    fun setAllMovies(movies: List<Movies>)
    fun setAllPrices(prices: List<Price>)
    fun setAllRooms(rooms: List<Room>)
    fun setAllShowTimes(showTimes: List<ShowTime>)
    fun setTheater(theater: Theater)
}