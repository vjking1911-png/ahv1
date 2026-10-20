package com.example.engine

import java.util.Locale

/**
 * Offline Safe Mathematical Expression Tokenizer & Evaluator.
 * Safely parses expressions containing 'CGPA', numbers, parentheses, and arithmetic operators
 * without any dynamic scripting or code execution vectors.
 */
object FormulaEngine {

    sealed class Token {
        data class Number(val value: Double) : Token()
        data class Variable(val name: String) : Token()
        data class Operator(val op: Char) : Token()
        data object OpenParen : Token()
        data object CloseParen : Token()
    }

    fun evaluate(expression: String, cgpaValue: Double): Result<Double> {
        return try {
            val tokens = tokenize(expression)
            val substituted = tokens.map { token ->
                when (token) {
                    is Token.Variable -> {
                        if (token.name.equals("CGPA", ignoreCase = true)) {
                            Token.Number(cgpaValue)
                        } else {
                            throw IllegalArgumentException("Unknown variable '${token.name}'. Use 'CGPA'.")
                        }
                    }
                    else -> token
                }
            }
            val parser = Parser(substituted)
            val result = parser.parseExpression()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val chars = input.toCharArray()

        while (i < chars.size) {
            val c = chars[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < chars.size && (chars[i].isDigit() || chars[i] == '.')) {
                        sb.append(chars[i])
                        i++
                    }
                    tokens.add(Token.Number(sb.toString().toDouble()))
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < chars.size && (chars[i].isLetterOrDigit() || chars[i] == '_')) {
                        sb.append(chars[i])
                        i++
                    }
                    tokens.add(Token.Variable(sb.toString()))
                }
                c in "+-*/^%" -> {
                    tokens.add(Token.Operator(c))
                    i++
                }
                c == '(' -> {
                    tokens.add(Token.OpenParen)
                    i++
                }
                c == ')' -> {
                    tokens.add(Token.CloseParen)
                    i++
                }
                else -> {
                    throw IllegalArgumentException("Unexpected character: '$c'")
                }
            }
        }
        return tokens
    }

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): Token = tokens[pos++]

        fun parseExpression(): Double {
            var value = parseTerm()
            while (peek() is Token.Operator) {
                val op = (peek() as Token.Operator).op
                if (op == '+' || op == '-') {
                    consume()
                    val right = parseTerm()
                    value = if (op == '+') value + right else value - right
                } else {
                    break
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (peek() is Token.Operator) {
                val op = (peek() as Token.Operator).op
                if (op == '*' || op == '/' || op == '%') {
                    consume()
                    val right = parseFactor()
                    value = when (op) {
                        '*' -> value * right
                        '/' -> {
                            if (right == 0.0) throw ArithmeticException("Division by zero")
                            value / right
                        }
                        '%' -> value % right
                        else -> value
                    }
                } else {
                    break
                }
            }
            return value
        }

        private fun parseFactor(): Double {
            var value = parsePrimary()
            if (peek() is Token.Operator && (peek() as Token.Operator).op == '^') {
                consume()
                val exponent = parseFactor()
                value = Math.pow(value, exponent)
            }
            return value
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")
            return when (token) {
                is Token.Number -> {
                    consume()
                    token.value
                }
                is Token.Operator -> {
                    if (token.op == '-') {
                        consume()
                        -parsePrimary()
                    } else if (token.op == '+') {
                        consume()
                        parsePrimary()
                    } else {
                        throw IllegalArgumentException("Unexpected operator: ${token.op}")
                    }
                }
                is Token.OpenParen -> {
                    consume()
                    val value = parseExpression()
                    val closing = peek()
                    if (closing is Token.CloseParen) {
                        consume()
                        value
                    } else {
                        throw IllegalArgumentException("Missing closing parenthesis")
                    }
                }
                else -> throw IllegalArgumentException("Unexpected token: $token")
            }
        }
    }
}
