package gg.stereo.command

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import gg.stereo.bot.Launcher
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*

class CommandLoader(
  val owners: List<String>,
  private val packageName: String,
  val prefixes: List<String>,
  val waiter: EventWaiter
) {
  private val reflections = Reflections(packageName)
  private val logger = LoggerFactory.getLogger(CommandLoader::class.java)
  
  val commands: ArrayList<Command> = arrayListOf()
  
  fun handle(event: MessageReceivedEvent) {
    val raw = event.message.contentRaw
    
    var prefix = prefixes.find { raw.startsWith(it.toLowerCase()) } ?: ""
    
    if (
      prefix.isEmpty() &&
      event.message.mentionedUsers.contains(event.jda.selfUser)
    ) {
      val mentionPrefix = "<@!?(\\d+)>\\s*".toRegex()
      
      if (mentionPrefix.containsMatchIn(raw)) {
        val groups = mentionPrefix.find(raw)!!.groupValues
        
        if (groups[1] != event.jda.selfUser.id) {
          return
        }
        
        prefix = groups[0]
      }
    }
    
    if (prefix.isEmpty() || !raw.startsWith(prefix)) {
      return
    }
    
    val preArgs = raw.replace(prefix, "").split(" ")
    
    val command = getCommand(preArgs[0].toLowerCase())
    
    if (command != null) {
      val ctx = Context(event, this, waiter)
      
      if (command.getRestrictions() != null) {
        val restrictions = command.getRestrictions()!!
        
        if (restrictions.guild && !event.isFromGuild) {
          return ctx.sendError("You must run this command in a guild.")
        }
        
        if (restrictions.owner && !owners.contains(ctx.event.author.id)) {
          return ctx.sendError(
            "You cannot run this command, as it is locked to owners only."
          )
        }
        
        if (restrictions.inVc) {
          if (!ctx.event.member?.voiceState?.inVoiceChannel()!!) {
            return ctx.sendError("Please join a voice channel")
          }
          
          if (
            ctx.event.guild.selfMember.voiceState?.inVoiceChannel()!! &&
            ctx.event.member?.voiceState?.channel?.id !=
            ctx.event.guild.selfMember.voiceState?.channel?.id
          ) {
            return ctx.sendError("Please join my voice channel")
          }
        }
        
        if (restrictions.vcJoinCheck) {
          val vc = ctx.member?.voiceState?.channel
  
          if (vc?.userLimit != 0 && vc?.userLimit == vc?.members?.size) {
            return ctx.sendError("Your voice channel is full")
          }
  
          if (
            !ctx.guild.selfMember.hasPermission(
              vc!!,
              Permission.VOICE_CONNECT, Permission.VOICE_SPEAK
            )
          ) {
            return ctx.sendError(
              "I cannot join your voice channel due to insufficent permissions"
            )
          }
        }
      }
      
      try {
        command.executor(ctx, preArgs.subList(1, preArgs.size))
        
        logger.info("${ctx.event.author.asTag} ran the ${command.getInfo()!!.trigger} command")
      } catch(e: Exception) {
        ctx.sendEmbedded {
          setColor("f55e53".toInt(16))
          setDescription(
            "An unexpected exception was thrown,```kt\n${e.toString().take(1960)}```"
          )
        }
      }
    }
  }
  
  fun getCommand(command: String): Command? {
    return commands.find {
      it.getInfo() != null &&
        it.getInfo()!!.trigger.equals(command, ignoreCase = true) ||
        it.getInfo()!!.triggers.contains(command)
    }
  }
  
  fun load() {
    val modules = reflections.getSubTypesOf(Command::class.java)
    
    for (mod in modules) {
      if (mod.constructors.isEmpty()) {
        continue
      }
      
      val command = mod.getConstructor().newInstance()
      
      if (command.getInfo() == null) {
        continue
      }
      
      commands.add(command)
    }
    
    logger.info("Loaded {} commands", commands.size)
  }
}