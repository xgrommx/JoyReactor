package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.model.Tag
import y2k.joyreactor.presenters.TagListPresenter

/**
 * Created by y2k on 11/12/15.
 */
class MenuFragment : BaseFragment() {

    lateinit var presenter: TagListPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val list = view.findViewById(R.id.list) as RecyclerView

        val adapter = TagsAdapter()
        list.adapter = adapter

        presenter = ServiceLocator.resolve(lifeCycleService,
            object : TagListPresenter.View {

                override fun reloadData(tags: List<Tag>) {
                    adapter.updateData(tags)
                }
            })

        return view
    }

    inner class TagsAdapter : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

        private var tags = emptyList<Tag>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(vh: ViewHolder, position: Int) {
            if (vh.title != null && vh.icon != null) {
                val item = tags[position - 1]
                vh.title.text = item.title
                vh.icon.setImage(item.image)
            }
        }

        override fun getItemCount(): Int {
            return tags.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) R.layout.layout_subscriptions_header else R.layout.item_subscription
        }

        fun updateData(tags: List<Tag>) {
            this.tags = tags
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val title = view.findViewById(R.id.title) as TextView?
            val icon = view.findViewById(R.id.icon) as WebImageView?

            init {
                val action = view.findViewById(R.id.action)
                if (action == null) {
                    view.findViewById(R.id.selectFeatured).setOnClickListener { presenter.selectedFeatured() }
                    view.findViewById(R.id.selectFavorite).setOnClickListener { presenter.selectedFavorite() }
                } else {
                    action.setOnClickListener { presenter.selectTag(tags[adapterPosition - 1]) }
                }
            }
        }
    }
}