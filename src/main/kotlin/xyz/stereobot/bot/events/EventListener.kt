package xyz.stereobot.bot.events

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

class EventListener : ListenerAdapter() {
  private val logger = LoggerFactory.getLogger(EventListener::class.java)
  
  override fun onReady(event: ReadyEvent) {
    logger.info("Logged in as {} ({})",
      event.jda.selfUser.asTag,
      event.jda.selfUser.id
    )
  }
}