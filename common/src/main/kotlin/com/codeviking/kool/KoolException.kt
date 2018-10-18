package com.codeviking.koolite

/**
 * @author fabmax
 */
class KoolException(message: String, cause: Exception?) : Exception(message, cause) {
    constructor(message: String): this(message, null)
}
