package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), ReposAdapter.OnRepoActionClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(this)
        binding.repoRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("Usted no tiene repositorios")
                    }
                } else {
                    val errorMsg = when(response.code()) {
                        401 -> "Error de autenticación"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconocido ${response.code()}"
                    }
                    Log.e("MainActivity", errorMsg)
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("Error de conexión")
                Log.e("MainActivity", "Error de conexión ${t.message}")
            }
        })
    }

    private fun showMessage (msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(repo: Repo) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val nameInput = EditText(this).apply {
            setText(repo.name)
            hint = "Nombre del repositorio"
        }
        val descriptionInput = EditText(this).apply {
            setText(repo.description)
            hint = "Descripción"
        }

        layout.addView(nameInput)
        layout.addView(descriptionInput)

        AlertDialog.Builder(this)
            .setTitle("Editar Repositorio")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = nameInput.text.toString()
                val newDescription = descriptionInput.text.toString()
                val updatedRepo = repo.copy(name = newName, description = newDescription)

                updateRepo(repo.owner.login, repo.name, updatedRepo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateRepo(owner: String, oldName: String, repo: Repo) {
        val apiService = RetrofitClient.gitHubApiService
        apiService.updateRepo(owner, oldName, repo).enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado con éxito")
                    fetchRepositories()
                } else {
                    showMessage("Error al actualizar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo en la conexión al actualizar")
            }
        })
    }

    override fun onDeleteClick(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Repositorio")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepo(repo.owner.login, repo.name)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteRepo(owner: String, repoName: String) {
        val apiService = RetrofitClient.gitHubApiService
        apiService.deleteRepo(owner, repoName).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado con éxito")
                    fetchRepositories()
                } else {
                    showMessage("Error al eliminar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                showMessage("Fallo en la conexión al eliminar")
            }
        })
    }
}
