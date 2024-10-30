package `object`

data class Theater(
    val theaterName : String,
    val address: String,
    val contact: String,
    val movies: List<Movies>,
    val price: List<Price>,
    val room: List<Room>
)
