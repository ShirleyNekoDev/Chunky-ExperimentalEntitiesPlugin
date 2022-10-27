package de.groovybyte.chunky.experimentalentitiesplugin.math

import se.llbit.math.Transform
import se.llbit.math.Vector3
import kotlin.math.max
import kotlin.math.min

/**
 * @author ShirleyNekoDev
 */
operator fun Vector3.component1(): Double = x
operator fun Vector3.component2(): Double = y
operator fun Vector3.component3(): Double = z
operator fun Vector3.set(dimension: Int, value: Float) = value.toDouble().also {
    when(dimension) {
        0 -> x = it
        1 -> y = it
        2 -> z = it
        else -> throw IndexOutOfBoundsException()
    }
}

/**
 * scales a vector's components with the components of another vector
 */
fun Vector3.scale(vec: Vector3) {
    x *= vec.x
    y *= vec.y
    z *= vec.z
}

/**
 * copies each vector for modifications
 */
fun List<Vector3>.normalizedVertices(
    rotation: Vector3 = Vector3(),
    targetSize: Vector3 = Vector3(Double.NaN, 1.0, Double.NaN),
): List<Vector3> = normalizeVertices(
    map { Vector3(it) },
    rotation,
    targetSize
)

/**
 * overwrites values in vertices!!!
 */
fun normalizeVertices(
    vertices: List<Vector3>,
    rotation: Vector3 = Vector3(), // pitch, yaw, roll
    targetSize: Vector3 = Vector3(
        Double.NaN, // width, NaN - check height
        1.0, // height
        Double.NaN, // depth, NaN - check height
    ),
): List<Vector3> {
    val transform = Transform.NONE
        .rotateX(rotation.x)
        .rotateY(rotation.y)
        .rotateZ(rotation.z)
    vertices.forEach {
        transform.apply(it)
    }

    val min =
        Vector3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    val max =
        Vector3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

    fun map(
        value: Double,
        oldMin: Double,
        oldMax: Double,
        newMin: Double,
        newMax: Double
    ): Double {
        val oldSpan = oldMax - oldMin
        val newSpan = newMax - newMin
        return (value - oldMin) / oldSpan * newSpan + newMin
    }
    vertices.forEach {
        min.x = min(min.x, it.x)
        min.y = min(min.y, it.y)
        min.z = min(min.z, it.z)
        max.x = max(max.x, it.x)
        max.y = max(max.y, it.y)
        max.z = max(max.z, it.z)
    }
    val span = Vector3().apply { sub(max, min) }


    // target normalization: height is between 0 and 1
    val xTarget = if (targetSize.x.isNaN()) {
        map(span.x, 0.0, span.y, 0.0, targetSize.y)
    } else {
        targetSize.x
    }
    val zTarget = if (targetSize.z.isNaN()) {
        map(span.z, 0.0, span.y, 0.0, targetSize.y)
    } else {
        targetSize.z
    }
    vertices.forEach {
        it.x = map(it.x, min.x, max.x, -xTarget / 2, xTarget / 2)
        it.y = map(it.y, min.y, max.y, 0.0, targetSize.y)
        it.z = map(it.z, min.z, max.z, -zTarget / 2, zTarget / 2)
    }
    return vertices
}
