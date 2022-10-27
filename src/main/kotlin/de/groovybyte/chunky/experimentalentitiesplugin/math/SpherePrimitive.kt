package de.groovybyte.chunky.experimentalentitiesplugin.math

import se.llbit.chunky.world.Material
import se.llbit.math.AABB
import se.llbit.math.QuickMath
import se.llbit.math.Ray
import se.llbit.math.Vector3
import se.llbit.math.primitive.Primitive
import kotlin.math.sqrt

/**
 * @author ShirleyNekoDev
 */
class SpherePrimitive(
    val center: Vector3,
    val radius: Double,
    val material: Material,
) : Primitive {
    private val radiusSquared = radius*radius
    private val bounds: AABB = AABB(
        center.x - radius,
        center.y - radius,
        center.z - radius,
        center.x + radius,
        center.y + radius,
        center.z + radius
    )

    override fun intersect(ray: Ray): Boolean {
        ray.t = Double.POSITIVE_INFINITY

        // origin relative to block itself
        val localOrigin = Vector3(ray.o)
        localOrigin.scaleAdd(Ray.OFFSET, ray.d)
        localOrigin.x = ray.o.x - QuickMath.floor(localOrigin.x)
        localOrigin.y = ray.o.y - QuickMath.floor(localOrigin.y)
        localOrigin.z = ray.o.z - QuickMath.floor(localOrigin.z)

        //solve for tc
        val l = Vector3(center)
        l.sub(localOrigin)
        val tca = l.dot(ray.d)
        if (tca < 0.0) {
            return false
        }

        val dSquared = l.dot(l) - tca * tca
        if (dSquared > radiusSquared) {
            return false
        }

        val thc = sqrt(radiusSquared - dSquared)
        val t0 = tca - thc
        val t1 = tca + thc

        if (t0 < 0) {
            if (t1 < 0) {
                // behind sphere
                return false
            }
            // inside sphere
            return false
        }

        ray.t = t0
        ray.distance += t0
        ray.o.scaleAdd(t0, ray.d)

        localOrigin.scaleAdd(t0, ray.d)
        val normal = Vector3(localOrigin)
        normal.sub(center)
        normal.normalize()
        ray.normal = normal

        ray.u = 0.0
        ray.v = 0.0
        material.getColor(ray)

        return true
    }

    override fun bounds(): AABB = bounds
}
