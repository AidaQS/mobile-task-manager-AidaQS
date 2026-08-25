package gal.uvigo.taskmanager

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gal.uvigo.taskmanager.databinding.FragmentTaskDetailBinding

@Suppress("DEPRECATION")
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // habilita solo el botón de editar
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskDetailBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val task = viewModel.getTaskByRemoteId(args.taskId)
        binding.task = task   // databinding cargando datos en la vista

        // ⬇️ Botón Delete  con confirmación
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_task_detail, menu) // SOLO Edit
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//
//            R.id.action_edit -> {
//                findNavController().navigate(
//                    TaskDetailFragmentDirections.actionTaskDetailToTaskForm(args.taskId)
//                )
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun showDeleteConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_delete_title))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.confirm_delete_positive)) { _, _ ->
                val task = viewModel.getTaskByRemoteId(args.taskId)
                if (task != null) {
                    viewModel.deleteTask(task)
                }
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.confirm_delete_negative), null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

