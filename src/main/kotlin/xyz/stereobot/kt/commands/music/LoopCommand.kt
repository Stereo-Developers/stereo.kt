package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class LoopCommand : Command() {
  init {
    this.name = "loop"
    this.aliases = listOf("repeat", "replay")
    this.info = "Repeats the queue or the track"
    this.use = "<queue|track>"
    this.usages = listOf(
      "",
      "track",
      "queue"
    )
    this.group = "Music"
    this.guild = true
    this.ratelimit = 3000
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.player.playingTrack == null) {
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
    
    if (
      args.isEmpty() ||
      !listOf("queue", "track").contains(args[0].toLowerCase())
    ) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#3377de"))
            .setDescription(
              "You are currently looping ${
                if (ctx.scheduler.repeat == "nothing")
                  "nothing"
                else "the ${ctx.scheduler.repeat}"
              }"
            )
            .build()
        )
        .queue()
      
      return
    }
    
    //ctx.scheduler.repeat = args[0].toLowerCase()
  
    ctx.scheduler.repeat = if (
      args[0].toLowerCase() == ctx.scheduler.repeat
    ) {
      "nothing"
    } else {
      args[0].toLowerCase()
    }
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription(
            "You are now looping ${
              if (ctx.scheduler.repeat == "nothing")
                "nothing"
              else "the ${ctx.scheduler.repeat}"
            }"
          )
          .build()
      )
      .queue()
  }
}