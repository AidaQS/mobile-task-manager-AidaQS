package gal.uvigo.taskmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gal.uvigo.taskmanager.databinding.ItemTaskBinding
import gal.uvigo.taskmanager.databinding.ItemHeaderBinding

class TaskAdapter(private val onClick: ((Task) -> Unit)? = null) :
    ListAdapter<TaskListItem, RecyclerView.ViewHolder>(TaskDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK = 1
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is TaskListItem.Header -> TYPE_HEADER
        is TaskListItem.TaskEntry -> TYPE_TASK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_TASK -> TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as TaskListItem.Header)
            is TaskViewHolder -> holder.bind(item as TaskListItem.TaskEntry)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: TaskListItem.Header) {
            binding.headerTitle.text = header.category.name
        }
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: TaskListItem.TaskEntry) {
            binding.titleText.text = entry.task.title
            binding.descText.text = entry.task.description
            binding.checkDone.isChecked = entry.task.isDone
            val iconRes = when(entry.task.syncState) {
                SyncState.SYNCED -> R.drawable.ic_synced
                SyncState.PENDING_CREATE -> R.drawable.ic_pending
                SyncState.PENDING_UPDATE -> R.drawable.ic_pending
                SyncState.PENDING_DELETE -> R.drawable.ic_deleted
                SyncState.FAILED -> R.drawable.ic_failed
            }
            binding.syncIcon.setImageResource(iconRes)
            binding.root.setOnClickListener { onClick?.invoke(entry.task) }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<TaskListItem>() {
    override fun areItemsTheSame(oldItem: TaskListItem, newItem: TaskListItem) =
        if (oldItem is TaskListItem.Header && newItem is TaskListItem.Header)
            oldItem.category == newItem.category
        else if (oldItem is TaskListItem.TaskEntry && newItem is TaskListItem.TaskEntry)
            oldItem.task._id == newItem.task._id
        else false

    override fun areContentsTheSame(oldItem: TaskListItem, newItem: TaskListItem) = oldItem == newItem
}