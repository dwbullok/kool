package com.codeviking.kxg.util

import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.gl.GL_ALWAYS
import com.codeviking.kxg.gl.GL_POINTS
import com.codeviking.kxg.math.Vec3f
import com.codeviking.kxg.scene.Mesh
import com.codeviking.kxg.scene.MeshData
import com.codeviking.kxg.shading.*

/**
 * @author fabmax
 */

fun pointMesh(name: String? = null, block: PointMesh.() -> Unit): PointMesh {
    return PointMesh(name = name).apply(block)
}

open class PointMesh(data: MeshData = MeshData(Attribute.POSITIONS, Attribute.COLORS), name: String? = null) :
        Mesh(data, name) {
    init {
        data.primitiveType = GL_POINTS
        shader = basicPointShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    var isXray = false
    var pointSize: Float
        get() = (shader as BasicPointShader).pointSize
        set(value) { (shader as BasicPointShader).pointSize = value }

    fun addPoint(block: IndexedVertexList.Vertex.() -> Unit): Int {
        val idx =  meshData.addVertex(block)
        meshData.addIndex(idx)
        return idx
    }

    fun addPoint(position: Vec3f, color: Color): Int {
        val idx =  meshData.addVertex(position, null, color, null)
        meshData.addIndex(idx)
        return idx
    }

    fun clear() {
        meshData.clear()
    }

    override fun render(ctx: KxgContext) {
        ctx.pushAttributes()
        if (isXray) {
            ctx.depthFunc = GL_ALWAYS
        }
        ctx.applyAttributes()

        super.render(ctx)

        ctx.popAttributes()
    }
}
