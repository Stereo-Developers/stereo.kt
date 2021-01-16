package gg.stereo.bot

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import gg.stereo.bot.events.JDAListener
import gg.stereo.command.CommandLoader
import gg.stereo.command.CommandLoaderBuilder
import gg.stereo.lavaplayer.Registry
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

object Launcher {
  val waiter = EventWaiter()
  val config = Configuration()
  val playerRegistry = Registry()
  
  lateinit var commandLoader: CommandLoader
  lateinit var jda: JDA
  
  @JvmStatic
  fun main(args: Array<String>) {
    commandLoader = CommandLoaderBuilder()
      .setCommandPackage("gg.stereo.bot.commands")
      .setWaiter(waiter)
      .setOwners(config.get("bot.owners"))
      .setPrefixes(config.get("bot.prefixes"))
      .build()
    
    jda = JDABuilder.createLight(config.get("bot.token"))
      .addEventListeners(JDAListener(commandLoader), waiter)
      .setMemberCachePolicy(MemberCachePolicy.VOICE)
      .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
      .enableCache(CacheFlag.VOICE_STATE)
      .disableCache(
        CacheFlag.MEMBER_OVERRIDES,
        CacheFlag.EMOTE,
        CacheFlag.CLIENT_STATUS,
        CacheFlag.ACTIVITY
      ).build()
    
    commandLoader.load()
  }
}