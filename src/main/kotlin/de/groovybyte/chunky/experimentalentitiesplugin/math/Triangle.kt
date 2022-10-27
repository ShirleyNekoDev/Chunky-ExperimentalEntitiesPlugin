package de.groovybyte.chunky.experimentalentitiesplugin.math

import se.llbit.math.Transform
import se.llbit.math.Vector3

/**
 * @author ShirleyNekoDev
 */
data class Triangle(
    val c0: Vector3,
    val c1: Vector3,
    val c2: Vector3
) {

    fun scaled(scale: Vector3, copy: Boolean = true): Triangle =
        (if(copy) copy() else this).apply {
            c0.scale(scale)
            c1.scale(scale)
            c2.scale(scale)
        }

    fun transformed(transform: Transform, copy: Boolean = true): Triangle =
        (if(copy) copy() else this).apply {
            transform.apply(c0)
            transform.apply(c1)
            transform.apply(c2)
        }
}
