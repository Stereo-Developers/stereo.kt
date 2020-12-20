package xyz.stereobot.kt.database

import com.mongodb.event.ServerHeartbeatFailedEvent
import com.mongodb.event.ServerHeartbeatStartedEvent
import com.mongodb.event.ServerHeartbeatSucceededEvent
import com.mongodb.event.ServerMonitorListener
import org.slf4j.LoggerFactory

class Monitor : ServerMonitorListener {
  private val logger = LoggerFactory.getLogger(Monitor::class.java)
  
  var latency: Long = 0
    private set
  
  var lastHeartbeated: Long? = null
  
  override fun serverHearbeatStarted(event: ServerHeartbeatStartedEvent?) {
    lastHeartbeated = System.currentTimeMillis()
  }
  
  override fun serverHeartbeatSucceeded(event: ServerHeartbeatSucceededEvent?) {
    latency = System.currentTimeMillis() - lastHeartbeated!!
    lastHeartbeated = null
  }
  
  override fun serverHeartbeatFailed(event: ServerHeartbeatFailedEvent?) {
    event!!.throwable.printStackTrace()
  }
}