package xyz.stereobot.kt.lavaplayer

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class FilterManager(val player: AudioPlayer) {
  private var filters: List<AudioFilter> = listOf()
  
  fun add(filter: AudioFilter) {
    filters = filters.plus(filter)
    apply()
  }
  
  fun remove(filter: AudioFilter) {
    filters = filters.minus(filter)
    apply()
  }
  
  fun clear() {
    filters = listOf()
    player.setFilterFactory(null)
  }
  
  private fun apply() {
    player.setFilterFactory { _, _, _ -> filters }
  }
}