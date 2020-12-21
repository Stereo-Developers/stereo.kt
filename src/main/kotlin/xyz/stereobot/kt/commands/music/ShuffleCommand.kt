package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class ShuffleCommand : Command() {
  init {
    this.name = "shuffle"
    this.aliases = listOf("mix", "mixup")
    this.info = "Shuffles the queue around"
    this.guild = true
    this.group = "Music"
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.scheduler.queue.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is no player spawned for this guild.")
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
    
    if (ctx.scheduler.queue.size < 3) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is nothing to even shuffle!")
            .build()
        )
        .queue()
      
      return
    }
    
    ctx.scheduler.shuffle()
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Shuffled the queue successfully")
          .build()
      )
      .queue()
  }
}