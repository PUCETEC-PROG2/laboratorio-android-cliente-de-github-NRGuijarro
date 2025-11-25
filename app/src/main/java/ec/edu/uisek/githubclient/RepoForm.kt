package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var repoFormBinding: ActivityRepoFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)
        repoFormBinding.buttonCancelRepo.setOnClickListener { finish() }
        repoFormBinding.buttonSaveRepo.setOnClickListener { createRepo() }
    }

    private fun validateFormRepo(): Boolean{
        val repoName = repoFormBinding.nameRepoInputForm.text.toString()

        if(repoName.isBlank()){
            repoFormBinding.nameRepoInputForm.error = "El nombre del repositorio es obligatorio"
            return false
        }

        if(repoName.contains(" ")){
            repoFormBinding.nameRepoInputForm.error = "El nombre del repositorio no puede tener espacios"
            return false
        }

        return true
    }

    private fun createRepo(){
        if(!validateFormRepo()){
            return
        }

        val repoName = repoFormBinding.nameRepoInputForm.text.toString()
        val repoDescription = repoFormBinding.descriptionRepoInputForm.text.toString()

        val repoRequest: RepoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        //vamos a crar el cliente de GITHUB usando API service
        var apiService = RetrofitClient.gitHubApiService
        val call = apiService.postFormRepo(repoRequest)

        call.enqueue(object : Callback<Repo>{
            override fun onResponse(call: Call<Repo?>, response: Response<Repo?>) {
                if(response.isSuccessful){
                    Log.d("RepoForm", "El repositorio ${repoName} fue creado exitosamente")
                    showMessage("El repositorio ${repoName} fue creado exitosamente")
                    finish()
                }else {
                    //no hay respuesta
                    val errorMessage = when(response.code()){
                        401 -> "Error de autenticacion"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconociido ${response.code()}: ${response.message()}"
                    }
                    //voy a lanzar un error
                    Log.e("RepoForm", errorMessage)
                    //muestro el mensaje
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: retrofit2.Call<Repo?>, t: Throwable) {
                Log.e("RepoForm", "Error de red: ${t.message}")
                showMessage("Error de red: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG)
    }
}