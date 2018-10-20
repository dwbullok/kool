package com.codeviking.kxg

/**
 * @author fabmax
 */
class KxgException(message: String, cause: Exception?) : Exception(message, cause) {
    constructor(message: String): this(message, null)
}
