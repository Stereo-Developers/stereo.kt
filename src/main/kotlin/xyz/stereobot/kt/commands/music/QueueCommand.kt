package xyz.stereobot.kt.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command
import java.lang.StringBuilder

class QueueCommand : Command() {
  init {
    this.name = "queue"
    this.aliases = listOf("q", "upnext")
    this.info = "Displays the players queue"
    this.use = "<page>"
    this.usages = listOf("", "2")
    this.guild = true
    this.group = "Music"
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.scheduler.queue.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is no upcoming songs to display.")
            .build()
        )
        .queue()
      
      return
    }
    
    val page = if (args.isEmpty()) {
      1
    } else {
      try {
        Integer.parseInt(args[0])
      } catch (e: Exception) {
        1
      }
    }
    
    println(page)
    
    val display = ctx.paged<AudioTrack>(ctx.scheduler.queue.toList(), page, 10)
    
    val embed = EmbedBuilder()
      .setColor(getColor("#3377de"))
      .setAuthor("Queue for ${ctx.guild.name}", null, ctx.guild.iconUrl)
  
    var description = StringBuilder()
    var i = (page - 1) * 10
    
    for (song in display.list) {
      i++
      
      val title = if (song.info.title.length < 45) {
        song.info.title
      } else {
        song.info.title.substring(0, 45) + "..."
      }
      
      description
        .append(
          "`#${i.toString().padStart(2, '0')}` | "
        )
        .append("[${title}](${song.info.uri})")
        .append("\n")
    }
    
    if (description.length > 2000) {
      embed.setDescription(description.substring(0, 2000))
    } else {
      embed.setDescription(description)
    }
    
    ctx.channel.sendMessage(
      embed
        .setFooter("${display.page}/${display.max}")
        .build()
    ).queue()
  }
}