package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command
import kotlin.math.floor
import kotlin.math.roundToInt

class NowPlayingCommand : Command() {
  init {
    this.name = "nowplaying"
    this.aliases = listOf("playing", "np")
    this.info = "Displays the currently playing song"
    this.group = "Music"
    this.guild = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.player.playingTrack == null) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is nothing currently playing.")
            .build()
        )
        .queue()
    
      return
    }
    
    val track = ctx.player.playingTrack
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setAuthor(
            if (track.info.title.length > 34)
              track.info.title.substring(0, 31) + "..."
            else track.info.title,
            track.info.uri,
            ctx.author.effectiveAvatarUrl
          )
          .setThumbnail(
            "https://i.ytimg.com/vi/${track.identifier}/maxresdefault.jpg"
          )
          .setDescription("${
            if (ctx.player.isPaused) "⏸️" else "▶"
          } `${
            parsePlayerTime(track.position)
          } ${makeProgressBar(20, track.position, track.duration)} ${
            parsePlayerTime(track.duration)
          }`")
          .build()
      )
      .queue()
  }
  
  private fun makeProgressBar(length: Int, position: Long, duration: Long): String {
    val bar = StringBuilder()
    
    bar
      .append("\u25AC".repeat((position.toDouble() / duration.toDouble() * length).roundToInt()))
      .append("o")
      .append("\u23BC".repeat((length - position.toDouble() / duration.toDouble() * length).toInt()))
    
    return bar.toString()
  }
  
  private fun parsePlayerTime(time: Long): String {
    if (time >= 36000000) {
      return "..."
    }
    
    val hours: Number = floor(time / (1e3 * 60 * 60) % 60)
    val minutes: Number = floor(time / 6e4)
    val seconds: Number = time % 6e4 / 1e3
    
    return if (hours == 0) {
      "${forceTwoDigits(hours.toInt())}:${forceTwoDigits(minutes.toInt())}:${forceTwoDigits(seconds.toInt())}"
    } else {
      "${forceTwoDigits(minutes.toInt())}:${forceTwoDigits(seconds.toInt())}"
    }
  }
  
  private fun forceTwoDigits(number: Int): String {
    return if (number < 10) "0$number" else number.toString()
  }
}