package xyz.stereobot.kt.database.providers

import com.mongodb.MongoClientSettings
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import xyz.stereobot.kt.Configuration
import xyz.stereobot.kt.database.DatabaseManager

class SettingsProvider(
  val database: DatabaseManager,
  val config: Configuration,
  val dbName: String,
  val collectionName: String,
) {
  private val items = HashMap<String, Document>()
  private val collection = database.client.getDatabase(dbName).getCollection(collectionName)
  
  fun init(id: String): Document {
    val settings = collection.find().toList().find { it["id"]!! == id }
    
    return if (settings.isNullOrEmpty()) {
      val document = Document()
      document["id"] = id
      document["prefixes"] = config.get<List<String>>("bot.prefixes")
      document["dj"] = null
      document["vclock"] = null
      
      this.collection.insertOne(document)
      
      this.items[id] = document
      
      document
    } else {
      settings
    }
  }
  
  fun <T> get(id: String, key: String, defaultValue: T): T {
    val data = this.items[id] ?: this.init(id)
    
    return (data[key] ?: defaultValue) as T
  }
  
  fun update(id: String, key: String, value: Any): Document {
    val data = this.items[id] ?: this.init(id)
    
    val doc = Document()
    doc.putAll(data)
    doc.remove("_id_")
    doc[key] = value
    
    collection.replaceOne(
      data.toBsonDocument(
        data::class.java,
        MongoClientSettings.getDefaultCodecRegistry()
      ),
      doc,
      ReplaceOptions().upsert(true)
    )

    this.items.put(id, doc)
    return data
  }
}