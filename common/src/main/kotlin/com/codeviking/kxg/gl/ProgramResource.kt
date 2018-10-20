package com.codeviking.kxg.gl

import com.codeviking.kxg.KxgContext

class ProgramResource private constructor(glRef: Any, ctx: KxgContext) : GlResource(glRef, Type.PROGRAM, ctx) {
    companion object {
        fun create(ctx: KxgContext): ProgramResource {
            return ProgramResource(glCreateProgram(), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: KxgContext) {
        glDeleteProgram(this)
        super.delete(ctx)
    }

    fun attachShader(shader: ShaderResource, ctx: KxgContext) {
        glAttachShader(this, shader)
    }

    fun link(ctx: KxgContext): Boolean {
        glLinkProgram(this)
        return glGetProgrami(this, GL_LINK_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: KxgContext): String {
        return glGetProgramInfoLog(this)
    }
}