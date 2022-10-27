package de.groovybyte.chunky.experimentalentitiesplugin.entities

import de.groovybyte.chunky.experimentalentitiesplugin.math.Triangle
import de.groovybyte.chunky.experimentalentitiesplugin.math.normalizedVertices
import de.groovybyte.chunky.experimentalentitiesplugin.math.set
import se.llbit.math.Transform
import se.llbit.math.Vector3
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.PI
import java.lang.Float.intBitsToFloat

/**
 * @author ShirleyNekoDev
 */

private val rotateX90 = Transform.NONE.rotateX(-PI / 2)
fun loadVerticesStream(inputStream: InputStream): List<Triangle> = inputStream
    .bufferedReader()
    .useLines { seq ->
        seq
            .filter { it.isNotBlank() }
            .map { line ->
                val (x, y, z) = line.split(" ").map { it.toDouble() }
                Vector3(x, y, z).also { rotateX90.apply(it) }
            }
            .toList()
            .normalizedVertices()
            .chunked(3)
            .map { (c0, c1, c2) -> Triangle(c0, c1, c2) }
    }

fun loadSTLStream(inputStream: InputStream): List<Triangle> = DataInputStream(inputStream)
    .use {
        val vertices = ArrayList<Vector3>()
        try {
            // skip the header
            it.skip(80)

            // get number triangles (not really needed)
            // WARNING: STL FILES ARE SMALL-ENDIAN
            val triangleCount = Integer.reverseBytes(it.readInt())
            vertices.ensureCapacity(3 * triangleCount)

            // read triangles
            fun DataInputStream.readVector3() = Vector3().also { vec3 ->
                for (dimension in 0 until 3) {
                    vec3[dimension] = intBitsToFloat(Integer.reverseBytes(readInt()))
                }
            }
            while (it.available() > 0) {
                // not used
                val normal = it.readVector3()

                vertices.add(it.readVector3())
                vertices.add(it.readVector3())
                vertices.add(it.readVector3())

                // not used
                val attribute = java.lang.Short.reverseBytes(it.readShort())
            }
        } catch (ex: IOException) {
            throw IOException(
                "Malformed STL binary at vertice number " + (vertices.size + 1),
                ex
            )
        }
        vertices
    }
    .normalizedVertices()
    .chunked(3)
    .map { (c0, c1, c2) -> Triangle(c0, c1, c2) }
