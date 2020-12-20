package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class SkipCommand : Command() {
  init {
    this.name = "skip"
    this.aliases = listOf("next", "playnext")
    this.info = "Skips to the next song"
    this.group = "Music"
    this.guild = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.scheduler.queue.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is no upcoming songs to skip to.")
            .build()
        )
        .queue()
    
      return
    }
  
    if (
      !ctx.member!!.voiceState!!.inVoiceChannel() ||
      ctx.vc!!.id != ctx.member.voiceState!!.channel!!.id
    ) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please join my voice channel")
            .build()
        )
        .queue()
    
      return
    }
  
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Skipped the track successfully")
          .build()
      )
      .queue { ctx.scheduler.startNext() }
  }
}