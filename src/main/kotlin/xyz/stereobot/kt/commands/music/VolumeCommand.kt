package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class VolumeCommand : Command() {
  init {
    this.name = "volume"
    this.aliases = listOf("vol", "setvolume")
    this.info = "Sets the players volume"
    this.use = "<volume>"
    this.usages = listOf(
      "",
      "50"
    )
    this.guild = true
    this.group = "Music"
    this.ratelimit = 3000
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.player.playingTrack == null) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is no player spawned in the guild")
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
    
    val volume: Int? = try {
      Integer.parseInt(args[0])
    } catch (e: Exception) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#3377de"))
            .setDescription("The current volume is **${ctx.player.volume}/100**")
            .build()
        )
        .queue()
      
      return
    }
    
    if (volume == null) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a valid number")
            .build()
        )
        .queue()
  
      return
    }
    
    if (volume > 100 || volume < 1) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a volume from 1-100")
            .build()
        )
        .queue()
  
      return
    }
    
    ctx.player.volume = volume
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Set the players volume to **${volume}**")
          .build()
      )
      .queue()
  }
}