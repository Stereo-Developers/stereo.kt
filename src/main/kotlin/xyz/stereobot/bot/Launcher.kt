package xyz.stereobot.bot

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import xyz.stereobot.bot.events.EventListener
import xyz.stereobot.bot.events.FlightEventListener
import xyz.stereobot.lavaplayer.Registry

object Launcher {
  val logger = LoggerFactory.getLogger(Launcher::class.java)
  val config = Configuration()
  val waiter = EventWaiter()
  val playerRegistry = Registry()
  
  @ExperimentalStdlibApi
  @JvmStatic
  fun main(args: Array<String>) {
    logger.info("Welcome to Stereo!")
    
    val commandClient = CommandClientBuilder()
      .setPrefixes(config.get<List<String>>("bot.prefixes"))
      .setOwnerIds(*config.get<ArrayList<String>>("bot.owners").toTypedArray())
      .addEventListeners(FlightEventListener())
      .registerDefaultParsers()
      .configureDefaultHelpCommand { enabled = false }
      .build()
    
    val jda = JDABuilder.createLight(config.get("bot.token"))
      .addEventListeners(EventListener(), commandClient, waiter)
      .setMemberCachePolicy(MemberCachePolicy.VOICE)
      .enableCache(CacheFlag.VOICE_STATE)
      .disableCache(
        CacheFlag.ACTIVITY,
        CacheFlag.CLIENT_STATUS,
        CacheFlag.EMOTE,
        CacheFlag.MEMBER_OVERRIDES
      ).setEnabledIntents(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_VOICE_STATES
      ).build()
    
    commandClient.commands.register("xyz.stereobot.bot.commands")
  }
}