package gal.uvigo.taskmanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import gal.uvigo.taskmanager.databinding.FragmentTaskFormBinding
import java.util.*

class TaskFormFragment : Fragment() {

    private var _binding: FragmentTaskFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskFormFragmentArgs by navArgs()
    private lateinit var task: Task

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task = if (args.taskId != "-1") {
            viewModel.getTaskByRemoteId(args.taskId.toString()) ?: Task()
        } else Task()

        binding.task = task

        // Inicializar campos
        binding.editTitle.setText(task.title)
        binding.editDescription.setText(task.description)
        binding.editDueDate.setText(task.dueDate)
        binding.autoCategory.setText(getCategoryString(task.category), false)
        binding.checkboxIsDone.isChecked = task.isDone

        setupCategory()
        setupDueDatePicker()

        // FAB como botón de guardar con texto y confirmación
        binding.fabSaveTask.setOnClickListener {
            if (binding.editTitle.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), getString(R.string.title_required), Toast.LENGTH_SHORT).show()
            } else {
                showSaveConfirmationDialog()
            }
        }
    }

    private fun saveTask() {
        task.title = binding.editTitle.text.toString()
        task.description = binding.editDescription.text.toString()
        task.dueDate = binding.editDueDate.text.toString()

        val categoryStr = binding.autoCategory.text.toString()
        Category.values().forEach { category ->
            if (categoryStr == getCategoryString(category)) {
                task.category = category
            }
        }

        task.isDone = binding.checkboxIsDone.isChecked

        if (args.taskId == "-1") viewModel.addTask(task) else viewModel.updateTask(task)
        findNavController().navigateUp()
    }

    private fun showSaveConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_save_title))
            .setMessage(getString(R.string.confirm_save_message))
            .setPositiveButton(getString(R.string.confirm_save_positive)) { dialog, _ ->
                saveTask()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.confirm_save_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupCategory() {
        val categoriesTranslated = Category.values().map { getCategoryString(it) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categoriesTranslated)
        binding.autoCategory.setAdapter(adapter)
    }

    private fun getCategoryString(category: Category): String {
        return when (category) {
            Category.WORK -> getString(R.string.category_work)
            Category.PERSONAL -> getString(R.string.category_personal)
            Category.URGENT -> getString(R.string.category_urgent)
            Category.FAMILY -> getString(R.string.category_family)
            Category.SHOPPING -> getString(R.string.category_shopping)
            Category.OTHER -> getString(R.string.category_other)
        }
    }

    private fun setupDueDatePicker() {
        binding.editDueDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                task.dueDate = "%04d-%02d-%02d".format(y, m + 1, d)
                binding.editDueDate.setText(task.dueDate)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
