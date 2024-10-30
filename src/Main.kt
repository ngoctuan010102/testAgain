import dbprovider.DbInstance
import `object`.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object bookTicket {
    val userInforation = mutableListOf<String>()
    var slot: String? = null
    var showTime: String? = null
    var type: String? = null
    val movieInformation = mutableListOf<Movies>()
    var affectedDate: String? = null
    private var price: Double? = null
    var isVip: Boolean = false
    fun printTicket(){
        var slotVip: String? = null
        if (isVip) {
            slotVip = "$slot VIP"
        }
        else {
            slotVip = "$slot"
        }
        if (type == "2D")
            price = 45000.0
        else
            price = 65000.0
        welcome()
        println("Customer: ${userInforation[0]}")
        println("Phone Number: ${userInforation[1]}")
        println("===================================================")
        println("Movie name: ${movieInformation[0].moviesName}")
        println("Movie type: $type")
        println("Affected Date: $affectedDate")
        println("Show time: $showTime")
        println("Slot : $slotVip")
        println("Show time: ${movieInformation[0]}")
        println("Price : $price VND")
        println("===================================================")
        println("Hope you enjoy the movie")
        println("\nPress Enter to continue")
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    welcome()
    fakeData()
    showContent()
}

val instance = DbInstance().getInstance()
fun printOutMovies(list: List<Movies>) {
    for (i in list.indices) {
        println("========Movie ${i + 1}========")
        println("\tMovie's Name: ${list[i].moviesName}")
        println("\tMovie's code: ${list[i].code}")
        println("\tStart date: ${list[i].startDate}")
        println("\tDuration: ${list[i].duration}")
    }
}

fun showController(option: Int) {
    when (option) {
        1 -> {
            val list = instance.getNowMovies()
            printOutMovies(list)
        }

        2 -> {
            val list = instance.getComingSoonMovies()
            printOutMovies(list)
        }

        3 -> {
            val list = instance.getAvailableMoviesByDate()
            printOutMovies(list)
        }

        4 -> {
            val list = instance.getAllMovies()
            print("Type the Movie's code: ")
            val input = readln().toIntOrNull()
            input?.let {
                for (i in list.indices) {
                    // exit code
                    if (list[i].code.toInt() == input) {
                        if (startYet(list[i])) {
                            println("Movie hasn't started yet")
                            break
                        } else if (expireYet(list[i])) {
                            println("Movie has been expired")
                            break
                        } else {
                            showSubMenu(list[i])
                        }

                    }
                }
            }
        }
    }
}


fun showSubMenu(movie: Movies) {
    var choice = 8
    do {
        choice = printOutMoviesCode(getAllMoviesDate(movie))
        if (choice == 0)
            break
    } while (choice !in 1..7)
    when (choice) {
        in 1..9 -> {
            bookTicket.movieInformation.add(movie)
            val listShowTime = movie.showtime.toMutableList()
            showTime(listShowTime, getAllMoviesDate(movie), choice)
        }
    }
}

fun printOutListShowTime(list: List<ShowTime>): Int {
    println("=== Please select the show time that you want to book ticket ===")
    for (i in list.indices) {
        println("${i + 1}: Time: ${list[i].time} - Type: ${list[i].type}")
    }
    println("0. Exit")
    println("================================================================")
    val choice = readln().toIntOrNull()
    return choice?.let {
        choice
    } ?: -1
}

// get the showtime appears if today
fun getRealShowTime(list: List<ShowTime>): List<ShowTime> {
    val currentDate = LocalDateTime.now()
    val hour = currentDate.hour
    val minute = currentDate.minute
    return list.let {
        val listReturn = mutableListOf<ShowTime>()
        for (i in list.indices) {
            val timeList = list[i].time.split(":")
            if (timeList[0].toInt() > hour) {
                listReturn.add(list[i])
            } else if (timeList[0].toInt() == hour) {
                if (timeList[1].toInt() > minute)
                    listReturn.add(list[i])
            }
        }
        sortRealShowTimeList(listReturn)
    }
}

fun sortRealShowTimeList(list: List<ShowTime>): List<ShowTime> {
    val listSorted = list.toMutableList()
    for (i in listSorted.indices) {
        for (j in i + 1..<listSorted.size) {
            val listPickDateFirst = listSorted[i].time.split(":")
            val listPickDateSecond = listSorted[j].time.split(":")
            if (listPickDateFirst[0].toInt() > listPickDateSecond[0].toInt()) {
                val pos = listSorted[j]
                listSorted[j] = listSorted[i]
                listSorted[i] = pos
            }
        }
    }
    return listSorted
}

fun showTime(list: List<ShowTime>, listMovieDate: List<String>, pickNumber: Int) {
    var choice = 0
    val listRealShowTime = getRealShowTime(list)
    val currentDate = LocalDate.now()
    val getPickedMovie = listMovieDate[pickNumber - 1]
    val pickedDate = getPickedMovie[0].toString() + getPickedMovie[1].toString()
    var isRealShowTime = false
    do {
        //get the list if it is today
        choice = if (currentDate.dayOfMonth == pickedDate.toInt()) {
            isRealShowTime = true
            printOutListShowTime(listRealShowTime)
        } else {
            isRealShowTime = false
            printOutListShowTime(list)
        }
        if (choice == 0)
            break
    } while (choice > list.size - 1 || choice < 0)
    if (isRealShowTime) {
        print(listRealShowTime[choice - 1].time)
        print(listRealShowTime[choice - 1].type)
        bookTicket.showTime = listRealShowTime[choice - 1].time
        bookTicket.type = listRealShowTime[choice - 1].type
    } else {
        bookTicket.showTime = list[choice - 1].time
        bookTicket.type = list[choice - 1].type
    }
    showSlot()
}

fun createSeats(): MutableList<List<String>> {
    val listChar = listOf("A", "B", "C", "D")
    val listNumber = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val listSeats = mutableListOf<List<String>>()
    for (i in listChar.indices) {
        val listTemSeats = mutableListOf<String>()
        for (j in listNumber.indices) {
            listTemSeats.add("${listChar[i]}${listNumber[j]}")
        }
        listSeats.add(listTemSeats)
    }
    return listSeats
}

fun showSlot() {
    val listSlot = createSeats()
    val vipSlot = mutableListOf<String>()
    println("\t\t\t=================<<<<SCREEN>>>>===================")
    for (i in listSlot.indices) {
        println("\t\t\t\t${listSlot[i]}")
        if (i == 1 || i == 2) {
            val listVipSlot = listSlot[i]
            for (j in 2..6)
                vipSlot.add(listVipSlot[j])
        }
    }
    println("\n\n\t\t\tVip Slot: $vipSlot")
    println("===========================================================")
    selectSlot(listSlot)
    bookTicket.isVip = isVipSlot(vipSlot)
    getUserInformation()
    bookTicket.printTicket()
}

fun isVipSlot(vipSlot: List<String>): Boolean {
    for (i in vipSlot.indices) {
        if (vipSlot[i].equals(bookTicket.slot, true)) {
            return true
        }
    }
    return false
}

fun getUserInformation() {
    print("Your name: ")
    val name = readlnOrNull()
    name?.let {
        bookTicket.userInforation.add(it)
    }
    print("Your phoneNumber: ")
    val phone = readlnOrNull()
    phone?.let {
        bookTicket.userInforation.add(it)
    }
    val currentDate = LocalDate.now()
    bookTicket.affectedDate = "${currentDate.dayOfMonth}/${currentDate.monthValue}/${currentDate.year}"
    showTicketInformation()
}

fun showTicketInformation() {
    print("Your name: ")
}

fun selectSlot(listSlot: MutableList<List<String>>) {
    var choice = -1
    do {
        print("\n\nPlease select a slot \nYour option (0 to exit): ")
        // input
        val input = readlnOrNull()
        choice = input?.let { it ->
            if (it.toIntOrNull() != null) {
                if(it.toInt() == 0)
                    showContent()
            } else {
                for (i in listSlot.indices) {
                    var check = false
                    val list = listSlot[i]
                    for (j in 0..8) {
                        if (list[j].equals(it, true)) {
                            bookTicket.slot = list[j]
                            choice = 0
                            check = true
                            break
                        }
                    }
                    if(check)
                        break
                }
            }
            choice
        } ?: -1
    } while (choice != 0)
}

fun printOutMoviesCode(list: List<String>): Int {
    println("=== Please select the date that you want to book ticket ===")
    for (i in list.indices) {
        println("${i + 1}. ${list[i]}")
    }
    println("0. Exit")
    println("==========================================================\n")
    print("\tYour option: ")
    val choice = readln().toIntOrNull()
    return choice?.let {
        choice
    } ?: 8
}

fun getAllMoviesDate(movie: Movies): List<String> {
    return movie.let {
        val list = movie.schedule.split(",")
        val list2 = mutableListOf<String>()
        val currentD = LocalDate.now()
        for (i in list.indices) {
            val getFullDate = currentD.parseAValue(list[i])
            val stringDate =
                "${formatDate(getFullDate.dayOfMonth)}/${formatDate(getFullDate.monthValue)}/${getFullDate.year}"
            list2.add(stringDate)
        }
        list2
    }
}

fun formatDate(intDate: Int): String {
    return when (intDate) {
        in 1..9 -> "0$intDate"
        else -> "$intDate"
    }

}

fun LocalDate.parseAValue(day: String): LocalDate {

    return when (day.lowercase()) {
        "monday" -> this.with(DayOfWeek.MONDAY)
        "tuesday" -> this.with(DayOfWeek.TUESDAY)
        "wednesday" -> this.with(DayOfWeek.WEDNESDAY)
        "thursday" -> this.with(DayOfWeek.THURSDAY)
        "friday" -> this.with(DayOfWeek.FRIDAY)
        "saturday" -> this.with(DayOfWeek.SATURDAY)
        else -> {
            this.with(DayOfWeek.SUNDAY)
        }
    }
}

fun expireYet(movie: Movies): Boolean {
    return movie.let {
        val currentDate = LocalDate.now()
        val endDate = LocalDate.parse(movie.endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        currentDate.dayOfMonth > endDate.dayOfMonth
    }
}

fun startYet(movie: Movies): Boolean {
    val currentDate = LocalDate.now()
    val startDate = LocalDate.parse(movie.startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    return currentDate.dayOfMonth < startDate.dayOfMonth
}

fun welcome() {
    println("=======================================================================")
    println("Welcome to -- BHD Movies Theater --")
    println("Address: 123 A Street")
    println("Contact: 0237327362")
    println("=======================================================================")
}

fun showContent() {
    var option = 0
    do {
        getContent()
        option = readln().toIntOrNull() ?: 0
        if (option != 0) {
            showController(option)
        }
    } while (option != 5)
}

fun getContent() {
    println("\n")
    println("1. View now showing movies")
    println("2. View coming soon movies")
    println("3. View available movies by date")
    println("4. Book Ticket")
    println("5. Exit")
    println("=======================================================================")
    println("\n")
    print("   Option: ")
}

fun fakeData() {

    //price
    val price = Price("2D", "40000")
    val price1 = Price("3D", "65000")
    val listPrice = listOf(price, price1)
    instance.setAllPrices(listPrice)

    // room
    val room = Room("Luxury", "1")
    val room1 = Room("Common", "0")
    val listRoom = listOf(room, room1)
    instance.setAllRooms(listRoom)

    // show time
    val showTime = ShowTime("10:40", "2D", "Luxury")
    val showTime1 = ShowTime("22:40", "3D", "Common")
    val showTime2 = ShowTime("20:30", "2D", "Common")
    val listShowTime = listOf(showTime, showTime1, showTime2)
    instance.setAllShowTimes(listShowTime)

    //movie
    val movie0 = Movies(
        "Back To School",
        "1024",
        "02/06/2024",
        "04/06/2024",
        "Monday,Tuesday,Wednesday,Thursday,Friday,Sunday,Saturday",
        "1:50",
        listShowTime
    )
    val movie1 = Movies(
        "Mai",
        "1023",
        "06/06/2024",
        "09/06/2024",
        "Monday,Tuesday,Wednesday,Thursday,Friday,Sunday",
        "1:50",
        listShowTime
    )
    val movie2 = Movies(
        "Godzilla and Kong",
        "1111",
        "04/06/2024",
        "10/06/2024",
        "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday",
        "2:00",
        listShowTime
    )
    val listMovies = listOf(movie0, movie1, movie2)
    instance.setAllMovies(listMovies)

    //theater
    val theater = Theater("BHD Movies Theater", "123 A Street", "023928323", listMovies, listPrice, listRoom)
    instance.setTheater(theater)
}