package com.codeviking.gdx.modules.globe

import com.codeviking.gdx.KxgContext
import com.codeviking.gdx.Texture
import com.codeviking.gdx.assetTexture
import com.codeviking.gdx.shading.ColorModel
import com.codeviking.gdx.shading.LightModel
import com.codeviking.gdx.shading.Shader
import com.codeviking.gdx.shading.basicShader

interface TileShaderProvider {
    fun getShader(tileName: TileName, ctx: KxgContext): TileShader
}

class TileShader(val shader: Shader, val attribution: TileMesh.AttributionInfo)

abstract class TexImageTileShaderProvider : TileShaderProvider {
    override fun getShader(tileName: TileName, ctx: KxgContext): TileShader{
        val shader = basicShader {
            colorModel = ColorModel.TEXTURE_COLOR
            lightModel = LightModel.PHONG_LIGHTING

            specularIntensity = 0.25f
            shininess = 25f
            texture = getTexture(tileName, ctx)
        }
        return TileShader(shader, getAttributionInfo(tileName))
    }

    abstract fun getAttributionInfo(tileName: TileName): TileMesh.AttributionInfo

    abstract fun getTexture(tileName: TileName, ctx: KxgContext): Texture
}

open class OsmTexImageTileShaderProvider : TexImageTileShaderProvider() {
    protected val tileUrls = mutableListOf("a.tile.openstreetmap.org", "b.tile.openstreetmap.org", "c.tile.openstreetmap.org")

    override fun getTexture(tileName: TileName, ctx: KxgContext): Texture {
        val srvIdx = (tileName.x xor tileName.y xor tileName.zoom) % tileUrls.size
        return assetTexture("https://${tileUrls[srvIdx]}/${tileName.zoom}/${tileName.x}/${tileName.y}.png", ctx)
    }

    override fun getAttributionInfo(tileName: TileName): TileMesh.AttributionInfo =
            TileMesh.AttributionInfo("Imagery: © OpenStreetMap", "http://www.openstreetmap.org/copyright")
}
