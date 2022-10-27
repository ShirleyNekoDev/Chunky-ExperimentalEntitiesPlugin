package de.groovybyte.chunky.experimentalentitiesplugin.entities

import de.groovybyte.chunky.experimentalentitiesplugin.math.SpherePrimitive
import de.groovybyte.chunky.experimentalentitiesplugin.materials.FlatColorMaterial
import se.llbit.chunky.entity.Entity
import se.llbit.chunky.world.Material
import se.llbit.json.Json
import se.llbit.json.JsonValue
import se.llbit.math.Vector3
import se.llbit.math.primitive.Primitive

/**
 * @author ShirleyNekoDev
 */
class SphereEntity(
    position: Vector3,
    radius: Double = 1.0,
    material: Material = FlatColorMaterial(1f, 1f, 1f, 1f)
) : Entity(position) {
    init {
        println(position)
    }

    val primitive: Primitive = SpherePrimitive(position, radius, material)

    override fun primitives(offset: Vector3): List<Primitive> = listOf(primitive)

    override fun toJson(): JsonValue = Json.NULL
}
