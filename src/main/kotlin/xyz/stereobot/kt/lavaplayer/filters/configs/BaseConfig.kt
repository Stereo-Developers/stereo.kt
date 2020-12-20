/**
 * Taken from https://github.com/Frederikam/Lavalink/blob/dev/LavalinkServer/src/main/java/lavalink/server/player/filters/filterConfigs.kt#L99#L102
 * Commit 386c2f3bee435821041c8f81a22bfd511807ae13
 * Thanks Lavaplayer for having no documentation (that i can find) (jk still love you lavaplayer)
 */

package xyz.stereobot.kt.lavaplayer.filters.configs

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

abstract class BaseConfig {
  abstract fun build(format: AudioDataFormat, output: FloatPcmAudioFilter): FloatPcmAudioFilter
  abstract val isEnabled: Boolean
}