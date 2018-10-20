package com.codeviking.gdx.scene

import com.codeviking.gdx.math.MutableVec3f
import com.codeviking.gdx.util.Color
import com.codeviking.gdx.util.MutableColor

/**
 * @author fabmax
 */
class Light {

    val direction = MutableVec3f(1f, 1f, 1f)

    val color = MutableColor(Color.WHITE)

}
