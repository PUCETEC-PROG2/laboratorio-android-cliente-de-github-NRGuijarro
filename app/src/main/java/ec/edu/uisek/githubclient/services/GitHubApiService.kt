package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GitHubApiService {

    @GET("/user/repos")
    fun getRepos(): Call<List<Repo>>

    @POST("user/repos")
    fun postFormRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>

}