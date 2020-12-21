package xyz.stereobot.kt.commands.settings

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class DefaultVolumeCommand : Command() {
  init {
    this.name = "defaultvolume"
    this.aliases = listOf("defaultvol", "dv", "defvol")
    this.info = "Sets the players default volume"
    this.usages = listOf("", "50")
    this.group = "Settings"
    this.guild = true
    this.userPerms = listOf(Permission.MANAGE_CHANNEL)
    this.ratelimit = 7000
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    val volume = ctx.settings.get(ctx.guild.id, "defaultvolume", 100)
    
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#3377de"))
            .setDescription("The default volume is **${volume}**")
            .build()
        )
        .queue()
      
      return
    }
  
    val newVolume: Int? = try {
      Integer.parseInt(args[0])
    } catch (e: Exception) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#3377de"))
            .setDescription("The default volume is **${volume}**")
            .build()
        )
        .queue()
    
      return
    }
  
    if (newVolume == null) {
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
  
    if (newVolume > 100 || newVolume < 1) {
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
    
    if (volume == newVolume) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("f55e53"))
            .setDescription("That is already the default volume")
            .build()
        )
        .queue()
      
      return
    }
    
    ctx.settings.update(ctx.guild.id, "defaultvolume", newVolume)
    
    if (ctx.player.playingTrack != null) {
      ctx.player.volume = newVolume
    }
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Set the new default volume to **${newVolume}**")
          .build()
      )
      .queue()
  }
}