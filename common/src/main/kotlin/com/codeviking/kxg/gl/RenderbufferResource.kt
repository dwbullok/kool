package com.codeviking.gdx.gl

import com.codeviking.gdx.KxgContext

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