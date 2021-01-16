package gg.stereo.command.entities.classes

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import gg.stereo.bot.Launcher
import gg.stereo.command.CommandLoader
import gg.stereo.lavaplayer.Manager
import gg.stereo.lavaplayer.Registry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Context(
  val event: MessageReceivedEvent,
  val commandLoader: CommandLoader,
  val waiter: EventWaiter
) {
  val registry: Registry
    get() = Launcher.playerRegistry
  
  val guild: Guild
    get() = event.guild
  
  val message: Message
    get() = event.message
  
  val author: User
    get() = event.author
  
  val member: Member?
    get() = event.member
  
  val jda: JDA
    get() = event.jda
  
  val textChannel: TextChannel
    get() = event.textChannel
  
  val privateChannel: PrivateChannel
    get() = event.privateChannel
  
  val manager: Manager
    get() = Launcher.playerRegistry.get(event.guild.idLong)
  
  val isActive: Boolean
    get() = Launcher.playerRegistry.exists(event.guild.idLong)
  
  fun send(content: String) {
    event.message.textChannel.sendMessage(content).submit()
  }
  
  fun sendError(content: String) {
    sendEmbedded {
      setColor("f55e53".toInt(16))
      setDescription(content)
    }
  }
  
  fun sendEmbedded(embed: EmbedBuilder.() -> Unit) {
    event.message.textChannel.sendMessage(EmbedBuilder().apply(embed).build()).submit()
  }
  
  fun join(vc: VoiceChannel) {
    event.guild.audioManager.openAudioConnection(vc)
    event.guild.audioManager.isSelfDeafened = true
  }
  
  fun disconnect() {
    event.guild.audioManager.closeAudioConnection()
  }
}