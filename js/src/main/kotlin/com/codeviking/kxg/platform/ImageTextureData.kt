package com.codeviking.gdx.platform

import com.codeviking.gdx.JsImpl
import com.codeviking.gdx.KxgContext
import com.codeviking.gdx.Texture
import com.codeviking.gdx.TextureData
import com.codeviking.gdx.gl.GL_RGBA
import com.codeviking.gdx.gl.GL_TEXTURE_2D
import com.codeviking.gdx.gl.GL_UNSIGNED_BYTE
import org.w3c.dom.HTMLImageElement

class ImageTextureData(val image: HTMLImageElement) : TextureData() {
    override var isAvailable: Boolean
        get() = image.complete
        set(value) {}

    override fun onLoad(texture: Texture, ctx: KxgContext) {
        // fixme: is there a way to find out if the image has an alpha channel and set the GL format accordingly?
        JsImpl.gl.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, image)
        width = image.width
        height = image.height
        val size = width * height * 4
        ctx.memoryMgr.memoryAllocated(texture.res!!, size)
    }
}