package com.example.animationexpo.receipt

import kotlin.math.sqrt

data class Particle(
    var x: Float, var y: Float, var z: Float,
    var ox: Float, var oy: Float, var oz: Float
)

data class Constraint(
    val p1: Int,
    val p2: Int,
    val restLength: Float
)

data class ReceiptMesh(
    val particles: Array<Particle>,
    val uvs: FloatArray,
    val indices: IntArray,
    val constraints: List<Constraint>,
    val numX: Int,
    val numY: Int
)

object ReceiptMeshGenerator {

    const val NUM_X = 25
    const val NUM_Y = 50
    private const val WIDTH = 3.0f
    private const val HEIGHT = 6.0f

    fun generate(): ReceiptMesh {
        val numParticles = NUM_X * NUM_Y
        val particles = Array(numParticles) { Particle(0f, 0f, 0f, 0f, 0f, 0f) }
        val uvs = FloatArray(numParticles * 2)

        for (y in 0 until NUM_Y) {
            for (x in 0 until NUM_X) {
                val px = (x.toFloat() / (NUM_X - 1) - 0.5f) * WIDTH
                val py = -(y.toFloat() / (NUM_Y - 1)) * HEIGHT
                val i = y * NUM_X + x

                particles[i] = Particle(px, py, 0f, px, py, 0f)
                uvs[i * 2] = x.toFloat() / (NUM_X - 1)
                uvs[i * 2 + 1] = y.toFloat() / (NUM_Y - 1)
            }
        }

        val constraints = mutableListOf<Constraint>()

        for (y in 0 until NUM_Y) {
            for (x in 0 until NUM_X) {
                val i = y * NUM_X + x
                // Structural
                if (x < NUM_X - 1) constraints.add(createConstraint(particles, i, i + 1))
                if (y < NUM_Y - 1) constraints.add(createConstraint(particles, i, i + NUM_X))
                // Shear
                if (x < NUM_X - 1 && y < NUM_Y - 1) {
                    constraints.add(createConstraint(particles, i, i + NUM_X + 1))
                    constraints.add(createConstraint(particles, i + 1, i + NUM_X))
                }
                // Bending
                if (x < NUM_X - 2) constraints.add(createConstraint(particles, i, i + 2))
                if (y < NUM_Y - 2) constraints.add(createConstraint(particles, i, i + NUM_X * 2))
            }
        }

        val indices = mutableListOf<Int>()
        for (y in 0 until NUM_Y - 1) {
            for (x in 0 until NUM_X - 1) {
                val i = y * NUM_X + x
                indices.add(i); indices.add(i + 1); indices.add(i + NUM_X)
                indices.add(i + 1); indices.add(i + NUM_X + 1); indices.add(i + NUM_X)
            }
        }

        return ReceiptMesh(particles, uvs, indices.toIntArray(), constraints, NUM_X, NUM_Y)
    }

    private fun createConstraint(particles: Array<Particle>, i1: Int, i2: Int): Constraint {
        val p1 = particles[i1]
        val p2 = particles[i2]
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val dz = p2.z - p1.z
        return Constraint(i1, i2, sqrt(dx * dx + dy * dy + dz * dz))
    }
}
