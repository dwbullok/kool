package com.codeviking.kxg

import com.codeviking.kxg.gl.*
import com.codeviking.kxg.util.Uint8Buffer

/**
 * @author fabmax
 */

fun defaultProps(id: String): TextureProps {
    return TextureProps(id, GL_LINEAR, GL_CLAMP_TO_EDGE)
}

data class TextureProps(
        val id: String,
        val minFilter: Int,
        val magFilter: Int,
        val xWrapping: Int,
        val yWrapping: Int,
        val anisotropy: Int,
        val target: Int = GL_TEXTURE_2D) {

    constructor(id: String, filter: Int, wrapping: Int) :
            this(id, minFilter(filter), magFilter(filter), wrapping, wrapping, 16)

    constructor(id: String, filter: Int, wrapping: Int, anisotropy: Int) :
            this(id, minFilter(filter), magFilter(filter), wrapping, wrapping, anisotropy)

    companion object {
        val DEFAULT_MIN = GL_LINEAR_MIPMAP_LINEAR
        val DEFAULT_MAG = GL_LINEAR
        val DEFAULT_X_WRAP = GL_CLAMP_TO_EDGE
        val DEFAULT_Y_WRAP = GL_CLAMP_TO_EDGE

        private fun magFilter(filter: Int) = when (filter) {
            GL_NEAREST -> GL_NEAREST
            else -> DEFAULT_MAG
        }

        private fun minFilter(filter: Int) = when (filter) {
            GL_NEAREST -> GL_NEAREST
            else -> DEFAULT_MIN
        }
    }
}

abstract class TextureData {
    open var isAvailable = false
        protected set

    var width = 0
        protected set
    var height = 0
        protected set

    internal fun loadData(texture: Texture, ctx: KxgContext) {
        onLoad(texture, ctx)
        texture.res!!.isLoaded = true
        if (texture.props.minFilter == GL_LINEAR_MIPMAP_LINEAR) {
            glGenerateMipmap(texture.res!!.target)
        }
    }

    abstract fun onLoad(texture: Texture, ctx: KxgContext)
}

class BufferedTextureData(val buffer: Uint8Buffer, width: Int, height: Int, val format: Int) : TextureData() {
    init {
        this.isAvailable = true
        this.width = width
        this.height = height
    }

    override fun onLoad(texture: Texture, ctx: KxgContext) {
        val res = texture.res ?: throw KxgException("Texture wasn't created")
        val limit = buffer.limit
        val pos = buffer.position
        buffer.flip()
        glTexImage2D(res.target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer)
        buffer.limit = limit
        buffer.position = pos
        ctx.memoryMgr.memoryAllocated(res, buffer.position)
    }
}

open class Texture(val props: TextureProps, val generator: Texture.(ctx: KxgContext) -> TextureData) :
        GlObject<TextureResource>() {

    var width = 0
        protected set
    var height = 0
        protected set

    var delayLoading = false

    internal fun onCreate(ctx: KxgContext) {
        res = ctx.textureMgr.createTexture(props, ctx)
    }

    override fun dispose(ctx: KxgContext) {
        // do not call super, as this will immediately delete the texture on the GPU. However, texture resource is
        // shared and might be used by other Texture objects...
        if (isValid) {
            ctx.textureMgr.deleteTexture(this, ctx)
            res = null
        }
    }

    internal fun loadData(texData: TextureData, ctx: KxgContext) {
        if (!texData.isAvailable) {
            throw KxgException("Texture data is not available")
        }
        width = texData.width
        height = texData.height
        texData.loadData(this, ctx)
    }
}
