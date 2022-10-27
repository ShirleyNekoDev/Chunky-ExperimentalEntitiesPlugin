package de.groovybyte.chunky.experimentalentitiesplugin.entities

import de.groovybyte.chunky.experimentalentitiesplugin.math.Triangle
import se.llbit.chunky.entity.Entity
import se.llbit.chunky.world.Material
import se.llbit.json.Json
import se.llbit.json.JsonValue
import se.llbit.math.Ray
import se.llbit.math.Transform
import se.llbit.math.Vector2
import se.llbit.math.Vector3
import se.llbit.math.primitive.Primitive
import se.llbit.math.primitive.TexturedTriangle

/**
 * @author ShirleyNekoDev
 */
open class PrimitiveTrianglesEntity(
    position: Vector3 = Vector3(),
    val rotation: Vector3 = Vector3(0.0, 0.0, 0.0), // pitch, yaw, roll
    val scale: Vector3 = Vector3(1.0, 1.0, 1.0), // width, height, depth
    val triangles: List<Triangle>,
    val material: Material = object : Material("DebugUVTexture", null) {
        override fun getColor(ray: Ray) {
            ray.color.set(ray.u, ray.v, 0.0, 1.0)
        }

        override fun getColor(u: Double, v: Double): FloatArray {
            return floatArrayOf(u.toFloat(), v.toFloat(), 0f, 1f)
        }
    }
) : Entity(position) {
    constructor(
        position: Vector3,
        scale: Double,
        triangles: List<Triangle>,
    ) : this(
        position = position,
        scale = Vector3(scale, scale, scale),
        triangles = triangles,
    )

    override fun primitives(offset: Vector3): Collection<Primitive> {
        val transform = Transform.NONE
            .rotateX(rotation.x)
            .rotateY(rotation.y)
            .rotateZ(rotation.z)
            .translate(
                position.x + offset.x,
                position.y + offset.y,
                position.z + offset.z
            )
        return triangles.map { triangle ->
            val (c0, c1, c2) = triangle
                .scaled(scale)
                .transformed(transform, copy = false)
            TexturedTriangle(
                c0, c2, c1,
                Vector2(0.0, 0.0), Vector2(0.0, 1.0), Vector2(1.0, 0.0),
                material,
                false
            )
        }
    }

    override fun toJson(): JsonValue {
        // TODO: save in scene without wasting to much space
        return Json.NULL
    }

}
