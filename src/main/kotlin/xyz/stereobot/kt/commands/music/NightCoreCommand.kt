package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.lavaplayer.filters.configs.Timescale
import xyz.stereobot.kt.lavaplayer.filters.configs.Tremolo
import xyz.stereobot.kt.objects.Command

class NightCoreCommand : Command() {
  init {
    this.name = "nightcore"
    this.aliases = listOf("nc")
    this.info = "Turns on or off the nightcore filter"
    this.group = "Music"
    this.guild = true
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
    
    if (
      !listOf("nightcore", "nothing").contains(
        ctx.scheduler.filterManager.filter
      )
    ) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription(
              "Please turn off any other filter before turning on the nightcore filter."
            )
            .build()
        )
        .queue()
  
      return
    }
    
    when (ctx.scheduler.filterManager.filter) {
      "nightcore" -> {
        ctx.scheduler.filterManager.filter = "nothing"
        ctx.scheduler.filterManager.clear()
        
        ctx.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(getColor("#f55e53"))
              .setDescription("Turned off the nightcore filter")
              .setFooter("Please, give the bot a second to turn it off.")
              .build()
          )
          .queue()
        
        return
      }
      "nothing" -> {
        ctx.scheduler.filterManager.filter = "nightcore"
        
        ctx.scheduler.filterManager.addMany(
          listOf(
            Tremolo(14f, 0.3f),
            Timescale(1.2, 1.2),
          )
        )
        
        ctx.scheduler.filterManager.apply()
  
        ctx.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(getColor("#f55e53"))
              .setDescription("Turned on the nightcore filter")
              .setFooter("Please, give the bot a second to turn it on.")
              .build()
          )
          .queue()
  
        return
      }
    }
  }
}