package com.codeviking.koolite.scene

import com.codeviking.koolite.math.MutableVec3f
import com.codeviking.koolite.util.Color
import com.codeviking.koolite.util.MutableColor

/**
 * @author fabmax
 */
class Light {

    val direction = MutableVec3f(1f, 1f, 1f)

    val color = MutableColor(Color.WHITE)

}
