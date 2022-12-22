data class Lexem(val type: Constants.LexemTypes, val lex: Int, val value: String) {

    override fun toString(): String = "lexem type: ${type};\t lexem id: ${lex};\t value: $value"
}

data class Variable (val id: Int, val dataType: String, val name: String) {

    override fun toString(): String = "<${id}> Variable of type <${dataType}> with name <${name}>"
}