package xyz.stereobot.kt.handlers

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

class ListenerHandler(val waiter: EventWaiter) : ListenerAdapter() {
  private val logger = LoggerFactory.getLogger(ListenerHandler::class.java)
  private val commands = CommandHandler(waiter)
  
  override fun onReady(event: ReadyEvent) {
    logger.info("Logged in as {}", event.jda.selfUser.asTag)
  }
  
  override fun onMessageReceived(event: MessageReceivedEvent) {
    try {
      commands.handle(event)
    } catch (e: Exception) {
      e.printStackTrace()
      
      event.textChannel
        .sendMessage(
          EmbedBuilder()
            .setColor(Integer.parseInt("f55e53", 16))
            .setDescription(
              "We ran into an error!\n```kt\n${e}```"
            )
            .build()
        )
        .queue()
      
      return
    }
  }
}