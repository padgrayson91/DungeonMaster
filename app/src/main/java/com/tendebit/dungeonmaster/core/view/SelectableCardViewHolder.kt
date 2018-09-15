package com.tendebit.dungeonmaster.core.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectableElement
import io.reactivex.subjects.PublishSubject

class SelectableCardViewHolder<T : SelectableElement>(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_generic_selection, parent, false)) {
    private val classNameText = itemView.findViewById<TextView>(R.id.primary_item_text)
    private val secondaryActionButton = itemView.findViewById<ImageView>(R.id.action_button)
    val itemActions = PublishSubject.create<Pair<T, List<ItemAction>>>()

    fun populate(element: T, currentlySelected: SelectableElement?) {
        classNameText.text = element.primaryText()
        configurePrimaryActions(element, currentlySelected)
        configureSecondaryActions(element)
    }

    private fun configurePrimaryActions(element: T, currentlySelected: SelectableElement?) {
        val actions = element.primaryItemActions()
        itemView.setOnClickListener {
            itemActions.onNext(Pair(element, actions))
        }

        if (actions.contains(ItemAction.HIGHLIGHT) && element.primaryId() == currentlySelected?.primaryId()) {
            classNameText.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
        } else {
            classNameText.setTextColor(Color.LTGRAY)
        }
    }

    private fun configureSecondaryActions(element: T) {
        val actions = element.secondaryItemActions()
        when(actions.size) {
            0 -> configureNoActions()
            1 -> configureSingleAction(element, actions[0])
            else -> configureMultipleActions()
        }
    }

    private fun configureNoActions() {
        secondaryActionButton.visibility = View.INVISIBLE
        secondaryActionButton.setOnClickListener(null)
    }

    private fun configureSingleAction(element: T, itemAction: ItemAction) {
        secondaryActionButton.visibility = View.VISIBLE
        val context = secondaryActionButton.context.applicationContext
        when (itemAction) {
            // TODO: should have a utility class to convert from actions to UI text, drawables, etc
            ItemAction.DELETE -> secondaryActionButton.setImageDrawable(context.getDrawable(R.drawable.ic_material_clear_button))
            else -> throw RuntimeException("Action ${itemAction.name} cannot be rendered as an action button")
        }

        secondaryActionButton.setOnClickListener { itemActions.onNext(Pair(element, arrayListOf(itemAction))) }
    }

    private fun configureMultipleActions() {
        secondaryActionButton.visibility = View.VISIBLE
        // TODO: setup popup menu for view
    }

}