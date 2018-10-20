package com.codeviking.gdx.scene.doubleprec

import com.codeviking.gdx.KxgContext
import com.codeviking.gdx.math.Mat4dStack
import com.codeviking.gdx.math.RayTest
import com.codeviking.gdx.scene.Node
import com.codeviking.gdx.scene.Scene

fun doublePrecisionTransform(name: String? = null, block: TransformGroupDp.() -> Unit): DoublePrecisionRoot<TransformGroupDp> {
    val root = DoublePrecisionRoot(TransformGroupDp("${name ?: "DoublePrecisionRoot"}-rootGroup"), name)
    root.root.block()
    return root
}

class DoublePrecisionRoot<T: NodeDp>(val root: T, name: String? = null) : Node(name) {

    private val modelMatDp = Mat4dStack()

    init {
        modelMatDp.setIdentity()
        root.parent = this
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        root.scene = newScene
    }

    override fun preRender(ctx: KxgContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.preRenderDp(ctx, modelMatDp)
        super.preRender(ctx)
    }

    override fun render(ctx: KxgContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.renderDp(ctx, modelMatDp)
        super.render(ctx)
    }

    override fun postRender(ctx: KxgContext) {
        root.postRender(ctx)
        super.postRender(ctx)
    }

    override fun dispose(ctx: KxgContext) {
        root.dispose(ctx)
        super.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        root.rayTest(test)
        super.rayTest(test)
    }

    override fun get(name: String): Node? = super.get(name) ?: root[name]
}