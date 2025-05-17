import com.example.wallipop.ListOfPhotosItem
import com.example.wallipop.SearchResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ImageService {
    @GET("photos")
    fun getPhotos(
        @Query("page") page: Int,
        @Query("client_id") clientId: String = API_KEY
    ): Call<List<ListOfPhotosItem>>

    @GET("search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("client_id") clientId: String = API_KEY
    ): Call<SearchResponse>

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"
        private const val API_KEY = "8sYNuHtHI6Cqb9cUmRdhGaCnc6JLfy8gC9o5gROY-P0"

        val imageInstance: ImageService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService::class.java)
        }
    }
}


