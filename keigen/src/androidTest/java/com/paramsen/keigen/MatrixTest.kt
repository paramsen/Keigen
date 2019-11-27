package com.paramsen.keigen

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for each operation. Must run on the Android platform due to the NDK stuff being
 * compiled with the Android toolchains.
 *
 * @author Pär Amsen 11/2019
 */
@Suppress("MemberVisibilityCanBePrivate")
class MatrixTest {
    @Test
    fun plus() {
        val x = Matrix(2, 2, 1f)
        val y = Matrix(2, 2, 2.5f)

        val z = x + y

        assertEquals(z[0, 0], 3.5f)
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = Matrix(2, 2, 1f)
        val xPointer = x.nativePointer
        val y = Matrix(2, 2, 2f)
        val yPointer = y.nativePointer

        x += y

        assertEquals(x[0, 0], 3f)
        assertEquals(y[0, 0], 2f)
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = Matrix(3, 2)
        val y = Matrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = Matrix(2, 2, 1f)
        val y = Matrix(2, 2, 2.5f)

        val z = x - y

        forRowCol(z) { i, j -> assertEquals(-1.5f, z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = Matrix(2, 2, 1f)
        val xPointer = x.nativePointer
        val y = Matrix(2, 2, 2f)
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEquals(-1f, x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = Matrix(2, 4, 1f)
        val y = Matrix(4, 3, 2f)

        val z = x * y

        forRowCol(z) { i, j -> assertEquals(z[i, j], 8f)}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = Matrix(2, 4, 1f)
        val xPointer = x.nativePointer
        val y = Matrix(4, 3, 2f)
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEquals(x[i, j], 8f)}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = Matrix(4, 4, 1f)
        val xPointer = x.nativePointer
        val y = Matrix(4, 4, 2f)
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEquals(x[i, j], 8f) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = Matrix(2, 4, 1f)
        val xPointer = x.nativePointer
        val y = Matrix(4, 2, 2f)
        val yPointer = y.nativePointer
        val z = Matrix(2, 2, 0f)
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEquals(z[i, j], 8f) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = Matrix(2, 2, 2f)

        val y = x * 2f

        forRowCol(y) { i, j -> assertEquals(y[i, j], 4f) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = Matrix(2, 2, 2f)
        val xPointer = x.nativePointer

        x *= 2f

        forRowCol(x) { i, j -> assertEquals(x[i, j], 4f) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = Matrix(2, 2, 5f)

        val y = x / 2f

        forRowCol(y) { i, j -> assertEquals(y[i, j], 2.5f) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = Matrix(2, 2, 5f)
        val xPointer = x.nativePointer

        x /= 2f

        forRowCol(x) { i, j -> assertEquals(x[i, j], 2.5f) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f)
        val x = Matrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEquals(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f)
        val x = Matrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = Matrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEquals(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see Matrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = Matrix(4, 4, 1.5f)
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEquals(1.5f, x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = Matrix(4, 4)
        x[0, 0] = 2f
        x[1, 2] = 5f

        assertEquals(2f, x[0, 0])
        assertEquals(5f, x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.5f
        val m = Matrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEquals(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = FloatArray(9) { it.toFloat() }
        val m = Matrix(3, 3, data)

        forRowCol(m) { i, j -> assertEquals(3f * i + j, m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = floatArrayOf(0f, 1f, 2f, 3f)
        val x = Matrix(2, 2, data)
        val y = Matrix(2, 2, data)
        val z = x + y

        assertEquals(0f, z[0, 0])
        assertEquals(2f, z[0, 1])
        assertEquals(4f, z[1, 0])
        assertEquals(6f, z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = floatArrayOf(0f, 1f, 2f, 3f)
        val x = Matrix(2, 2, data)
        val y = Matrix(2, 2, data)
        val z = x * y

        assertEquals(2f, z[0, 0])
        assertEquals(3f, z[0, 1])
        assertEquals(6f, z[1, 0])
        assertEquals(11f, z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = FloatArray(rows * cols) { it.toFloat() }
        val x = Matrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = FloatArray(rows * cols) { it.toFloat() }
        val x = Matrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = Matrix(2, 2, -1f)
        x.setArray(floatArrayOf(0f, 1f, 2f, 3f))

        assertEquals(0f, x[0, 0])
        assertEquals(1f, x[0, 1])
        assertEquals(2f, x[1, 0])
        assertEquals(3f, x[1, 1])
    }

    @Test
    fun dispose() {
        val m = Matrix(2, 2)
        m.dispose()
        assertEquals(Matrix.NULL_PTR, m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: Matrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: Matrix, y: Matrix, result: Matrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: Long, m: Matrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: Matrix, y: Matrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: Long, m: Matrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }
}