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
import xyz.stereobot.kt.commands.settings.PrefixCommand
import xyz.stereobot.kt.commands.util.*
import xyz.stereobot.kt.database.DatabaseManager
import xyz.stereobot.kt.database.providers.SettingsProvider
import java.util.*

import javax.annotation.Nullable
import kotlin.collections.ArrayList
import kotlin.math.round

class CommandHandler(val waiter: EventWaiter) : ArrayList<Command>() {
  private val logger = LoggerFactory.getLogger(CommandHandler::class.java)
  private val config = Configuration()
  
  val database = DatabaseManager()
  val settings = SettingsProvider(
    database,
    config,
    config.get("database.name"),
    "guilds"
  )
  
  val cooldowns = HashMap<String, Long>()
  
  fun register(command: Command) {
    this.add(command)
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
  
  private fun runPermissionChecks(perms: List<Permission>, member: Member?): List<Permission>? {
    val raw = perms.map { it.rawValue }.reduce { acc, l -> acc + l }
    
    return if (!member!!.hasPermission(perms)) {
      missing(null, member, raw)
    } else {
      null
    }
  }
  
  fun handle(event: MessageReceivedEvent) {
    val content = event.message.contentRaw
    val mentionPrefix = "<@!?(\\d+)>\\s*".toRegex()
    
    var prefix = this.settings.get<List<String>>(
      if (event.isFromGuild)
        event.guild.id
      else "",
      "prefixes",
      config.get("bot.prefixes")
    ).find { content.toLowerCase().startsWith(it.toLowerCase()) }
  
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
      
      if (command.getBotPermissions().isNotEmpty()) {
        val permissions = this.runPermissionChecks(
          command.getBotPermissions(),
          event.guild.selfMember
        )
        
        if (!permissions.isNullOrEmpty()) {
          event.channel
            .sendMessage(
              EmbedBuilder()
                .setColor(Integer.parseInt("f55e53", 16))
                .setDescription(
                  "I am missing the permission${
                    if (permissions.size > 1)
                      "s"
                    else ""
                  }: ${permissions.joinToString(", ") { "`${it.getName()}`" }}."
                )
                .build()
            )
            .queue()
          
          return
        }
      }
  
      if (command.getUserPermissions().isNotEmpty()) {
        val permissions = this.runPermissionChecks(
          command.getUserPermissions(),
          event.member
        )
    
        if (!permissions.isNullOrEmpty()) {
          event.channel
            .sendMessage(
              EmbedBuilder()
                .setColor(Integer.parseInt("f55e53", 16))
                .setDescription(
                  "You are missing the permission${
                    if (permissions.size > 1)
                      "s"
                    else ""
                  }: ${permissions.joinToString(", ") { "`${it.getName()}`" }}."
                )
                .build()
            )
            .queue()
      
          return
        }
      }
      
      if (
        command.getCooldown() != null &&
        !config.get<List<String>>("bot.owners").contains(event.author.id)
      ) {
        val cooldownKey = "${event.author.id}_${
          if (event.isFromGuild)
            event.guild.id
          else "DIRECT"
        }_${command.name}"
  
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
    register(PingCommand(database))
    register(HelpCommand(this))
    
    register(PlayCommand(this))
    register(LyricsCommand())
    register(NowPlayingCommand())
    register(QueueCommand())
    register(SkipCommand())
    register(DisconnectCommand())
    register(NightCoreCommand())
    register(BassboostCommand())
    register(ShuffleCommand())
    
    register(EvalCommand(this))
    register(ExecuteCommand())
    
    register(PrefixCommand(this))
  }
}