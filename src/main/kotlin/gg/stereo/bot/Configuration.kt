package gg.stereo.bot

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.nio.file.Files
import java.nio.file.Paths

class Configuration {
  private lateinit var config: Config;
  
  init {
    val path = Paths.get("config.conf");
    
    if (Files.isReadable(path)) {
      val config = ConfigFactory.parseFile(path.toFile()).resolve();
      
      this.config = ConfigFactory.load(config);
    }
  }
  
  fun <T> get(path: String): T {
    return this.config.getAnyRef(path) as T;
  }
}