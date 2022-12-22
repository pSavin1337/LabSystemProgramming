fun main(args: Array<String>) {
    val path = System.getProperty("user.dir")
    val lexemProcessor = LexemProcessor("$path/src/main/kotlin/Program.txt")
    val result = lexemProcessor.processFile()
    println("Lexems: ");
    println("================================================");
    result.first.forEach {
        println(it.toString());
    }
    println("================================================");
    println("Variables: ");
    result.second.forEach {
        println(it.toString());
    }
    println("================================================");

}