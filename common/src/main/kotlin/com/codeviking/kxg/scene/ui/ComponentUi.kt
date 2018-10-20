package com.codeviking.kxg.scene.ui

import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.scene.Mesh
import com.codeviking.kxg.scene.MeshData
import com.codeviking.kxg.shading.*
import com.codeviking.kxg.util.Color
import com.codeviking.kxg.util.Disposable
import com.codeviking.kxg.util.MeshBuilder

interface ComponentUi : Disposable {

    fun updateComponentAlpha() { }

    fun createUi(ctx: KxgContext) { }

    fun updateUi(ctx: KxgContext) { }

    fun onRender(ctx: KxgContext) { }

    override fun dispose(ctx: KxgContext) { }
}

open class BlankComponentUi : ComponentUi

open class SimpleComponentUi(val component: UiComponent) : ComponentUi {

    protected var shader: BasicShader? = null
    protected val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    val color: ThemeOrCustomProp<Color> = ThemeOrCustomProp(Color.BLACK.withAlpha(0.5f))

    override fun updateComponentAlpha() {
        shader?.alpha = component.alpha
    }

    override fun createUi(ctx: KxgContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader = createShader(ctx)
        shader?.staticColor?.set(color.prop)
        mesh.shader = shader
        component.addNode(mesh, 0)
    }

    override fun dispose(ctx: KxgContext) {
        component -= mesh
        mesh.dispose(ctx)
    }

    override fun updateUi(ctx: KxgContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader?.staticColor?.set(color.prop)

        component.setupBuilder(meshBuilder)
        meshBuilder.color = color.prop
        meshBuilder.rect {
            size.set(component.width, component.height)
            fullTexCoords()
        }
    }

    protected open fun createShader(ctx: KxgContext): BasicShader {
        return basicShader {
            lightModel = component.root.shaderLightModel
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }
    }
}

open class BlurredComponentUi(component: UiComponent) : SimpleComponentUi(component) {
    override fun createShader(ctx: KxgContext): BasicShader {
        return blurShader {
            lightModel = component.root.shaderLightModel
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }.apply {
            blurHelper = component.root.createBlurHelper()
        }
    }

    override fun updateUi(ctx: KxgContext) {
        super.updateUi(ctx)
        val bs = shader
        if (bs is BlurShader) {
            bs.colorMix = bs.staticColor.w
            bs.staticColor.w = 1f
        }
    }
}