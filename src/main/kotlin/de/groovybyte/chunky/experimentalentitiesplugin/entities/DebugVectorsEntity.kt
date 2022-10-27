package de.groovybyte.chunky.experimentalentitiesplugin.entities

import de.groovybyte.chunky.experimentalentitiesplugin.utils.lookAt
import se.llbit.chunky.entity.Entity
import se.llbit.chunky.entity.Poseable
import se.llbit.chunky.renderer.scene.Camera
import se.llbit.chunky.world.Material
import se.llbit.json.*
import se.llbit.math.*
import se.llbit.math.primitive.Primitive
import kotlin.math.*

/**
 * @author ShirleyNekoDev
 */
class DebugVectorsEntity(
    position: Vector3,
    val camera: Camera,
) : Entity(position), Poseable {
    private val zQuad = object : Quad(
        // origin:
        Vector3(0.0, 0.0, 0.5),
        // x:
        Vector3(1.0, 0.0, 0.5),
        // y:
        Vector3(0.0, 1.0, 0.5),
        // uv min, uv max:
        Vector4(-1.0, 1.0, -1.0, 1.0),
        // double sided:
        true,
    ), Primitive {
        override fun bounds() = AABB.bounds(
            o,
            Vector3(o).apply { add(xv) },
            Vector3(o).apply { add(yv) }
        )
    }
    private val yQuad = object : Quad(
        // origin:
        Vector3(0.0, 0.5, 0.0),
        // x:
        Vector3(1.0, 0.5, 0.0),
        // y:
        Vector3(0.0, 0.5, 1.0),
        // uv min, uv max:
        Vector4(-1.0, 1.0, -1.0, 1.0),
        // double sided:
        true,
    ), Primitive {
        override fun bounds() = AABB.bounds(
            o,
            Vector3(o).apply { add(xv) },
            Vector3(o).apply { add(yv) }
        )
    }
    private val xQuad = object : Quad(
        // origin:
        Vector3(0.5, 0.0, 0.0),
        // x:
        Vector3(0.5, 1.0, 0.0),
        // y:
        Vector3(0.5, 0.0, 1.0),
        // uv min, uv max:
        Vector4(-1.0, 1.0, -1.0, 1.0),
        // double sided:
        true,
    ), Primitive {
        override fun bounds() = AABB.bounds(
            o,
            Vector3(o).apply { add(xv) },
            Vector3(o).apply { add(yv) }
        )
    }

    // x -> green
    val xColor = floatArrayOf(0f, 1f, 0f, 1f)
    // y -> red
    val yColor = floatArrayOf(1f, 0f, 0f, 1f)
    // z -> blue
    val zColor = floatArrayOf(0f, 0f, 1f, 1f)

    fun arrowFunction(
        u: Double,
        v: Double,
        uColor: FloatArray,
        vColor: FloatArray
    ): FloatArray {
        if (abs(u) < 0.05 && abs(v) < 0.05) {
            return floatArrayOf(1f, 1f, 1f, 1f)
        }
        if (u > 0.75) {
            if (abs(v) + (u - 0.75) < 0.25) {
                return uColor
            }
        } else if (abs(v) < 0.05) {
            return uColor
        }
        if (v > 0.75) {
            if (abs(u) + (v - 0.75) < 0.25) {
                return vColor
            }
        } else if (abs(u) < 0.05) {
            return vColor
        }
        return floatArrayOf(0f, 0f, 0f, 0f)
    }

    val zPlaneMaterial = object : Material("zPlane", null) {
        override fun getColor(ray: Ray) {
            ray.color.set(ray.u, ray.v, 0.0, 1.0)
        }

        override fun getColor(u: Double, v: Double): FloatArray =
            arrowFunction(u, v, xColor, yColor)
    }
    val yPlaneMaterial = object : Material("yPlane", null) {
        override fun getColor(ray: Ray) {
            ray.color.set(ray.u, ray.v, 0.0, 1.0)
        }

        override fun getColor(u: Double, v: Double): FloatArray =
            arrowFunction(u, v, xColor, zColor)
    }
    val xPlaneMaterial = object : Material("yPlane", null) {
        override fun getColor(ray: Ray) {
            ray.color.set(ray.u, ray.v, 0.0, 1.0)
        }

        override fun getColor(u: Double, v: Double): FloatArray =
            arrowFunction(u, v, yColor, zColor)
    }

    override fun primitives(offset: Vector3): Collection<Primitive> {
        val c = mutableListOf<Primitive>()
        val t = Transform.NONE
            .translate(-0.5, -0.5, -0.5)
            .scale(scale)
//            .rotateX(allPose.x)
//            .rotateY(allPose.y)
//            .rotateZ(allPose.z)
            .translate(position)
        zQuad.addTriangles(
            c,
            zPlaneMaterial,
            t
        )
        yQuad.addTriangles(
            c,
            yPlaneMaterial,
            t
        )
        xQuad.addTriangles(
            c,
            xPlaneMaterial,
            t
        )
        return c
    }

    override fun toJson(): JsonValue {
        // TODO
        return Json.NULL
    }

    override fun hasHead() = false

    private var scale: Double = 1.0
    override fun getScale() = scale
    override fun setScale(value: Double) {
        scale = value
    }

    override fun partNames(): Array<String> = arrayOf("all")

    private val pose: JsonObject = JsonObject().apply {
        add("all", object : JsonArray(3) {
            init {
                addAll(
                    Json.of(camera.pitch),
                    Json.of(camera.yaw),
                    Json.of(camera.roll)
                )
            }

            override fun set(i: Int, value: JsonValue) {
                if (value !is JsonNumber)
                    throw error("value must be numeric")
                super.set(i, value)
                val pitch = get(0).asDouble(camera.pitch)
                val yaw = get(1).asDouble(camera.yaw)
                val roll = get(2).asDouble(camera.roll)

                val entity2camera = Vector3(position).also { it.sub(camera.position) }
                val distanceToCamera = entity2camera.length()

                val newCameraPosition = Vector3(
                    position.x + cos(yaw) * distanceToCamera * sin(pitch),
                    position.y + cos(pitch) * distanceToCamera,
                    position.z + sin(yaw) * distanceToCamera * sin(pitch),
                )
                camera.position.set(newCameraPosition)
                camera.lookAt(position, roll)
            }
        })
    }

    override fun getPose() = pose
}
