package com.codeviking.kxg.scene

import com.codeviking.kxg.math.MutableVec3f
import com.codeviking.kxg.util.Color
import com.codeviking.kxg.util.MutableColor

/**
 * @author fabmax
 */
class Light {

    val direction = MutableVec3f(1f, 1f, 1f)

    val color = MutableColor(Color.WHITE)

}
