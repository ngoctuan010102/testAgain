
import `object`.ShowTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun main(){
        val currentDate = LocalDateTime.now()
        val hour = currentDate.hour
        val minute = currentDate.minute
    println(hour)
    println(minute)
}