package com.paramsen.keigen

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for each operation. Must run on the Android platform due to the NDK layer being
 * compiled with the Android toolchains.
 *
 * @author Pär Amsen 11/2019
 */
@Suppress("MemberVisibilityCanBePrivate")
class FloatMatrixTest {
    @Test
    fun plus() {
        val x = FloatMatrix(2, 2, 1.toFloat())
        val y = FloatMatrix(2, 2, 2.toFloat())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toFloat())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = FloatMatrix(2, 2, 1.toFloat())
        val xPointer = x.nativePointer
        val y = FloatMatrix(2, 2, 2.toFloat())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toFloat())
        assertEqualsOverride(y[0, 0], 2.toFloat())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = FloatMatrix(3, 2)
        val y = FloatMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = FloatMatrix(2, 2, 1.toFloat())
        val y = FloatMatrix(2, 2, 2.toFloat())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toFloat(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = FloatMatrix(2, 2, 1.toFloat())
        val xPointer = x.nativePointer
        val y = FloatMatrix(2, 2, 2.toFloat())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toFloat(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = FloatMatrix(2, 4, 1.toFloat())
        val y = FloatMatrix(4, 3, 2.toFloat())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toFloat())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = FloatMatrix(2, 4, 1.toFloat())
        val xPointer = x.nativePointer
        val y = FloatMatrix(4, 3, 2.toFloat())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toFloat())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = FloatMatrix(4, 4, 1.toFloat())
        val xPointer = x.nativePointer
        val y = FloatMatrix(4, 4, 2.toFloat())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toFloat()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = FloatMatrix(2, 4, 1.toFloat())
        val xPointer = x.nativePointer
        val y = FloatMatrix(4, 2, 2.toFloat())
        val yPointer = y.nativePointer
        val z = FloatMatrix(2, 2, 0.toFloat())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toFloat()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = FloatMatrix(2, 2, 2.toFloat())

        val y = x * 2.toFloat()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toFloat()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = FloatMatrix(2, 2, 2.toFloat())
        val xPointer = x.nativePointer

        x *= 2.toFloat()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toFloat()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = FloatMatrix(2, 2, 4.toFloat())

        val y = x / 2.toFloat()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toFloat()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = FloatMatrix(2, 2, 4.toFloat())
        val xPointer = x.nativePointer

        x /= 2.toFloat()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toFloat()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = floatArrayOf(0.toFloat(), 1.toFloat(), 2.toFloat(), 3.toFloat(), 4.toFloat(), 5.toFloat())
        val x = FloatMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = floatArrayOf(0.toFloat(), 1.toFloat(), 2.toFloat(), 3.toFloat(), 4.toFloat(), 5.toFloat())
        val x = FloatMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = FloatMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see FloatMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = FloatMatrix(4, 4, 1.toFloat())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toFloat(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = FloatMatrix(4, 4)
        x[0, 0] = 2.toFloat()
        x[1, 2] = 5.toFloat()

        assertEqualsOverride(2.toFloat(), x[0, 0])
        assertEqualsOverride(5.toFloat(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toFloat()
        val m = FloatMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = FloatArray(9) { it.toFloat() }
        val m = FloatMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toFloat(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = floatArrayOf(0.toFloat(), 1.toFloat(), 2.toFloat(), 3.toFloat())
        val x = FloatMatrix(2, 2, data)
        val y = FloatMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toFloat(), z[0, 0])
        assertEqualsOverride(2.toFloat(), z[0, 1])
        assertEqualsOverride(4.toFloat(), z[1, 0])
        assertEqualsOverride(6.toFloat(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = floatArrayOf(0.toFloat(), 1.toFloat(), 2.toFloat(), 3.toFloat())
        val x = FloatMatrix(2, 2, data)
        val y = FloatMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toFloat(), z[0, 0])
        assertEqualsOverride(3.toFloat(), z[0, 1])
        assertEqualsOverride(6.toFloat(), z[1, 0])
        assertEqualsOverride(11.toFloat(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = FloatArray(rows * cols) { it.toFloat() }
        val x = FloatMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = FloatArray(rows * cols) { it.toFloat() }
        val x = FloatMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = FloatMatrix(2, 2, (-1).toFloat())
        x.setArray(floatArrayOf(0.toFloat(), 1.toFloat(), 2.toFloat(), 3.toFloat()))

        assertEqualsOverride(0.toFloat(), x[0, 0])
        assertEqualsOverride(1.toFloat(), x[0, 1])
        assertEqualsOverride(2.toFloat(), x[1, 0])
        assertEqualsOverride(3.toFloat(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = FloatMatrix(2, 2)
        m.dispose()
        assertEquals(FloatMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: FloatMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: FloatMatrix, y: FloatMatrix, result: FloatMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: FloatMatrix.NativePointer, m: FloatMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: FloatMatrix, y: FloatMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: FloatMatrix.NativePointer, m: FloatMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Float, was: Float) = assertEquals(expected, was)
}