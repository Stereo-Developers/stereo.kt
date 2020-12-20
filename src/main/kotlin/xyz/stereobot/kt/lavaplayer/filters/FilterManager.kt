/**
 * Taken from https://github.com/Frederikam/Lavalink/blob/dev/LavalinkServer/src/main/java/lavalink/server/player/filters/FilterChain.kt
 * Commit 386c2f3bee435821041c8f81a22bfd511807ae13
 * Thanks Lavaplayer for having no documentation (that i can find) (jk still love you lavaplayer)
 */

package xyz.stereobot.kt.lavaplayer.filters

import com.github.natanbc.lavadsp.natives.TimescaleNativeLibLoader
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
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
  
      // reverse, because thats what lavalink does lol
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