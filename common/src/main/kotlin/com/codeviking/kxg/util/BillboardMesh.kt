package com.codeviking.kxg.util

import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.math.Vec2f
import com.codeviking.kxg.math.Vec3f
import com.codeviking.kxg.scene.Mesh
import com.codeviking.kxg.scene.MeshData
import com.codeviking.kxg.shading.*

/**
 * @author fabmax
 */
class BillboardMesh(data: MeshData = MeshData(Attribute.POSITIONS, Attribute.COLORS, Attribute.TEXTURE_COORDS),
                    name: String = "") : Mesh(data, name) {

    init {
        shader = billboardShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    private var builder = MeshBuilder(data)

    var billboardSize: Float
        get() = (shader as BillboardShader).billboardSize
        set(value) { (shader as BillboardShader).billboardSize = value }

    fun addQuad(centerPosition: Vec3f, color: Color) {
        builder.color = color
        builder.rect {
            fullTexCoords()
            origin.set(centerPosition)
            // all vertices of the quad have the same position, actual coords are computed in vertex shader
            size.set(Vec2f.ZERO)
        }
    }

}

fun billboardShader(propsInit: ShaderProps.() -> Unit = { }): BillboardShader {
    val props = ShaderProps()
    props.propsInit()
    props.isTextureColor = true
    return BillboardShader(props, GlslGenerator())
}

class BillboardShader internal constructor(props: ShaderProps, generator: GlslGenerator) : BasicShader(props, generator) {

    private val uViewportSz = addUniform(Uniform2f("uViewportSz"))

    var billboardSize = 1f

    init {
        generator.customUniforms += uViewportSz
        generator.injectors += object: GlslGenerator.GlslInjector {
            override fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder, ctx: KxgContext) {
                text.append("gl_Position.x += (${Attribute.TEXTURE_COORDS.name}.x - 0.5) * gl_Position.w / uViewportSz.x;\n")
                        .append("gl_Position.y -= (${Attribute.TEXTURE_COORDS.name}.y - 0.5) * gl_Position.w / uViewportSz.y;\n")
            }
        }
    }

    override fun onBind(ctx: KxgContext) {
        super.onBind(ctx)
        uViewportSz.value.set(0.5f * ctx.viewport.width.toFloat() / billboardSize,
                0.5f * ctx.viewport.height.toFloat() / billboardSize)
        uViewportSz.bind(ctx)
    }
}
