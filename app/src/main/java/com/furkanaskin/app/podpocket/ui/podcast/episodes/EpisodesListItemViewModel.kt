package com.furkanaskin.app.podpocket.ui.podcast.episodes

import androidx.databinding.ObservableField
import com.furkanaskin.app.podpocket.core.BaseViewModel
import com.furkanaskin.app.podpocket.db.entities.EpisodeEntity

/**
 * Created by Furkan on 29.04.2019
 */

class EpisodesListItemViewModel : BaseViewModel() {
    var item = ObservableField<EpisodeEntity>()
    var position = -1

    fun setModel(item: EpisodeEntity, position: Int) {
        this.item.set(item)
        this.position = position
    }
}