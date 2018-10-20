package com.codeviking.gdx.gl

import com.codeviking.gdx.KxgContext
import com.codeviking.gdx.util.Float32Buffer
import com.codeviking.gdx.util.Uint16Buffer
import com.codeviking.gdx.util.Uint32Buffer
import com.codeviking.gdx.util.Uint8Buffer

class BufferResource private constructor(glRef: Any, val target: Int, ctx: KxgContext) :
        GlResource(glRef, Type.BUFFER, ctx) {

    // target is used as a map key, avoid auto-boxing it into an object int every time it is used...
    private val targetKeyAsObject: Int? = target

    companion object {
        fun create(target: Int, ctx: KxgContext): BufferResource {
            return BufferResource(glCreateBuffer(), target, ctx)
        }
    }

    override fun delete(ctx: KxgContext) {
        glDeleteBuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: KxgContext) {
        if (ctx.boundBuffers[targetKeyAsObject!!] != this) {
            glBindBuffer(target, this)
            ctx.boundBuffers[targetKeyAsObject] = this
        }
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: KxgContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: KxgContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: KxgContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: KxgContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind(ctx: KxgContext) {
        glBindBuffer(target, null)
        ctx.boundBuffers[target] = null
    }
}