package com.codeviking.koolite.util

import com.codeviking.koolite.KoolContext

interface Disposable {
    fun dispose(ctx: KoolContext)
}
