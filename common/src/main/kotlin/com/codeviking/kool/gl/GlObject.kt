package com.codeviking.koolite.gl

import com.codeviking.koolite.KoolContext
import com.codeviking.koolite.util.Disposable

/**
 * @author fabmax
 */
abstract class GlObject<T: GlResource> : Disposable {
    open var res: T? = null
        protected set

    open val isValid: Boolean
        get() = res != null

    override fun dispose(ctx: KoolContext) {
        res?.delete(ctx)
        res = null
    }

}
