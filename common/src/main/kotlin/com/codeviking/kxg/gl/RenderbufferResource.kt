package com.codeviking.kxg.gl

import com.codeviking.kxg.KxgContext

class RenderbufferResource private constructor(glRef: Any, ctx: KxgContext) :
        GlResource(glRef, Type.RENDERBUFFER, ctx) {
    companion object {
        fun create(ctx: KxgContext): RenderbufferResource {
            return RenderbufferResource(glCreateRenderbuffer(), ctx)
        }
    }

    override fun delete(ctx: KxgContext) {
        glDeleteRenderbuffer(this)
        super.delete(ctx)
    }
}