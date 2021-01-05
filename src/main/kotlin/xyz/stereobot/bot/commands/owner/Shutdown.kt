package xyz.stereobot.bot.commands.owner

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog
import kotlin.system.exitProcess

class Shutdown : Cog {
  @Command(
    aliases = ["turnoff", "shut"],
    description = "Shuts off the bot",
    developerOnly = true
  )
  fun shutdown(ctx: Context) {
    ctx.message
      .addReaction("\uD83D\uDC4B")
      .queue {
        ctx.jda.shutdownNow()
        exitProcess(21)
      }
  }
}