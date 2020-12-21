package xyz.stereobot.kt.commands.music

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
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
  
  }
}