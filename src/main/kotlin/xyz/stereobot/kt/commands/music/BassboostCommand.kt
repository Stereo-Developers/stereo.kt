package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.lavaplayer.filters.configs.Band
import xyz.stereobot.kt.lavaplayer.filters.configs.Equalizer
import xyz.stereobot.kt.objects.Command

class BassboostCommand : Command() {
  init {
    this.name = "bassboost"
    this.aliases = listOf("bb")
    this.info = "Sets the players bassboost"
    this.use = "<1-10>"
    this.usages = listOf(
      "",
      "5.5"
    )
    this.guild = true
    this.group = "Music"
    this.ratelimit = 7000
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
  
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a bass amount. Must be 1-10")
            .build()
        )
        .queue()
    
      return
    }
  
    val bb: Double = try {
      args[0].toDouble()
    } catch (e: Exception) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Input must be a number.")
            .build()
        )
        .queue()
    
      return
    }
  
    if (bb > 10.0 || bb < 0.0) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a bass amount through 1-10")
            .build()
        )
        .queue()
    
      return
    }
    
    val gain = bb.toFloat() / 10.0f
    
    ctx.scheduler.filterManager.clear()
    
    ctx.scheduler.filterManager.add(
      Equalizer(
        listOf(
          Band(0, -0.05f * gain),
          Band(1, 0f), // dont need to multiply, as 0 * anything = 0
          Band(2, 0.05f * gain),
          Band(3, 0.1f * gain)
        )
      )
    )
    
    ctx.scheduler.filterManager.apply()
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Set the bassboost to **${bb}**")
          .build()
      ).queue()
  }
}