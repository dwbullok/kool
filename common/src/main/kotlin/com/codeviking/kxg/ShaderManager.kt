package com.codeviking.kxg

import com.codeviking.kxg.gl.ProgramResource
import com.codeviking.kxg.gl.ShaderResource
import com.codeviking.kxg.gl.glUseProgram
import com.codeviking.kxg.shading.PreferredLightModel
import com.codeviking.kxg.shading.PreferredShadowMethod
import com.codeviking.kxg.shading.Shader
import com.codeviking.kxg.shading.ShadingHints
import com.codeviking.kxg.util.logE

/**
 * @author fabmax
 */
class ShaderManager internal constructor() : SharedResManager<Shader.Source, ProgramResource>() {

    var boundShader: Shader? = null
        private set

    var shadingHints = ShadingHints(PreferredLightModel.PHONG, PreferredShadowMethod.NO_SHADOW)
        // TODO: set(value) { updateAllShaders() }

    fun onNewFrame(ctx: KxgContext) {
        // force re-binding shader on new frame, otherwise delayed loaded
        // resources (e.g. textures) might not be loaded at all
        bindShader(null, ctx)
    }

    fun bindShader(shader: Shader?, ctx: KxgContext) {
        if (shader != null) {
            if (!shader.isValid) {
                shader.onLoad(ctx)
            }
            if (!shader.isBound(ctx)) {
                if (shader.res?.glRef != boundShader?.res?.glRef) {
                    glUseProgram(shader.res)
                }
                boundShader = shader
                shader.onBind(ctx)
            }

        } else if (boundShader != null) {
            // clear used shader
            glUseProgram(null)
            boundShader = null
        }
    }

    internal fun createShader(source: Shader.Source, ctx: KxgContext): ProgramResource {
        return addReference(source, ctx)
    }

    internal fun deleteShader(shader: Shader, ctx: KxgContext) {
        val res = shader.res
        if (res != null) {
            removeReference(shader.source, ctx)
        }
    }

    override fun createResource(key: Shader.Source, ctx: KxgContext): ProgramResource {
        // create vertex shader
        val vertShader = ShaderResource.createVertexShader(ctx)
        vertShader.shaderSource(key.vertexSrc, ctx)
        if (!vertShader.compile(ctx)) {
            // compilation failed
            val log = vertShader.getInfoLog(ctx)
            vertShader.delete(ctx)
            logE { "Vertex shader compilation failed: $log" }
            logE { "Shader source: \n${key.vertexSrc}" }
            throw KxgException("Vertex shader compilation failed: $log")
        }

        // create fragment shader
        val fragShader = ShaderResource.createFragmentShader(ctx)
        fragShader.shaderSource(key.fragmentSrc, ctx)
        if (!fragShader.compile(ctx)) {
            // compilation failed
            val log = fragShader.getInfoLog(ctx)
            fragShader.delete(ctx)
            logE { "Fragment shader compilation failed: $log" }
            logE { "Shader source: \n${key.fragmentSrc}" }
            throw KxgException("Fragment shader compilation failed: $log")
        }

        // link shader
        val prog = ProgramResource.create(ctx)
        prog.attachShader(vertShader, ctx)
        prog.attachShader(fragShader, ctx)
        val success = prog.link(ctx)
        // after linkage fragment and vertex shader are no longer needed
        vertShader.delete(ctx)
        fragShader.delete(ctx)
        if (!success) {
            // linkage failed
            val log = prog.getInfoLog(ctx)
            prog.delete(ctx)
            logE { "Shader linkage failed: $log" }
            throw KxgException("Shader linkage failed: $log")
        }

        return prog
    }

    override fun deleteResource(key: Shader.Source, res: ProgramResource, ctx: KxgContext) {
        res.delete(ctx)
    }
}
