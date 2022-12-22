object Constants {
    val TYPES: HashMap<String, Pair<Int, String>> = HashMap(
        mapOf(
            Pair("int", Pair(0, "32-bit integer")),
            Pair("uint", Pair(1, "32-bit unsigned integer")),
            Pair("long", Pair(2, "64-bit integer")),
            Pair("ulong", Pair(3, "64-bit unsigned integer")),
            Pair("string", Pair(4, "string of chars"))
        )
    )

    val OPERATORS: HashMap<String, Pair<Int, String>> = HashMap(
        mapOf(
            Pair("=", Pair(0, "assign_operation")),
            Pair("+", Pair(1, "sum_operation")),
            Pair("-", Pair(2, "subtract_operation")),
            Pair("*", Pair(3, "multiply_operation")),
            Pair("/", Pair(4, "divide_operation")),
            Pair("+=", Pair(5, "add_amount_operation")),
            Pair("-=", Pair(6, "subtract_amount_operation")),
            Pair("==", Pair(7, "are_equal_operation")),
            Pair(">", Pair(8, "more_operation")),
            Pair("<", Pair(9, "less_operation")),
            Pair("++", Pair(10, "increment_operation")),
            Pair("--", Pair(11, "decrement_operation")),
            Pair("%", Pair(12, "modulo_operation"))
        )
    )
    val KEYWORDS: List<String> = listOf("for", "if", "else")

    val KEY_SYMBOLS: List<String> = listOf(".", ";", ",", "(", ")", "[", "]", "{", "}")

    sealed class LexemProcessorStates {
        object Idle: LexemProcessorStates()
        object ReadingNum: LexemProcessorStates()
        object Delimeter: LexemProcessorStates()
        object ReadingIdentifier: LexemProcessorStates()
        object Error: LexemProcessorStates()
        object Final: LexemProcessorStates()
    }

    sealed class LexemTypes {
        object ParsingError: LexemTypes()
        object DataType: LexemTypes()
        object Variable: LexemTypes()
        object Delimeter: LexemTypes()
        object Identifier: LexemTypes()
        object Constant: LexemTypes()
        object Operation: LexemTypes()

        override fun toString(): String = this.javaClass.simpleName
    }

}