package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface GitHubApiService {

    @GET("/user/repos")
    fun getRepos(): Call<List<Repo>>

    @PATCH("/repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body repoData: Repo
    ): Call<Repo>

    @DELETE("/repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<Unit>
}