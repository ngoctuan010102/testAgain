package `object`

data class Movies(
    val moviesName: String,
    val code: String,
    val startDate: String,
    val endDate: String,
    val schedule: String,
    val duration: String,
    val showtime: List<ShowTime>
)
