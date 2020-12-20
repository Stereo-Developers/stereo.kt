package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.handlers.CommandHandler
import xyz.stereobot.kt.lavaplayer.PlayerManager
import xyz.stereobot.kt.objects.Command
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class PlayCommand(val commands: CommandHandler) : Command() {
  init {
    this.name = "play"
    this.aliases = listOf("p", "addsong", "song", "playsong")
    this.info = "Plays music in your voice channel"
    this.group = "Music"
    this.usages = listOf(
      "https://www.youtube.com/watch?v=xaazUgEKuVA", // good video xd
      "iann dior - holding on"
    )
    this.use = "[song or url]"
    this.guild = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    var song = args.joinToString(" ")
  
    if (song.isEmpty()) {
      ctx.channel.sendMessage(
        EmbedBuilder()
          .setColor(Integer.parseInt("f55e53", 16))
          .setDescription("Please provide a song to play")
          .setFooter("Type \"cancel\" to cancel this prompt")
          .build()
      ).queue()
  
      ctx.waiter.waitForEvent(
        MessageReceivedEvent::class.java,
        { event -> event.author.id == ctx.author.id && event.messageId != ctx.event.messageId },
        { event ->
          val content = event.message.contentRaw
      
          if (content.toLowerCase() == "cancel") {
            ctx.channel.sendMessage(
              EmbedBuilder()
                .setColor(Integer.parseInt("f55e53", 16))
                .setDescription("You took too long to answer, I'll be cancelling the prompt now.")
                .build()
            ).queue()
        
            return@waitForEvent
          }
      
          song = content
  
          val check = this.check(ctx)
          if (check) {
            return@waitForEvent
          }
          
          if (isUrl(song)) {
            PlayerManager.getInstance().loadAndPlay(ctx, song)
            return@waitForEvent
          }
  
          PlayerManager.getInstance().loadAndPlay(
            ctx,
            "ytsearch:${URLEncoder.encode(song, Charsets.UTF_8)}"
          )
        },
        15L,
        TimeUnit.SECONDS,
        {
          ctx.channel.sendMessage(
            EmbedBuilder()
              .setColor(Integer.parseInt("f55e53", 16))
              .setDescription("You took too long to answer, I'll be cancelling the prompt now.")
              .build()
          ).queue()
        }
      )
    } else {
      val check = this.check(ctx)
      if (check) {
        return
      }
      
      if (isUrl(song)) {
        PlayerManager.getInstance().loadAndPlay(ctx, song)
        return
      }
  
      PlayerManager.getInstance().loadAndPlay(
        ctx,
        "ytsearch:${URLEncoder.encode(song, Charsets.UTF_8)}"
      )
    }
  }
  
  private fun check(ctx: Context): Boolean {
    if (!ctx.guild.selfMember.voiceState!!.inVoiceChannel()) {
      if (!ctx.member!!.voiceState!!.inVoiceChannel()) {
        ctx.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(getColor("#f55e53"))
              .setDescription("Please join a voice channel")
              .build()
          )
          .queue()
      
        return true
      }
    
      val vc = ctx.member.voiceState!!.channel!!
    
      val permissions = this.commands.missing(
        vc,
        ctx.guild.selfMember,
        Permission.getRaw(
          Permission.VOICE_CONNECT,
          Permission.VOICE_SPEAK,
          Permission.VIEW_CHANNEL
        )
      )
    
      if (permissions.isNotEmpty()) {
        ctx.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(getColor("#f55e53"))
              .setDescription(
                "I cannot join that voice channel due to invalid permissions."
              ).appendDescription("\n\n")
              .appendDescription(
                "I am missing: ${
                  permissions.joinToString(", ") { "`${it.getName()}`" }
                }"
              )
              .build()
          )
          .queue()
      
        return true
      }
    
      if (vc.userLimit == vc.members.size) {
        ctx.channel
          .sendMessage(
            EmbedBuilder()
              .setColor(getColor("#f55e53"))
              .setDescription("I cannot join that voice channel because it is full.")
              .build()
          )
          .queue()
      
        return true
      }
    
      ctx.join(vc)
    }
    
    return false
  }
  
  private fun isUrl(check: String): Boolean {
    return try {
      URL(check)
      true
    } catch (e: Exception) {
      false
    }
  }
}