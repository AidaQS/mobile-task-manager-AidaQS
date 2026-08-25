package gal.uvigo.taskmanager

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gal.uvigo.taskmanager.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = TaskAdapter { task ->
            val id = task._id ?: return@TaskAdapter
            val action = TaskListFragmentDirections.actionTaskListToTaskDetail(id) //cambio
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.taskListItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        setupSwipeAndDrag()
    }

    private fun setupSwipeAndDrag() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder) =
                if (adapter.currentList[vh.bindingAdapterPosition] is TaskListItem.Header) 0
                else makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(rv: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPos = source.bindingAdapterPosition
                val toPos = target.bindingAdapterPosition

                val fromItem = adapter.currentList[fromPos]
                val toItem = adapter.currentList[toPos]

                if (fromItem is TaskListItem.TaskEntry && toItem is TaskListItem.TaskEntry &&
                    fromItem.task.category == toItem.task.category
                ) {
                    // Solo mover si son de la misma categoría
                    val currentList = adapter.currentList.toMutableList()
                    val temp = currentList[fromPos]
                    currentList[fromPos] = currentList[toPos]
                    currentList[toPos] = temp

                    // Actualizar visualmente sin afectar los datos reales
                    adapter.submitList(currentList)
                    return true
                }
                return false
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[vh.bindingAdapterPosition]
                if (item is TaskListItem.TaskEntry) {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            // Swipe izquierdo: eliminar
                            viewModel.deleteTask(item.task)
                        }
                        ItemTouchHelper.RIGHT -> {
                            // Swipe derecho: marcar como COMPLETADA (si no lo está ya)
                            if (!item.task.isDone) {
                                viewModel.updateTask(item.task.copy(isDone = true))
                            } else {
                                // Si ya está completada, no hacer nada y restaurar la vista
                                adapter.notifyItemChanged(vh.bindingAdapterPosition)
                            }
                        }
                    }
                } else {
                    // Si es un header, restaurar la vista
                    adapter.notifyItemChanged(vh.bindingAdapterPosition)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    private fun getIndexInCategory(absolutePos: Int, category: Category): Int {
        val list = adapter.currentList
        var idx = -1
        var count = 0
        for (i in 0..absolutePos) {
            val it = list[i]
            if (it is TaskListItem.TaskEntry && it.task.category == category) {
                idx = count
                count++
            }
        }
        return if (idx == -1) 0 else idx
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_task_list, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_add -> {
//                findNavController().navigate(
//                    TaskListFragmentDirections.actionTaskListToTaskForm("-1") //cambio
//                )
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}