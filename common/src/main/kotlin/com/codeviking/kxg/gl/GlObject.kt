package com.codeviking.gdx.gl

import com.codeviking.gdx.KxgContext
import com.codeviking.gdx.util.Disposable

/**
 * @author fabmax
 */
abstract class GlObject<T: GlResource> : Disposable {
    open var res: T? = null
        protected set

    open val isValid: Boolean
        get() = res != null

    override fun dispose(ctx: KxgContext) {
        res?.delete(ctx)
        res = null
    }

}
