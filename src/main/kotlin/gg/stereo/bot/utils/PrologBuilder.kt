package gg.stereo.bot.utils

class PrologBuilder {
  private val lines: ArrayList<Line> = arrayListOf()
  
  fun addLine(line: Line): PrologBuilder {
    lines.add(line)
    
    return this
  }
  
  fun removeLine(index: Int): PrologBuilder {
    lines.removeAt(index)
    
    return this
  }
  
  fun removeLine(line: Line): PrologBuilder {
    lines.remove(line)
    
    return this
  }
  
  fun build(): String {
    val padding = fun (): Int {
      var padding = 0
      
      for (line in lines) {
        if (line.name.length > padding) {
          padding = line.name.length
        }
      }
      
      return padding
    }
    
    val stringBuilder = StringBuilder()
    
    stringBuilder.apply {
      append("```prolog\n")
      
      for (line in lines) {
        if (line.name.isNotEmpty() && line.value.isNotEmpty()) {
          append("${line.name.padEnd(padding(), ' ')} : ", line.value)
          append("\n")
        }
      }
      
      append("```")
    }
    
    return stringBuilder.toString()
  }
}

data class Line(val name: String, val value: String)