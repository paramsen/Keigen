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
class IntMatrixTest {
    @Test
    fun plus() {
        val x = IntMatrix(2, 2, 1.toInt())
        val y = IntMatrix(2, 2, 2.toInt())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toInt())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = IntMatrix(2, 2, 1.toInt())
        val xPointer = x.nativePointer
        val y = IntMatrix(2, 2, 2.toInt())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toInt())
        assertEqualsOverride(y[0, 0], 2.toInt())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = IntMatrix(3, 2)
        val y = IntMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = IntMatrix(2, 2, 1.toInt())
        val y = IntMatrix(2, 2, 2.toInt())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toInt(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = IntMatrix(2, 2, 1.toInt())
        val xPointer = x.nativePointer
        val y = IntMatrix(2, 2, 2.toInt())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toInt(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = IntMatrix(2, 4, 1.toInt())
        val y = IntMatrix(4, 3, 2.toInt())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toInt())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = IntMatrix(2, 4, 1.toInt())
        val xPointer = x.nativePointer
        val y = IntMatrix(4, 3, 2.toInt())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toInt())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = IntMatrix(4, 4, 1.toInt())
        val xPointer = x.nativePointer
        val y = IntMatrix(4, 4, 2.toInt())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toInt()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = IntMatrix(2, 4, 1.toInt())
        val xPointer = x.nativePointer
        val y = IntMatrix(4, 2, 2.toInt())
        val yPointer = y.nativePointer
        val z = IntMatrix(2, 2, 0.toInt())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toInt()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = IntMatrix(2, 2, 2.toInt())

        val y = x * 2.toInt()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toInt()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = IntMatrix(2, 2, 2.toInt())
        val xPointer = x.nativePointer

        x *= 2.toInt()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toInt()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = IntMatrix(2, 2, 4.toInt())

        val y = x / 2.toInt()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toInt()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = IntMatrix(2, 2, 4.toInt())
        val xPointer = x.nativePointer

        x /= 2.toInt()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toInt()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = intArrayOf(0.toInt(), 1.toInt(), 2.toInt(), 3.toInt(), 4.toInt(), 5.toInt())
        val x = IntMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = intArrayOf(0.toInt(), 1.toInt(), 2.toInt(), 3.toInt(), 4.toInt(), 5.toInt())
        val x = IntMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = IntMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see IntMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = IntMatrix(4, 4, 1.toInt())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toInt(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = IntMatrix(4, 4)
        x[0, 0] = 2.toInt()
        x[1, 2] = 5.toInt()

        assertEqualsOverride(2.toInt(), x[0, 0])
        assertEqualsOverride(5.toInt(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toInt()
        val m = IntMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = IntArray(9) { it.toInt() }
        val m = IntMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toInt(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = intArrayOf(0.toInt(), 1.toInt(), 2.toInt(), 3.toInt())
        val x = IntMatrix(2, 2, data)
        val y = IntMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toInt(), z[0, 0])
        assertEqualsOverride(2.toInt(), z[0, 1])
        assertEqualsOverride(4.toInt(), z[1, 0])
        assertEqualsOverride(6.toInt(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = intArrayOf(0.toInt(), 1.toInt(), 2.toInt(), 3.toInt())
        val x = IntMatrix(2, 2, data)
        val y = IntMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toInt(), z[0, 0])
        assertEqualsOverride(3.toInt(), z[0, 1])
        assertEqualsOverride(6.toInt(), z[1, 0])
        assertEqualsOverride(11.toInt(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = IntArray(rows * cols) { it.toInt() }
        val x = IntMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = IntArray(rows * cols) { it.toInt() }
        val x = IntMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = IntMatrix(2, 2, (-1).toInt())
        x.setArray(intArrayOf(0.toInt(), 1.toInt(), 2.toInt(), 3.toInt()))

        assertEqualsOverride(0.toInt(), x[0, 0])
        assertEqualsOverride(1.toInt(), x[0, 1])
        assertEqualsOverride(2.toInt(), x[1, 0])
        assertEqualsOverride(3.toInt(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = IntMatrix(2, 2)
        m.dispose()
        assertEquals(IntMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: IntMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: IntMatrix, y: IntMatrix, result: IntMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: IntMatrix.NativePointer, m: IntMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: IntMatrix, y: IntMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: IntMatrix.NativePointer, m: IntMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Int, was: Int) = assertEquals(expected, was)
}
