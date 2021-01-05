package xyz.stereobot.bot.events

import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.exceptions.BadArgument
import me.devoxin.flight.api.hooks.DefaultCommandEventAdapter
import net.dv8tion.jda.api.Permission
import org.slf4j.LoggerFactory
import kotlin.math.round

class FlightEventListener : DefaultCommandEventAdapter() {
  private val logger = LoggerFactory.getLogger(FlightEventListener::class.java)
  
  override fun onCommandPostInvoke(ctx: Context, command: CommandFunction, failed: Boolean) {
    logger.info("{} ran the {} command{}",
      ctx.author.asTag,
      command.name,
      if (failed) " which failed." else ""
    )
  }
  
  override fun onCommandCooldown(ctx: Context, command: CommandFunction, cooldown: Long) {
    ctx.send {
      setColor(Integer.parseInt("f55e53", 16))
      setDescription(
        "Woah there! You have to wait **${
          round(cooldown.toDouble() * 1000)
        }s** before using this command again."
      )
    }
  }
  
  override fun onBadArgument(ctx: Context, command: CommandFunction, error: BadArgument) {
    if (error.argument.type.isEnum) {
      val options = error.argument.type.enumConstants.map { it.toString().toLowerCase() }
      
      return ctx.send {
        setColor(Integer.parseInt("f55e53", 16))
        setDescription("You provided an invalid argument for **${error.argument.name}**.")
        addField(
          "› Valid Options (${options.size})",
          options.joinToString(",\n") { "• `$it`" },
          false
        )
      }
    } else {
      if (error.providedArgument.isEmpty()) {
        return ctx.send {
          setColor(Integer.parseInt("f55e53", 16))
          setDescription(
            "You provided an invalid argument for **${
              error.argument.name
            }**.\n\nThe provided argument was empty."
          )
        }
      } else {
        return ctx.send {
          setColor(Integer.parseInt("f55e53", 16))
          setDescription(
            "You provided an invalid argument for **${
              error.argument.name
            }**.\n\nExpected Type: `${
              error.argument.type.simpleName.toLowerCase()
            }`"
          )
        }
      }
    }
  }
}