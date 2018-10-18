package com.codeviking.koolite.util

import com.codeviking.koolite.KoolContext
import com.codeviking.koolite.gl.GL_ALWAYS
import com.codeviking.koolite.gl.GL_POINTS
import com.codeviking.koolite.math.Vec3f
import com.codeviking.koolite.scene.Mesh
import com.codeviking.koolite.scene.MeshData
import com.codeviking.koolite.shading.*

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

    override fun render(ctx: KoolContext) {
        ctx.pushAttributes()
        if (isXray) {
            ctx.depthFunc = GL_ALWAYS
        }
        ctx.applyAttributes()

        super.render(ctx)

        ctx.popAttributes()
    }
}
