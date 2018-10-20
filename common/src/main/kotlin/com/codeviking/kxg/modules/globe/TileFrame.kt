package com.codeviking.gdx.modules.globe

import com.codeviking.gdx.math.Mat4d
import com.codeviking.gdx.math.Vec3d
import com.codeviking.gdx.scene.Group
import com.codeviking.gdx.scene.doubleprec.TransformGroupDp

class TileFrame(val tileName: TileName, private val globe: Globe) : TransformGroupDp() {

    val transformToLocal: Mat4d get() = invTransform
    val transformToGlobal: Mat4d get() = transform

    val zoomGroups = mutableListOf<Group>()
    var tileCount = 0
        private set

    init {
        rotate(tileName.lonCenter, Vec3d.Y_AXIS)
        rotate(90.0 - tileName.latCenter, Vec3d.X_AXIS)
        translate(0.0, globe.radius, 0.0)
        checkInverse()

        for (i in tileName.zoom..globe.maxZoomLvl) {
            val grp = Group()
            zoomGroups += grp
            +grp
        }
    }

    fun addTile(tile: TileMesh) {
        getZoomGroup(tile.tileName.zoom) += tile
        tileCount++
    }


    fun removeTile(tile: TileMesh) {
        getZoomGroup(tile.tileName.zoom) -= tile
        tileCount--
    }

    private fun getZoomGroup(level: Int) = zoomGroups[level - tileName.zoom]

}