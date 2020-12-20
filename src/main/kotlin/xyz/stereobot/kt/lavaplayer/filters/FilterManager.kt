package xyz.stereobot.kt.lavaplayer.filters

import com.github.natanbc.lavadsp.natives.TimescaleNativeLibLoader
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import org.slf4j.LoggerFactory
import xyz.stereobot.kt.lavaplayer.filters.configs.BaseConfig

class FilterManager(val player: AudioPlayer) {
  private var filters: List<BaseConfig> = listOf()
  private val logger = LoggerFactory.getLogger(FilterManager::class.java)
  
  var filter = "nothing"
  
  fun add(filter: BaseConfig) {
    filters = filters.plus(filter)
  }
  
  fun addMany(filters: List<BaseConfig>) {
    for (filter in filters) {
      this.add(filter)
    }
  }
  
  fun remove(filter: BaseConfig) {
    filters = filters.minus(filter)
  }
  
  fun clear() {
    filters = listOf()
    player.setFilterFactory(null)
  }
  
  fun apply() {
    player.setFilterFactory { _, format, output ->
      val appliedFilters = mutableListOf<FloatPcmAudioFilter>()
      
      for (filter in filters) {
        appliedFilters.add(
          filter.build(
            format,
            appliedFilters.lastOrNull() ?: output
          )
        )
      }
  
      // reverse, because the lavadsp example shows that so yeah
      appliedFilters.reversed() as List<AudioFilter>?
    }
  }
  
  init {
    if (!TimescaleNativeLibLoader.isLoaded()) {
      logger.info("Loading timescale library")
      TimescaleNativeLibLoader.loadTimescaleLibrary()
    }
  }
}