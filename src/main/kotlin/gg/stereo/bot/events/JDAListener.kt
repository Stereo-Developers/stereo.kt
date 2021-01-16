package gg.stereo.bot.events

import gg.stereo.bot.Launcher
import gg.stereo.command.CommandLoader
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

class JDAListener(private val commandLoader: CommandLoader) : ListenerAdapter() {
  private val logger = LoggerFactory.getLogger(JDAListener::class.java)
  
  override fun onReady(event: ReadyEvent) {
    logger.info("{} is ready!", event.jda.selfUser.asTag)
  }
  
  override fun onMessageReceived(event: MessageReceivedEvent) {
    if (event.author.isBot || event.author.isFake || event.isWebhookMessage) {
      return
    }
  
    commandLoader.handle(event)
  }
  
  override fun onGuildLeave(event: GuildLeaveEvent) {
    logger.info("Left guild {} ({})",
      event.guild.name, event.guild.id
    )
    
    if (Launcher.playerRegistry.exists(event.guild.idLong)) {
      Launcher.playerRegistry.destroy(event.guild.idLong)
    }
  }
  
  override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
    if (event.member.id == event.jda.selfUser.id) {
      if (Launcher.playerRegistry.exists(event.guild.idLong)) {
        val manager = Launcher.playerRegistry.get(event.guild.idLong)
        
        if (manager.player.playingTrack == null) {
          return // nothing playing, therefore nothing to say
        }
        
        manager.channel?.sendMessage(
          EmbedBuilder()
            .setColor("f55e53".toInt(16))
            .setDescription(
              "I have been disconnected from my voice channel.\n\nThe queue has been cleared."
            ).build()
        )?.queue {
          Launcher.playerRegistry.destroy(event.guild.idLong)
        }
      }
    } else {
      // this is where we check if the bot was left alone
      if (
        event.channelLeft.members.contains(event.guild.selfMember) &&
        event.channelLeft.members.size == 1
      ) {
        if (Launcher.playerRegistry.exists(event.guild.idLong)) {
          val manager = Launcher.playerRegistry.get(event.guild.idLong)
    
          if (manager.player.playingTrack == null) {
            return // nothing playing, therefore nothing to say
          }
    
          manager.channel?.sendMessage(
            EmbedBuilder()
              .setColor("f55e53".toInt(16))
              .setDescription(
                "I've been left alone in my voice channel.\n\nI will be leaving and destroying the player now."
              ).build()
          )?.queue {
            event.guild.audioManager.closeAudioConnection()
            Launcher.playerRegistry.destroy(event.guild.idLong)
          }
        }
      }
    }
  }
}