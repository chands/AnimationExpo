package com.example.animationexpo.receipt

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.sqrt
import kotlin.math.tan

class ReceiptRenderer(
    private val mesh: ReceiptMesh,
    private val textureBitmap: Bitmap
) {
    private val particles = mesh.particles
    private val numX = mesh.numX
    private val numY = mesh.numY
    private val indices = mesh.indices

    private val projectedPositions = FloatArray(particles.size * 2)
    private val meshVerts = FloatArray((numX * numY) * 2)
    private val normalData = FloatArray(particles.size * 3)
    private val vertexColors = IntArray(particles.size)
    private val shortIndices: ShortArray

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }
    private val overlayPaint = Paint()

    private val lightDir1 = floatArrayOf(0.4f, 0.8f, 0.6f).normalized()
    private val lightDir2 = floatArrayOf(-0.5f, -0.2f, 0.8f).normalized()

    init {
        shortIndices = ShortArray(indices.size) { indices[it].toShort() }
    }

    companion object {
        private const val AMBIENT = 0.55f
        private const val DIFF1_STRENGTH = 0.4f
        private const val DIFF2_STRENGTH = 0.2f

        private const val CAM_X = 0f
        private const val CAM_Y = -2.0f
        private const val CAM_Z = 8.5f
        private val FOV = 45f * Math.PI.toFloat() / 180f
    }

    fun render(drawScope: DrawScope) {
        val canvasWidth = drawScope.size.width
        val canvasHeight = drawScope.size.height
        val aspect = canvasWidth / canvasHeight
        val tanFov = tan(FOV / 2f)

        projectVertices(canvasWidth, canvasHeight, aspect, tanFov)
        computeNormals()
        drawMesh(drawScope)
    }

    private fun projectVertices(
        canvasWidth: Float, canvasHeight: Float,
        aspect: Float, tanFov: Float
    ) {
        for (i in particles.indices) {
            val p = particles[i]
            val viewX = p.x - CAM_X
            val viewY = p.y - CAM_Y
            val viewZ = p.z - CAM_Z

            val ndcX = viewX / (-viewZ * aspect * tanFov)
            val ndcY = viewY / (-viewZ * tanFov)

            val screenX = (ndcX + 1f) * 0.5f * canvasWidth
            val screenY = (1f - ndcY) * 0.5f * canvasHeight

            projectedPositions[i * 2] = screenX
            projectedPositions[i * 2 + 1] = screenY

            meshVerts[i * 2] = screenX
            meshVerts[i * 2 + 1] = screenY
        }
    }

    private fun computeNormals() {
        normalData.fill(0f)

        var i = 0
        while (i < indices.size) {
            val i1 = indices[i]
            val i2 = indices[i + 1]
            val i3 = indices[i + 2]

            val p1 = particles[i1]
            val p2 = particles[i2]
            val p3 = particles[i3]

            val dx1 = p2.x - p1.x; val dy1 = p2.y - p1.y; val dz1 = p2.z - p1.z
            val dx2 = p3.x - p1.x; val dy2 = p3.y - p1.y; val dz2 = p3.z - p1.z

            val nx = dy1 * dz2 - dz1 * dy2
            val ny = dz1 * dx2 - dx1 * dz2
            val nz = dx1 * dy2 - dy1 * dx2

            normalData[i1 * 3] += nx; normalData[i1 * 3 + 1] += ny; normalData[i1 * 3 + 2] += nz
            normalData[i2 * 3] += nx; normalData[i2 * 3 + 1] += ny; normalData[i2 * 3 + 2] += nz
            normalData[i3 * 3] += nx; normalData[i3 * 3 + 1] += ny; normalData[i3 * 3 + 2] += nz

            i += 3
        }
    }

    private fun computeVertexLighting(vertexIndex: Int): Float {
        var nx = normalData[vertexIndex * 3]
        var ny = normalData[vertexIndex * 3 + 1]
        var nz = normalData[vertexIndex * 3 + 2]

        val len = sqrt(nx * nx + ny * ny + nz * nz)
        if (len > 0.0001f) {
            nx /= len; ny /= len; nz /= len
        }

        // Ensure normal faces viewer (positive Z)
        if (nz < 0f) { nx = -nx; ny = -ny; nz = -nz }

        val diff1 = maxOf(0f, nx * lightDir1[0] + ny * lightDir1[1] + nz * lightDir1[2])
        val diff2 = maxOf(0f, nx * lightDir2[0] + ny * lightDir2[1] + nz * lightDir2[2])

        return (AMBIENT + diff1 * DIFF1_STRENGTH + diff2 * DIFF2_STRENGTH).coerceIn(0f, 1f)
    }

    private fun drawMesh(drawScope: DrawScope) {
        val nativeCanvas = drawScope.drawContext.canvas.nativeCanvas

        nativeCanvas.drawBitmapMesh(
            textureBitmap,
            numX - 1,
            numY - 1,
            meshVerts,
            0,
            null,
            0,
            bitmapPaint
        )

        drawLightingOverlay(drawScope)
    }

    private fun drawLightingOverlay(drawScope: DrawScope) {
        for (i in particles.indices) {
            val lighting = computeVertexLighting(i)
            val alpha = ((1f - lighting) * 120f).toInt().coerceIn(0, 120)
            vertexColors[i] = android.graphics.Color.argb(alpha, 0, 0, 0)
        }

        val nativeCanvas = drawScope.drawContext.canvas.nativeCanvas
        overlayPaint.reset()

        nativeCanvas.drawVertices(
            android.graphics.Canvas.VertexMode.TRIANGLES,
            projectedPositions.size,
            projectedPositions,
            0,
            null,
            0,
            vertexColors,
            0,
            shortIndices,
            0,
            shortIndices.size,
            overlayPaint
        )
    }

    fun screenToWorld(screenX: Float, screenY: Float, canvasWidth: Float, canvasHeight: Float): Triple<Float, Float, Float> {
        val aspect = canvasWidth / canvasHeight
        val tanFov = tan(FOV / 2f)

        val ndcX = (screenX / canvasWidth) * 2f - 1f
        val ndcY = 1f - (screenY / canvasHeight) * 2f

        val dirX = ndcX * aspect * tanFov
        val dirY = ndcY * tanFov
        val dirZ = -1f
        val len = sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ)

        return Triple(dirX / len, dirY / len, dirZ / len)
    }

    fun findClosestParticle(screenX: Float, screenY: Float, canvasWidth: Float, canvasHeight: Float): Pair<Int, Float> {
        val (rdx, rdy, rdz) = screenToWorld(screenX, screenY, canvasWidth, canvasHeight)

        var minDist = Float.MAX_VALUE
        var bestIdx = -1
        var bestT = 0f

        for (i in particles.indices) {
            val p = particles[i]
            val vx = p.x - CAM_X
            val vy = p.y - CAM_Y
            val vz = p.z - CAM_Z

            val t = vx * rdx + vy * rdy + vz * rdz
            val px = CAM_X + rdx * t
            val py = CAM_Y + rdy * t
            val pz = CAM_Z + rdz * t

            val dx = p.x - px
            val dy = p.y - py
            val dz = p.z - pz
            val dist = sqrt(dx * dx + dy * dy + dz * dz)

            if (dist < minDist && dist < 1.0f) {
                minDist = dist
                bestIdx = i
                bestT = t
            }
        }

        return Pair(bestIdx, bestT)
    }

    fun getRayWorldPosition(screenX: Float, screenY: Float, canvasWidth: Float, canvasHeight: Float, depth: Float): Triple<Float, Float, Float> {
        val (rdx, rdy, rdz) = screenToWorld(screenX, screenY, canvasWidth, canvasHeight)
        return Triple(
            CAM_X + rdx * depth,
            CAM_Y + rdy * depth,
            CAM_Z + rdz * depth
        )
    }

    private fun FloatArray.normalized(): FloatArray {
        val len = sqrt(this[0] * this[0] + this[1] * this[1] + this[2] * this[2])
        return floatArrayOf(this[0] / len, this[1] / len, this[2] / len)
    }
}
