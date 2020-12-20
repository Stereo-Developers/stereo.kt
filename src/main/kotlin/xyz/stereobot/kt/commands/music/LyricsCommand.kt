package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command
import khttp.get
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class LyricsCommand : Command() {
  init {
    this.name = "lyrics"
    this.aliases = listOf("l", "songlyrics")
    this.info = "Displays the lyrics of a song"
    this.use = "[song name]"
    this.usages = listOf(
      "tate mcrae - stupid",
      "iann dior - holding on",
      "never gonna give you up"
    )
    this.group = "Music"
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a song name")
            .build()
        )
        .queue()
      
      return
    }
    
    val response = get(
      "https://some-random-api.ml/lyrics?title=${
        URLEncoder.encode(
          args.joinToString(" "),
          StandardCharsets.UTF_8
        )
      }"
    ).jsonObject
    
    if (!response.optString("error").isNullOrBlank()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription(
              "I couldn't find anything for that query. Check your spelling?"
            )
            .build()
        )
        .queue()
      
      return
    }
  
    val lyrics = response.getString("lyrics")
  
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setAuthor(
            "${response.getString("title")} - ${response.getString("author")}",
            response.getJSONObject("thumbnail").getString("genius"),
            response.getJSONObject("links").getString("genius")
          )
          .setDescription(
            if (lyrics.length > 1950)
              lyrics.substring(0, 1950) + "..."
            else lyrics
          )
          .build()
      )
      .queue()
  }
}