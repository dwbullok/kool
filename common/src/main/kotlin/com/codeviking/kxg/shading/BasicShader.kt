package com.codeviking.kxg.shading

import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.RenderPass
import com.codeviking.kxg.Texture
import com.codeviking.kxg.math.MutableVec4f
import com.codeviking.kxg.scene.Mesh
import com.codeviking.kxg.scene.Scene
import com.codeviking.kxg.scene.animation.Armature
import com.codeviking.kxg.util.Float32Buffer
import com.codeviking.kxg.util.ShadowMap


fun basicShader(propsInit: ShaderProps.() -> Unit): BasicShader {
    return BasicShader(ShaderProps().apply(propsInit))
}

/**
 * Generic simple shader generated by [GlslGenerator]
 */
open class BasicShader(val props: ShaderProps, protected val generator: GlslGenerator = GlslGenerator()) : Shader() {

    protected val uMvpMatrix = addUniform(UniformMatrix4(GlslGenerator.U_MVP_MATRIX))
    protected val uModelMatrix = addUniform(UniformMatrix4(GlslGenerator.U_MODEL_MATRIX))
    protected val uViewMatrix = addUniform(UniformMatrix4(GlslGenerator.U_VIEW_MATRIX))
    protected val uLightColor = addUniform(Uniform3f(GlslGenerator.U_LIGHT_COLOR))
    protected val uLightDirection = addUniform(Uniform3f(GlslGenerator.U_LIGHT_DIRECTION))
    protected val uCamPosition = addUniform(Uniform3f(GlslGenerator.U_CAMERA_POSITION))
    protected val uShininess = addUniform(Uniform1f(GlslGenerator.U_SHININESS))
    protected val uSpecularIntensity = addUniform(Uniform1f(GlslGenerator.U_SPECULAR_INTENSITY))
    protected val uStaticColor = addUniform(Uniform4f(GlslGenerator.U_STATIC_COLOR))
    protected val uTexture = addUniform(UniformTexture2D(GlslGenerator.U_TEXTURE_0))
    protected val uNormalMap = addUniform(UniformTexture2D(GlslGenerator.U_NORMAL_MAP_0))
    protected val uAlpha = addUniform(Uniform1f(GlslGenerator.U_ALPHA))
    protected val uSaturation = addUniform(Uniform1f(GlslGenerator.U_SATURATION))
    protected val uFogColor = addUniform(Uniform4f(GlslGenerator.U_FOG_COLOR))
    protected val uFogRange = addUniform(Uniform1f(GlslGenerator.U_FOG_RANGE))
    protected val uBones = addUniform(UniformMatrix4(GlslGenerator.U_BONES))
    protected val uShadowMvp: UniformMatrix4 = addUniform(UniformMatrix4(GlslGenerator.U_SHADOW_MVP))
    protected val uShadowTexSz: Uniform1iv = addUniform(Uniform1iv(GlslGenerator.U_SHADOW_TEX_SZ, props.shadowMap?.numMaps ?: 0))
    protected val uClipSpaceFarZ: Uniform1fv = addUniform(Uniform1fv(GlslGenerator.U_CLIP_SPACE_FAR_Z, props.shadowMap?.numMaps ?: 0))

    protected val uShadowTex = mutableListOf<UniformTexture2D>()

    var shininess: Float
        get() = uShininess.value[0]
        set(value) { uShininess.value[0] = value }
    var specularIntensity: Float
        get() = uSpecularIntensity.value[0]
        set(value) { uSpecularIntensity.value[0] = value }
    var staticColor: MutableVec4f
        get() = uStaticColor.value
        set(value) { uStaticColor.value.set(value) }
    var texture: Texture?
        get() = uTexture.value
        set(value) { uTexture.value = value }
    var normalMap: Texture?
        get() = uNormalMap.value
        set(value) { uNormalMap.value = value }
    var alpha: Float
        get() = uAlpha.value[0]
        set(value) { uAlpha.value[0] = value }
    var saturation: Float
        get() = uSaturation.value[0]
        set(value) { uSaturation.value[0] = value }
    var bones: Float32Buffer?
        get() = uBones.value
        set(value) { uBones.value = value }

    private val shadowMap: ShadowMap?
    private var scene: Scene? = null

    init {
        // set meaningful uniform default values
        shininess = props.shininess
        specularIntensity = props.specularIntensity
        staticColor.set(props.staticColor)
        texture = props.texture
        normalMap = props.normalMap
        alpha = props.alpha
        saturation = props.saturation

        shadowMap = props.shadowMap
        if (shadowMap != null) {
            uShadowMvp.value = shadowMap.shadowMvp
            for (i in 0 until shadowMap.numMaps) {
                val shadowTex = addUniform(UniformTexture2D("${GlslGenerator.U_SHADOW_TEX}_$i"))
                uShadowTex += shadowTex
                shadowTex.value = shadowMap.getShadowMap(i)
                uShadowTexSz.value[i] = shadowMap.getShadowMapSize(i)
            }
        }
    }

    override fun generate(ctx: KxgContext) {
        source = generator.generate(props, ctx)

        attributes.clear()
        attributes.add(Attribute.POSITIONS)
        attributes.add(Attribute.NORMALS)
        attributes.add(Attribute.TEXTURE_COORDS)
        attributes.add(Attribute.COLORS)
        if (props.isNormalMapped) {
            attributes.add(Attribute.TANGENTS)
        }
        if (props.numBones > 0 && ctx.glCapabilities.shaderIntAttribs) {
            attributes.add(Armature.BONE_INDICES)
            attributes.add(Armature.BONE_WEIGHTS)
        }
    }

    override fun onBind(ctx: KxgContext) {
        onMatrixUpdate(ctx)

        scene = null

        uFogColor.bind(ctx)
        uFogRange.bind(ctx)
        uSaturation.bind(ctx)
        uAlpha.bind(ctx)
        uShininess.bind(ctx)
        uSpecularIntensity.bind(ctx)
        uStaticColor.bind(ctx)
        uTexture.bind(ctx)
        uNormalMap.bind(ctx)
        uBones.bind(ctx)

        if (ctx.glCapabilities.depthTextures && shadowMap != null) {
            if (ctx.renderPass == RenderPass.SHADOW) {
                for (i in 0 until shadowMap.numMaps) {
                    uShadowTex[i].value = null
                    uShadowTex[i].bind(ctx)
                }
            } else {
                for (i in 0 until shadowMap.numMaps) {
                    uClipSpaceFarZ.value[i] = shadowMap.getClipSpaceFarZ(i)
                    uShadowTex[i].value = shadowMap.getShadowMap(i)
                    uShadowTex[i].bind(ctx)
                }
                uShadowMvp.bind(ctx)
                uShadowTexSz.bind(ctx)
                uClipSpaceFarZ.bind(ctx)
            }
        }
    }

    override fun bindMesh(mesh: Mesh, ctx: KxgContext) {
        if (scene != mesh.scene) {
            scene = mesh.scene
            if (scene != null) {
                uCamPosition.value.set(scene!!.camera.globalPos)
                uCamPosition.bind(ctx)

                val light = scene!!.light
                uLightDirection.value.set(light.direction)
                uLightDirection.bind(ctx)
                uLightColor.value.set(light.color.r, light.color.g, light.color.b)
                uLightColor.bind(ctx)
            }
        }
        super.bindMesh(mesh, ctx)
    }

    override fun onMatrixUpdate(ctx: KxgContext) {
        // pass current transformation matrices to shader
        uMvpMatrix.value = ctx.mvpState.mvpMatrixBuffer
        uMvpMatrix.bind(ctx)
        uViewMatrix.value = ctx.mvpState.viewMatrixBuffer
        uViewMatrix.bind(ctx)
        uModelMatrix.value = ctx.mvpState.modelMatrixBuffer
        uModelMatrix.bind(ctx)
    }

    override fun dispose(ctx: KxgContext) {
        super.dispose(ctx)
        texture?.dispose(ctx)
        normalMap?.dispose(ctx)
    }
}

fun basicPointShader(propsInit: ShaderProps.() -> Unit): BasicPointShader {
    return BasicPointShader(ShaderProps().apply(propsInit), GlslGenerator())
}

open class BasicPointShader internal constructor(props: ShaderProps, generator: GlslGenerator) :
        BasicShader(props, generator) {

    companion object {
        const val U_POINT_SIZE = "uPointSz"
    }

    protected val uPointSz = addUniform(Uniform1f(U_POINT_SIZE))
    var pointSize: Float
        get() = uPointSz.value[0]
        set(value) { uPointSz.value[0] = value }

    init {
        generator.customUniforms += uPointSz
        generator.injectors += object : GlslGenerator.GlslInjector {
            override fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder, ctx: KxgContext) {
                text.append("gl_PointSize = ${BasicPointShader.U_POINT_SIZE};\n")
            }
        }

        pointSize = 1f
    }

    override fun onBind(ctx: KxgContext) {
        super.onBind(ctx)
        uPointSz.bind(ctx)
    }
}
