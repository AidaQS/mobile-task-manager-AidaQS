package gal.uvigo.taskmanager

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import gal.uvigo.taskmanager.TaskApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    private const val BASE_URL =
        "https://crudcrud.com/api/01b66b86b6424498b73d9a7103486e94/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }
}
