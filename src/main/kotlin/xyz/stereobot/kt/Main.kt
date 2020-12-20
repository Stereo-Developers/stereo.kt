package xyz.stereobot.kt

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import xyz.stereobot.kt.handlers.ListenerHandler

fun main(args: Array<String>) {
  val config = Configuration()
  
  val waiter = EventWaiter()
  
  JDABuilder.createLight(config.get<String>("bot.token"))
    .addEventListeners(ListenerHandler(waiter), waiter)
    .setActivity(Activity.listening("${config.get<List<String>>("bot.prefixes")[0]}help"))
    .setStatus(OnlineStatus.DO_NOT_DISTURB)
    .setMemberCachePolicy(MemberCachePolicy.VOICE)
    .enableIntents(GatewayIntent.getIntents(641))
    .disableIntents(GatewayIntent.getIntents(32126))
    .enableCache(CacheFlag.VOICE_STATE)
    .disableCache(
      CacheFlag.ACTIVITY,
      CacheFlag.EMOTE,
      CacheFlag.MEMBER_OVERRIDES,
      CacheFlag.CLIENT_STATUS
    )
    .build()
}