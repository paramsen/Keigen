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
class DoubleMatrixTest {
    @Test
    fun plus() {
        val x = DoubleMatrix(2, 2, 1.toDouble())
        val y = DoubleMatrix(2, 2, 2.toDouble())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toDouble())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = DoubleMatrix(2, 2, 1.toDouble())
        val xPointer = x.nativePointer
        val y = DoubleMatrix(2, 2, 2.toDouble())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toDouble())
        assertEqualsOverride(y[0, 0], 2.toDouble())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = DoubleMatrix(3, 2)
        val y = DoubleMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = DoubleMatrix(2, 2, 1.toDouble())
        val y = DoubleMatrix(2, 2, 2.toDouble())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toDouble(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = DoubleMatrix(2, 2, 1.toDouble())
        val xPointer = x.nativePointer
        val y = DoubleMatrix(2, 2, 2.toDouble())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toDouble(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = DoubleMatrix(2, 4, 1.toDouble())
        val y = DoubleMatrix(4, 3, 2.toDouble())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toDouble())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = DoubleMatrix(2, 4, 1.toDouble())
        val xPointer = x.nativePointer
        val y = DoubleMatrix(4, 3, 2.toDouble())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toDouble())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = DoubleMatrix(4, 4, 1.toDouble())
        val xPointer = x.nativePointer
        val y = DoubleMatrix(4, 4, 2.toDouble())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toDouble()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = DoubleMatrix(2, 4, 1.toDouble())
        val xPointer = x.nativePointer
        val y = DoubleMatrix(4, 2, 2.toDouble())
        val yPointer = y.nativePointer
        val z = DoubleMatrix(2, 2, 0.toDouble())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toDouble()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = DoubleMatrix(2, 2, 2.toDouble())

        val y = x * 2.toDouble()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toDouble()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = DoubleMatrix(2, 2, 2.toDouble())
        val xPointer = x.nativePointer

        x *= 2.toDouble()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toDouble()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = DoubleMatrix(2, 2, 4.toDouble())

        val y = x / 2.toDouble()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toDouble()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = DoubleMatrix(2, 2, 4.toDouble())
        val xPointer = x.nativePointer

        x /= 2.toDouble()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toDouble()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = doubleArrayOf(0.toDouble(), 1.toDouble(), 2.toDouble(), 3.toDouble(), 4.toDouble(), 5.toDouble())
        val x = DoubleMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = doubleArrayOf(0.toDouble(), 1.toDouble(), 2.toDouble(), 3.toDouble(), 4.toDouble(), 5.toDouble())
        val x = DoubleMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = DoubleMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see DoubleMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = DoubleMatrix(4, 4, 1.toDouble())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toDouble(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = DoubleMatrix(4, 4)
        x[0, 0] = 2.toDouble()
        x[1, 2] = 5.toDouble()

        assertEqualsOverride(2.toDouble(), x[0, 0])
        assertEqualsOverride(5.toDouble(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toDouble()
        val m = DoubleMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = DoubleArray(9) { it.toDouble() }
        val m = DoubleMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toDouble(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = doubleArrayOf(0.toDouble(), 1.toDouble(), 2.toDouble(), 3.toDouble())
        val x = DoubleMatrix(2, 2, data)
        val y = DoubleMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toDouble(), z[0, 0])
        assertEqualsOverride(2.toDouble(), z[0, 1])
        assertEqualsOverride(4.toDouble(), z[1, 0])
        assertEqualsOverride(6.toDouble(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = doubleArrayOf(0.toDouble(), 1.toDouble(), 2.toDouble(), 3.toDouble())
        val x = DoubleMatrix(2, 2, data)
        val y = DoubleMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toDouble(), z[0, 0])
        assertEqualsOverride(3.toDouble(), z[0, 1])
        assertEqualsOverride(6.toDouble(), z[1, 0])
        assertEqualsOverride(11.toDouble(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = DoubleArray(rows * cols) { it.toDouble() }
        val x = DoubleMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = DoubleArray(rows * cols) { it.toDouble() }
        val x = DoubleMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = DoubleMatrix(2, 2, (-1).toDouble())
        x.setArray(doubleArrayOf(0.toDouble(), 1.toDouble(), 2.toDouble(), 3.toDouble()))

        assertEqualsOverride(0.toDouble(), x[0, 0])
        assertEqualsOverride(1.toDouble(), x[0, 1])
        assertEqualsOverride(2.toDouble(), x[1, 0])
        assertEqualsOverride(3.toDouble(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = DoubleMatrix(2, 2)
        m.dispose()
        assertEquals(DoubleMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: DoubleMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: DoubleMatrix, y: DoubleMatrix, result: DoubleMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: DoubleMatrix.NativePointer, m: DoubleMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: DoubleMatrix, y: DoubleMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: DoubleMatrix.NativePointer, m: DoubleMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Double, was: Double) = assertEquals(expected, was, 0.000001)
}
