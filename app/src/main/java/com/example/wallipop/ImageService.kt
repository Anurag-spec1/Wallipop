import com.example.wallipop.ListOfPhotosItem
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



interface ImageService {

    @GET("photos")
    fun getPhotos(
        @Query("page") page: Int, // Page number for pagination
        @Query("client_id") clientId: String = API_KEY // API key as a default parameter
    ): Call<List<ListOfPhotosItem>>

    companion object {
        // Base URL for the Unsplash API
        private const val BASE_URL = "https://api.unsplash.com/"

        // API key (store it securely, e.g., in local.properties or use BuildConfig)
        private const val API_KEY = "8sYNuHtHI6Cqb9cUmRdhGaCnc6JLfy8gC9o5gROY-P0"

        // Lazy initialization of the Retrofit instance
        val imageInstance: ImageService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService::class.java)
        }
    }
}

