package com.codeviking.kxg.util

import com.codeviking.kxg.KxgContext

interface Disposable {
    fun dispose(ctx: KxgContext)
}
