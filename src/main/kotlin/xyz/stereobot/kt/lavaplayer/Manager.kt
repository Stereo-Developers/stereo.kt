package xyz.stereobot.kt.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

class Manager(manager: AudioPlayerManager) {
  val player: AudioPlayer = manager.createPlayer()
  val scheduler: Scheduler
  val handler: AudioSender
  
  init {
    scheduler = Scheduler(player)
    player.addListener(scheduler)
    handler = AudioSender(player)
  }
}