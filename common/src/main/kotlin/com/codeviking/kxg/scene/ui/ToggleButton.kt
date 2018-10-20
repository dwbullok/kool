package com.codeviking.kxg.scene.ui

import com.codeviking.kxg.InputManager
import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.math.RayTest
import com.codeviking.kxg.util.Color
import com.codeviking.kxg.util.CosAnimator
import com.codeviking.kxg.util.InterpolatedFloat
import com.codeviking.kxg.util.MutableColor

/**
 * @author fabmax
 */

open class ToggleButton(name: String, root: UiRoot, initState: Boolean = false): Button(name, root) {

    val onStateChange: MutableList<ToggleButton.() -> Unit> = mutableListOf()

    var knobColorOn = Color.WHITE
    var knobColorOff = Color.LIGHT_GRAY
    var trackColor = Color.GRAY

    var isEnabled = initState
        set(value) {
            if (value != field) {
                field = value
                fireStateChanged()
            }
        }

    init {
        textAlignment = Gravity(Alignment.START, Alignment.CENTER)
    }

    protected fun fireStateChanged() {
        for (i in onStateChange.indices) {
            onStateChange[i]()
        }
    }

    override fun fireOnClick(ptr: InputManager.Pointer, rt: RayTest, ctx: KxgContext) {
        isEnabled = !isEnabled
        super.fireOnClick(ptr, rt, ctx)
    }

    override fun setThemeProps(ctx: KxgContext) {
        super.setThemeProps(ctx)
        knobColorOn = root.theme.accentColor
    }

    override fun createThemeUi(ctx: KxgContext): ComponentUi {
        return root.theme.newToggleButtonUi(this)
    }
}

open class ToggleButtonUi(val tb: ToggleButton, baseUi: ComponentUi) : ButtonUi(tb, baseUi) {

    protected val knobAnimator = CosAnimator(InterpolatedFloat(0f, 1f))
    protected val knobColor = MutableColor()

    protected val stateChangedListener: ToggleButton.() -> Unit = {
        if (isEnabled) {
            // animate knob from left to right
            knobAnimator.speed = 1f
        } else {
            // animate knob from right to left
            knobAnimator.speed = -1f
        }
    }

    override fun createUi(ctx: KxgContext) {
        super.createUi(ctx)

        knobAnimator.speed = 0f
        knobAnimator.duration = 0.15f
        knobAnimator.value.value = if (tb.isEnabled) { 1f } else { 0f }
        knobAnimator.value.onUpdate = { tb.requestUiUpdate() }

        tb.onStateChange += stateChangedListener
    }

    override fun dispose(ctx: KxgContext) {
        super.dispose(ctx)
        tb.onStateChange -= stateChangedListener
    }

    override fun updateUi(ctx: KxgContext) {
        super.updateUi(ctx)

        val paddingR = tb.padding.right.toUnits(tb.width, tb.dpi)
        val trackW = tb.dp(24f)
        val trackH = tb.dp(6f)
        val knobR = tb.dp(10f)
        val x = tb.width - paddingR - trackW - knobR
        val y = (tb.height - trackH) / 2f

        meshBuilder.color = tb.trackColor
        meshBuilder.rect {
            origin.set(x, y, tb.dp(4f))
            size.set(trackW, trackH)
            cornerRadius = trackH / 2f
            cornerSteps = 4
        }

        val anim = knobAnimator.value.value

        knobColor.clear()
        knobColor.add(tb.knobColorOff, 1f - anim)
        knobColor.add(tb.knobColorOn, anim)
        meshBuilder.color = knobColor
        meshBuilder.circle {
            center.set(x + trackW * anim, y + trackH / 2f, tb.dp(6f))
            radius = knobR
            steps = 30
        }
    }

    override fun onRender(ctx: KxgContext) {
        super.onRender(ctx)
        knobAnimator.tick(ctx)
    }
}
