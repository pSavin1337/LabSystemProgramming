import Constants.LexemProcessorStates
import Constants.LexemTypes
import java.io.File

class LexemProcessor(fileName: String) {

    private var buffer: String = ""
    private var seekingBuffer: String = ""
    private var currentChar: Char
    private var state: LexemProcessorStates = LexemProcessorStates.Idle
    private var lexems: ArrayList<Lexem> = ArrayList()
    private var variablesTable: ArrayList<Variable> = ArrayList()
    private var variablesCounter: Int = 0
    private var program = ""
    private var index = 0

    init {
        program = readFileDirectlyAsText(fileName)
        currentChar = program[index]
    }

    private fun readFileDirectlyAsText(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun processFile(): Pair<List<Lexem>, List<Variable>> {
        while (state != LexemProcessorStates.Final) {
            when (state) {
                is LexemProcessorStates.Idle -> {
                    if (index == program.count()) {
                        state = LexemProcessorStates.Final
                        break
                    }

                    when (true) {
                        isEmptyOrNextLine(currentChar) -> {
                            getNextChar()
                        }
                        currentChar.isLetter() -> {
                            clearBuffer()
                            addToBuffer(currentChar)
                            state = LexemProcessorStates.ReadingIdentifier
                            getNextChar()
                        }
                        currentChar.isDigit() -> {
                            clearBuffer()
                            addToBuffer(currentChar)
                            state = LexemProcessorStates.ReadingNum
                            getNextChar()
                        }
                        else -> {
                            state = LexemProcessorStates.Delimeter
                            addToBuffer(currentChar)
                            getNextChar()
                        }
                    }
                }
                is LexemProcessorStates.ReadingIdentifier -> {
                    if (currentChar.isLetterOrDigit()) {
                        addToBuffer(currentChar)
                        getNextChar()
                    } else {
                        val lexemRef = searchInLexemDictionary()
                        val typeRef = searchInTypesDictionary()
                        if (lexemRef.first != -1) {
                            addLexem(LexemTypes.Identifier, lexemRef.first, lexemRef.second)
                            clearBuffer()
                        } else if (typeRef.first != -1) {
                            addLexem(LexemTypes.DataType, typeRef.first, typeRef.second)
                            clearBuffer()
                        } else {
                            val variable = variablesTable.any { it.name == buffer }
                            if (!variable) {
                                val variableType = lexems.indexOfLast { it.type == LexemTypes.DataType }
                                if (variableType == -1) {
                                    state = LexemProcessorStates.Error
                                    break
                                }
                                variablesTable.add(Variable(variablesCounter++, lexems[variableType].value, buffer))
                                addLexem(
                                    LexemTypes.Variable,
                                    variablesTable.count() - 1,
                                    "variable <${buffer}> of type <${lexems[variableType].type}>"
                                )
                                clearBuffer()
                            } else {
                                addLexem(
                                    LexemTypes.Variable,
                                    variablesTable.indexOfLast { it.name == buffer },
                                    "variable <${buffer}>"
                                )
                                clearBuffer()
                            }
                        }
                        state = LexemProcessorStates.Idle
                    }
                }
                is LexemProcessorStates.ReadingNum -> {
                    if (currentChar.isDigit()) {
                        addToBuffer(currentChar)
                        getNextChar()
                    } else {
                        addLexem(LexemTypes.Constant, buffer.toInt(), "integer with value = $buffer")
                        clearBuffer()
                        state = LexemProcessorStates.Idle
                    }
                }
                is LexemProcessorStates.Delimeter -> {
                    val searchResult = searchInDelimeterDictionary()
                    val searchOperatorsResult = searchInOperationsDictionary()

                    if (searchResult.first != -1) {
                        addLexem(LexemTypes.Delimeter, searchResult.first, searchResult.second)
                        state = LexemProcessorStates.Idle
                        clearBuffer()
                    } else if (searchOperatorsResult.first != -1) {
                        seekingBuffer = "${buffer[0]}${currentChar}"
                        val seekOperatorsResult = seekInOperationsDictionary()
                        if (seekOperatorsResult.first != -1) {
                            addLexem(LexemTypes.Operation, seekOperatorsResult.first, seekOperatorsResult.second)
                            state = LexemProcessorStates.Idle
                            clearBuffer()
                            getNextChar()
                        } else {
                            addLexem(LexemTypes.Operation, searchOperatorsResult.first, searchOperatorsResult.second)
                            state = LexemProcessorStates.Idle
                            clearBuffer()
                        }
                    } else {
                        addLexem(LexemTypes.ParsingError, -1, "Error at ${index}: Could not parse ${buffer}!")
                        state = LexemProcessorStates.Error
                    }
                }
                is LexemProcessorStates.Error -> {
                    state = LexemProcessorStates.Final
                }
                is LexemProcessorStates.Final -> {
                    return Pair<List<Lexem>, List<Variable>>(lexems, variablesTable)
                }
            }
        }

        return Pair<List<Lexem>, List<Variable>>(lexems, variablesTable)
    }

    private fun getNextChar() {
        if (index + 1 >= program.length) {
            state = LexemProcessorStates.Idle
            index++
        } else {
            currentChar = program[++index]
        }
    }

    private fun isEmptyOrNextLine(input: Char): Boolean =
        input == ' ' || input == '\n' || input == '\t' || input == '\r'

    private fun clearBuffer() {
        buffer = ""
        seekingBuffer = ""
    }

    private fun addToBuffer(input: Char) {
        buffer += input
    }

    private fun addLexem(type: LexemTypes, value: Int, lex: String) = lexems.add(Lexem(type, value, lex))

    private fun searchInLexemDictionary(): Pair<Int, String> = with(Constants.KEYWORDS.indexOf(buffer)) {
        if (this == -1)
            Pair(-1, buffer)
        else
            Pair(this, buffer)
    }

    private fun searchInDelimeterDictionary(): Pair<Int, String> = with(Constants.KEY_SYMBOLS.indexOf(buffer)) {
        if (this == -1)
            Pair(-1, buffer)
        else
            Pair(this, buffer)
    }

    private fun searchInTypesDictionary(): Pair<Int, String> = Constants.TYPES[buffer] ?: Pair(-1, buffer)

    private fun searchInOperationsDictionary(): Pair<Int, String> = Constants.OPERATORS[buffer] ?: Pair(-1, buffer)

    private fun seekInOperationsDictionary(): Pair<Int, String> = Constants.OPERATORS[seekingBuffer] ?: Pair(-1, buffer)

}