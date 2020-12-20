package xyz.stereobot.kt.database

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.slf4j.LoggerFactory
import xyz.stereobot.kt.Configuration

class DatabaseManager {
  private val config = Configuration()
  private val logger = LoggerFactory.getLogger(DatabaseManager::class.java)
  private var connectedIn: Long = 0
  
  var client: MongoClient
  val monitor = Monitor()
  
  init {
    val db = config.get<MongoDatabaseOptions>("database")
    
    val settings = MongoClientSettings.builder()
      .applyToServerSettings { it.addServerMonitorListener(monitor) }
      .applicationName("main")
      .applyConnectionString(
        ConnectionString(
          "mongodb://${db.user}:${db.password}@${db.host}:${db.port}/${db.name}"
        )
      )
      .build()
  
    val before = System.currentTimeMillis()
    client = MongoClients.create(settings)
    connectedIn = System.currentTimeMillis() - before
    
    logger.info("Connected to mongodb in ${before}ms.")
  }
}

interface MongoDatabaseOptions {
  val host: String
  val port: Int
  val user: String
  val password: String
  val name: String
}