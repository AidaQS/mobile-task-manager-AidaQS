package gal.uvigo.taskmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gal.uvigo.taskmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "📝 Task Manager"
        binding.toolbar.setTitleTextColor(getColor(R.color.md_on_primary))

        // Obtener NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configurar AppBar
        appBarConfiguration = AppBarConfiguration(setOf(R.id.taskListFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Mantener el título aunque se cambie de fragment
        navController.addOnDestinationChangedListener { _, destination, arguments ->

            // Siempre mantener el título
            supportActionBar?.title = "Task Manager"

            // Configurar FAB según el fragmento
            when (destination.id) {
                R.id.taskListFragment -> {
                    // FAB visible y sirve para añadir tarea
                    binding.fabAddTask.show()
                    binding.fabAddTask.setImageResource(R.drawable.ic_add)
                    binding.fabAddTask.setOnClickListener {
                        navController.navigate(R.id.action_taskList_to_taskForm)
                    }
                }
                R.id.taskDetailFragment -> {
                    // FAB visible y sirve para editar tarea
                    binding.fabAddTask.show()
                    binding.fabAddTask.setImageResource(R.drawable.ic_edit)
                    val taskId = arguments?.getInt("taskId") ?: -1
                    binding.fabAddTask.setOnClickListener {
                        val taskId = arguments?.getString("taskId") ?: "-1"
                        val action = TaskDetailFragmentDirections.actionTaskDetailToTaskForm(taskId)
                        navController.navigate(action)
                    }
                }

                else -> {
                    // Otros fragmentos: ocultar FAB
                    binding.fabAddTask.hide()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
