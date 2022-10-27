package de.groovybyte.chunky.experimentalentitiesplugin.utils

import se.llbit.chunky.renderer.scene.Camera
import se.llbit.math.Vector2
import se.llbit.math.Vector3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * @author ShirleyNekoDev
 */
fun Camera.lookAt(pos: Vector3, roll: Double = this.roll) {
    val entity2camera = Vector3(pos).also { it.sub(this.position) }
    val yaw = PI - atan2(entity2camera.z, entity2camera.x)
    val horizontalDistance = sqrt(Vector2(entity2camera.x, entity2camera.z).lengthSquared())
    val pitch = -PI / 2 - atan2(entity2camera.y, horizontalDistance)
    setView(yaw, pitch, roll)
}
