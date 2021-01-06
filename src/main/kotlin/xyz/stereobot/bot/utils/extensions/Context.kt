package xyz.stereobot.bot.utils.extensions

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.devoxin.flight.api.Context
import xyz.stereobot.bot.Launcher
import xyz.stereobot.lavaplayer.Manager
import xyz.stereobot.lavaplayer.Registry

val Context.waiter: EventWaiter
  get() = Launcher.waiter

val Context.playerRegistry: Registry
  get() = Launcher.playerRegistry

val Context.player: Manager
  get() = Launcher.playerRegistry.get(this.guild!!.idLong)

val Context.isActive: Boolean
  get() = this.player.player.playingTrack != null