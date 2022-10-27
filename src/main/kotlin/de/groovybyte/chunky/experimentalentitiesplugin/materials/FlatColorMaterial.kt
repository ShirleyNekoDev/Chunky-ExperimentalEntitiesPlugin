package de.groovybyte.chunky.experimentalentitiesplugin.materials

import se.llbit.chunky.world.Material
import se.llbit.math.Ray

/**
 * @author ShirleyNekoDev
 */
class FlatColorMaterial(
    r: Float,
    g: Float,
    b: Float,
    opacity: Float,
) : Material("FlatColorMaterial", null) {
    val color = floatArrayOf(r, g, b, opacity)

    override fun getColor(ray: Ray) {
        ray.color.set(color)
    }

    override fun getColor(u: Double, v: Double): FloatArray {
        return color
    }
}
