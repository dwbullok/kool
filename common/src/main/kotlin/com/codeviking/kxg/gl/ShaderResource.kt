package com.codeviking.kxg.gl

import com.codeviking.kxg.KxgContext

class ShaderResource private constructor(glRef: Any, ctx: KxgContext) :
        GlResource(glRef, Type.SHADER, ctx) {
    companion object {
        fun createFragmentShader(ctx: KxgContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_FRAGMENT_SHADER), ctx)
        }

        fun createVertexShader(ctx: KxgContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: KxgContext) {
        glDeleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: KxgContext) {
        glShaderSource(this, source)
    }

    fun compile(ctx: KxgContext): Boolean {
        glCompileShader(this)
        return glGetShaderi(this, GL_COMPILE_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: KxgContext): String {
        return glGetShaderInfoLog(this)
    }
}