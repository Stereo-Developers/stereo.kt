package xyz.stereobot.kt.handlers

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory
import xyz.stereobot.kt.Configuration
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

import xyz.stereobot.kt.commands.music.*
import xyz.stereobot.kt.commands.owner.*
import xyz.stereobot.kt.commands.util.*
import java.util.*

import javax.annotation.Nullable
import kotlin.collections.ArrayList
import kotlin.math.round

class CommandHandler(val waiter: EventWaiter) : ArrayList<Command>() {
  private val logger = LoggerFactory.getLogger(CommandHandler::class.java)
  private val config = Configuration()
  
  val cooldowns = HashMap<String, Long>()
  
  fun register(command: Command) {
    this.add(command)
  }
  
  fun unregister(command: Command) {
    this.remove(command)
  }
  
  fun findCommand(search: String): Command? {
    return this.find {
      it.name.equals(search, ignoreCase = true) ||
        it.aliases.contains(search.toLowerCase())
    }
  }
  
  fun missing(@Nullable channel: GuildChannel?, member: Member?, bit: Long): List<Permission> {
    val array = Permission.getPermissions(bit)
    
    return if (channel == null) {
      array.filter { !member!!.hasPermission(Permission.getPermissions(it.rawValue)) }
    } else {
      array.filter { !member!!.hasPermission(channel, Permission.getPermissions(it.rawValue)) }
    }
  }
  
  fun handle(event: MessageReceivedEvent) {
    val content = event.message.contentRaw
    val mentionPrefix = "<@!?(\\d+)>\\s*".toRegex()
    
    var prefix = config.get<List<String>>("bot.prefixes")
      .find {
        content.startsWith(it.toLowerCase())
      }
  
    if (mentionPrefix.containsMatchIn(content)) {
      val found = mentionPrefix.find(content)
    
      if (found!!.groupValues[1] != event.jda.selfUser.id) {
        return
      }
    
      prefix = found.groupValues[0]
    }
  
    if (prefix == null || !content.startsWith(prefix)) {
      return
    }
  
    val preArgs = content
      .replaceFirst(prefix, "")
      .split("\\s+".toRegex())
    
    val command = findCommand(preArgs[0].toLowerCase())
    
    if (command != null) {
      if (
        command.owner &&
        !config.get<List<String>>("bot.owners").contains(event.author.id)
      ) {
        event.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(Integer.parseInt("f55e53", 16))
              .setDescription("This command is restricted to owner only.")
              .build()
          )
          .queue()
        
        return
      }
      
      if (command.guild && !event.isFromGuild) {
        event.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(Integer.parseInt("f55e53", 16))
              .setDescription(
                "This command is restricted to servers only. [Invite](${
                  config.get<String>("bot.invite")
                }) the bot to a server to use this command."
              )
              .build()
          )
          .queue()
  
        return
      }
      
      if (command.getCooldown() != null) {
        val cooldownKey = "${event.author.id}_${command.name}"
  
        if (this.cooldowns.containsKey(cooldownKey)) {
          val cooldown = this.cooldowns.get(cooldownKey)!!
          
          if (Calendar.getInstance().timeInMillis >= cooldown) {
            this.cooldowns.remove(cooldownKey)
          } else {
            val time = round((cooldown - Calendar.getInstance().timeInMillis).toDouble())
      
            event.channel
              .sendMessage(
                EmbedBuilder()
                  .setColor(Integer.parseInt("f55e53", 16))
                  .setDescription(
                    "Woah there! Please wait **${round(time / 1000).toInt()}s** until using this command again."
                  )
                  .build()
              )
              .queue()
      
            return
          }
        } else {
          this.cooldowns[cooldownKey] = Calendar.getInstance().timeInMillis + command.getCooldown()!!
        }
  
        this.cooldowns[cooldownKey] = Calendar.getInstance().timeInMillis + command.getCooldown()!!
      }
      
      val args = preArgs.subList(1, preArgs.size)
      
      var before = System.currentTimeMillis()
      command.invoke(Context(event, waiter), args)
      before = System.currentTimeMillis() - before
      
      logger.info(
        "{} ran command {} in {}ms",
        event.author.asTag,
        command.name,
        before
      )
    }
  }
  
  init {
    register(PingCommand())
    register(HelpCommand(this))
    
    register(PlayCommand(this))
    register(LyricsCommand())
    register(NowPlayingCommand())
    register(QueueCommand())
    register(SkipCommand())
    register(DisconnectCommand())
    register(NightCoreCommand())
    register(BassboostCommand())
    
    register(EvalCommand())
    register(ExecuteCommand())
  }
}