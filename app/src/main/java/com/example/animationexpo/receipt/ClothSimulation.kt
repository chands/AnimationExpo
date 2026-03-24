package com.example.animationexpo.receipt

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ClothSimulation(private val mesh: ReceiptMesh) {

    private val particles = mesh.particles
    private val constraints = mesh.constraints
    private val numX = mesh.numX
    private val receiptHeight = 6.0f

    var grabbedIndex: Int = -1
    private var time = 0f

    private companion object {
        const val DAMPING = 0.985f
        const val GRAVITY = -0.007f
        const val CONSTRAINT_ITERATIONS = 15
    }

    fun step() {
        time += 0.016f

        val windX = sin(time * 1.5f) * 0.0015f
        val windZ = cos(time * 1.1f) * 0.0015f

        for (i in particles.indices) {
            if (i < numX || i == grabbedIndex) continue

            val p = particles[i]
            val vx = (p.x - p.ox) * DAMPING
            val vy = (p.y - p.oy) * DAMPING
            val vz = (p.z - p.oz) * DAMPING

            p.ox = p.x
            p.oy = p.y
            p.oz = p.z

            val windFactor = p.y / -receiptHeight
            p.x += vx + windX * windFactor
            p.y += vy + GRAVITY
            p.z += vz + windZ * windFactor
        }

        relaxConstraints()
    }

    private fun relaxConstraints() {
        repeat(CONSTRAINT_ITERATIONS) {
            for (c in constraints) {
                val p1 = particles[c.p1]
                val p2 = particles[c.p2]

                val dx = p2.x - p1.x
                val dy = p2.y - p1.y
                val dz = p2.z - p1.z
                val dist = sqrt(dx * dx + dy * dy + dz * dz)

                val w1 = if (c.p1 < numX || c.p1 == grabbedIndex) 0f else 1f
                val w2 = if (c.p2 < numX || c.p2 == grabbedIndex) 0f else 1f
                val wSum = w1 + w2

                if (wSum > 0f && dist > 0f) {
                    val diff = (dist - c.restLength) / (dist * wSum)
                    val offsetX = dx * diff
                    val offsetY = dy * diff
                    val offsetZ = dz * diff

                    if (w1 > 0f) {
                        p1.x += offsetX
                        p1.y += offsetY
                        p1.z += offsetZ
                    }
                    if (w2 > 0f) {
                        p2.x -= offsetX
                        p2.y -= offsetY
                        p2.z -= offsetZ
                    }
                }
            }
        }
    }

    fun moveGrabbedParticle(x: Float, y: Float, z: Float) {
        if (grabbedIndex in particles.indices) {
            val p = particles[grabbedIndex]
            p.x = x; p.y = y; p.z = z
            p.ox = x; p.oy = y; p.oz = z
        }
    }
}
