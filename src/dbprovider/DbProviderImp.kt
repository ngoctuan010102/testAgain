package dbprovider

import `object`.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
object Db{
    fun sortAscending(list: List<Movies>): List<Movies> {
        val listCurrentMovies = list.toMutableList()
        for (i in listCurrentMovies.indices){
            val dateStart = LocalDate.parse(listCurrentMovies[i].startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            var j = i + 1
            for ( j in listCurrentMovies.indices){
                val dateStartJ = LocalDate.parse(listCurrentMovies[j].startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                if (dateStart.dayOfMonth < dateStartJ.dayOfMonth && dateStart.month <= dateStartJ.month &&
                    dateStart.year <= dateStartJ.year){
                    val list1 = listCurrentMovies[i]
                    listCurrentMovies[i] = listCurrentMovies[j]
                    listCurrentMovies[j] = list1
                }
            }
        }
        return listCurrentMovies
    }
}
class DbProviderImp : DbProvider {
    private var listMovies = mutableListOf<Movies>()
    private var listPrices = mutableListOf<Price>()
    private var listRooms = mutableListOf<Room>()
    private var listShowTime = mutableListOf<ShowTime>()
    private var listTheatre = mutableListOf<Theater>()
    override fun getAllMovies(): List<Movies> {
        return listMovies
    }

    override fun getNowMovies(): List<Movies> {
        val listCurrentMovies = mutableListOf<Movies>()
        //showing movie today
        val currentTime = LocalDateTime.now()
        for (i in listMovies.indices){
           val dateStart = LocalDate.parse(listMovies[i].startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val dateEnd = LocalDate.parse(listMovies[i].endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            if (currentTime.isBefore(dateEnd.atStartOfDay()) && currentTime.isAfter(dateStart.atStartOfDay())){
                listCurrentMovies.add(listMovies[i])
            }
        }
        return Db.sortAscending(listCurrentMovies)
    }

    override fun getComingSoonMovies(): List<Movies> {
        val listComingMovies = mutableListOf<Movies>()
        //showing movie today
        val currentTime = LocalDateTime.now()
        for (i in listMovies.indices){
            val dateStart = LocalDate.parse(listMovies[i].startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            if (currentTime.isBefore(dateStart.atStartOfDay())){
                listComingMovies.add(listMovies[i])
            }
        }
        return Db.sortAscending(listComingMovies)
    }

    override fun getAvailableMoviesByDate(): List<Movies> {
        val listAvailableMovies = mutableListOf<Movies>()
        //showing movie today
        val currentTime = LocalDateTime.now()
        for (i in listMovies.indices){
            val dateStart = LocalDate.parse(listMovies[i].startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val dateEnd = LocalDate.parse(listMovies[i].endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val listSchedule = listMovies[i].schedule.split(",").toList()
            if (currentTime.isBefore(dateEnd.atStartOfDay()) && currentTime.isAfter(dateStart.atStartOfDay())){
                for ( j in listSchedule.indices){
                    if (listSchedule[i].equals(currentTime.dayOfWeek.toString(), true)){
                        listAvailableMovies.add(listMovies[i])
                        break
                    }
                }
            }
        }
        return Db.sortAscending(listAvailableMovies)
    }

    override fun bookTicket() {
        TODO("Not yet implemented")
    }

    override fun setAllMovies(movies: List<Movies>) {
        listMovies = movies.toMutableList()
    }

    override fun setAllPrices(prices: List<Price>) {
        listPrices = prices.toMutableList()
    }

    override fun setAllRooms(rooms: List<Room>) {
        listRooms = rooms.toMutableList()
    }

    override fun setAllShowTimes(showTimes: List<ShowTime>) {
        listShowTime = showTimes.toMutableList()
    }

    override fun setTheater(theater: Theater) {
        listTheatre.add(theater)
    }
}