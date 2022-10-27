package de.groovybyte.chunky.experimentalentitiesplugin.entities

import de.groovybyte.chunky.experimentalentitiesplugin.entities.EntityRegistry.EntityInstantiator.Companion.SPAWN_IN_FRONT_OF_CAMERA
import de.groovybyte.chunky.experimentalentitiesplugin.math.Triangle
import se.llbit.chunky.entity.Entity
import se.llbit.chunky.renderer.scene.Scene
import se.llbit.math.Ray

/**
 * @author ShirleyNekoDev
 */
object EntityRegistry {
    data class EntityInstantiator(
        val entityName: String,
        val positionizer: Positionizer = SPAWN_AT_TARGET_INTERSECTION,
        val instantiator: Scene.(cameraSceneIntersectionRay: Ray, scale: Double) -> Entity,
    ) {
        companion object {
            fun interface Positionizer {
                fun target(scene: Scene): Ray
            }

            val SPAWN_IN_FRONT_OF_CAMERA: Positionizer = Positionizer { scene ->
                val ray = Ray()
                scene.camera().getTargetDirection(ray)
                ray.orientNormal(ray.d)
                ray.t = 1.0
                ray.distance += ray.t
                ray.o.scaleAdd(ray.t, ray.d)
                ray.o.sub(scene.origin)
                ray
            }
            val SPAWN_AT_TARGET_INTERSECTION: Positionizer = Positionizer { scene ->
                val ray = Ray()
                if (scene.traceTarget(ray)) {
                    ray.o.add(scene.origin)
                } else {
                    ray.distance = Double.POSITIVE_INFINITY
                }
                ray
            }
        }

        fun spawn(scene: Scene, scale: Double) {
            scene.apply {
                val ray = positionizer.target(scene)
                if (ray.distance.isFinite())
                    actors.add(instantiator(ray, scale))
                rebuildActorBvh()
            }
        }
    }

    private val utahTeapot: List<Triangle> by lazy {
        loadVerticesStream(javaClass.classLoader.getResourceAsStream("UtahTeapot.vertices")!!)
    }
    private val blenderMonkey: List<Triangle> by lazy {
        loadVerticesStream(javaClass.classLoader.getResourceAsStream("BlenderMonkey.vertices")!!)
    }

    val registeredEntities = listOf(
        EntityInstantiator(
            "Debug Arrows",
            positionizer = SPAWN_IN_FRONT_OF_CAMERA
        ) { ray, _ ->
            DebugVectorsEntity(
                position = ray.o,
                camera = camera()
            )
        },
        EntityInstantiator("Utah Teapot") { ray, scale ->
            PrimitiveTrianglesEntity(
                position = ray.o,
                scale = scale,
                triangles = utahTeapot,
            )
        },
        EntityInstantiator("Blender Monkey") { ray, scale ->
            PrimitiveTrianglesEntity(
                position = ray.o,
                scale = scale,
                triangles = blenderMonkey,
            )
        },
    )
}
