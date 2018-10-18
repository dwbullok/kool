package com.codeviking.koolite.scene.doubleprec

import com.codeviking.koolite.KoolContext
import com.codeviking.koolite.math.Mat4dStack
import com.codeviking.koolite.math.RayTest
import com.codeviking.koolite.scene.Node
import com.codeviking.koolite.scene.Scene

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

    override fun preRender(ctx: KoolContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.preRenderDp(ctx, modelMatDp)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.renderDp(ctx, modelMatDp)
        super.render(ctx)
    }

    override fun postRender(ctx: KoolContext) {
        root.postRender(ctx)
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        root.dispose(ctx)
        super.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        root.rayTest(test)
        super.rayTest(test)
    }

    override fun get(name: String): Node? = super.get(name) ?: root[name]
}