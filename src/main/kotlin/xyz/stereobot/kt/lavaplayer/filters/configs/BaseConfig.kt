package xyz.stereobot.kt.lavaplayer.filters.configs

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

abstract class BaseConfig {
  abstract fun build(format: AudioDataFormat, output: FloatPcmAudioFilter): FloatPcmAudioFilter
  abstract val isEnabled: Boolean
}