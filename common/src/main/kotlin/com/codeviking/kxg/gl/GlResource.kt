package com.codeviking.gdx.gl

import com.codeviking.gdx.KxgContext

/**
 * @author fabmax
 */
abstract class GlResource constructor(glRef: Any, val type: Type, ctx: KxgContext) {
    enum class Type {
        BUFFER,
        FRAMEBUFFER,
        PROGRAM,
        RENDERBUFFER,
        SHADER,
        TEXTURE
    }

    var glRef: Any? = glRef
        protected set

    val isValid: Boolean
        get() = glRef != null

    init {
        @Suppress("LeakingThis")
        ctx.memoryMgr.memoryAllocated(this, 0)
    }

    open fun delete(ctx: KxgContext) {
        ctx.memoryMgr.deleted(this)
        glRef = null
    }
}
