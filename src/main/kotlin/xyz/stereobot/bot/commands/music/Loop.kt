package xyz.stereobot.bot.commands.music

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog
import xyz.stereobot.bot.utils.extensions.isActive
import xyz.stereobot.bot.utils.extensions.player

class Loop : Cog {
  @Command(
    aliases = ["repeat"],
    description = "Repeats the track or queue",
    guildOnly = true
  )
  fun loop(ctx: Context, type: String? = null) {
    if (!ctx.isActive) {
      return ctx.send {
        setColor(Integer.parseInt("f55e53", 16))
        setDescription("There is no active player in the guild")
      }
    }
    
    if (
      type == null ||
      !arrayOf("queue", "track", "nothing").contains(type.toLowerCase())
    ) {
      return ctx.send {
        setColor(Integer.parseInt("f55e53", 16))
        setDescription("You are currently looping ${
          if (ctx.player.repeat.name == "nothing")
            "nothing."
          else "the ${ctx.player.repeat.name}"
        }")
      }
    }
    
    ctx.player.loop(type.toLowerCase())
    
    return ctx.send {
      setColor(Integer.parseInt("f55e53", 16))
      setDescription("You are now looping ${
        if (type.toLowerCase() == "nothing")
          "nothing."
        else "the ${type.toLowerCase()}"
      }")
    }
  }
}