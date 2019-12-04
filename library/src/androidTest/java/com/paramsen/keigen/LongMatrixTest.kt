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
class LongMatrixTest {
    @Test
    fun plus() {
        val x = LongMatrix(2, 2, 1.toLong())
        val y = LongMatrix(2, 2, 2.toLong())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toLong())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = LongMatrix(2, 2, 1.toLong())
        val xPointer = x.nativePointer
        val y = LongMatrix(2, 2, 2.toLong())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toLong())
        assertEqualsOverride(y[0, 0], 2.toLong())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = LongMatrix(3, 2)
        val y = LongMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = LongMatrix(2, 2, 1.toLong())
        val y = LongMatrix(2, 2, 2.toLong())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toLong(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = LongMatrix(2, 2, 1.toLong())
        val xPointer = x.nativePointer
        val y = LongMatrix(2, 2, 2.toLong())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toLong(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = LongMatrix(2, 4, 1.toLong())
        val y = LongMatrix(4, 3, 2.toLong())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toLong())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = LongMatrix(2, 4, 1.toLong())
        val xPointer = x.nativePointer
        val y = LongMatrix(4, 3, 2.toLong())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toLong())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = LongMatrix(4, 4, 1.toLong())
        val xPointer = x.nativePointer
        val y = LongMatrix(4, 4, 2.toLong())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toLong()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = LongMatrix(2, 4, 1.toLong())
        val xPointer = x.nativePointer
        val y = LongMatrix(4, 2, 2.toLong())
        val yPointer = y.nativePointer
        val z = LongMatrix(2, 2, 0.toLong())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toLong()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = LongMatrix(2, 2, 2.toLong())

        val y = x * 2.toLong()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toLong()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = LongMatrix(2, 2, 2.toLong())
        val xPointer = x.nativePointer

        x *= 2.toLong()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toLong()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = LongMatrix(2, 2, 4.toLong())

        val y = x / 2.toLong()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toLong()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = LongMatrix(2, 2, 4.toLong())
        val xPointer = x.nativePointer

        x /= 2.toLong()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toLong()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = longArrayOf(0.toLong(), 1.toLong(), 2.toLong(), 3.toLong(), 4.toLong(), 5.toLong())
        val x = LongMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = longArrayOf(0.toLong(), 1.toLong(), 2.toLong(), 3.toLong(), 4.toLong(), 5.toLong())
        val x = LongMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = LongMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see LongMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = LongMatrix(4, 4, 1.toLong())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toLong(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = LongMatrix(4, 4)
        x[0, 0] = 2.toLong()
        x[1, 2] = 5.toLong()

        assertEqualsOverride(2.toLong(), x[0, 0])
        assertEqualsOverride(5.toLong(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toLong()
        val m = LongMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = LongArray(9) { it.toLong() }
        val m = LongMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toLong(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = longArrayOf(0.toLong(), 1.toLong(), 2.toLong(), 3.toLong())
        val x = LongMatrix(2, 2, data)
        val y = LongMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toLong(), z[0, 0])
        assertEqualsOverride(2.toLong(), z[0, 1])
        assertEqualsOverride(4.toLong(), z[1, 0])
        assertEqualsOverride(6.toLong(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = longArrayOf(0.toLong(), 1.toLong(), 2.toLong(), 3.toLong())
        val x = LongMatrix(2, 2, data)
        val y = LongMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toLong(), z[0, 0])
        assertEqualsOverride(3.toLong(), z[0, 1])
        assertEqualsOverride(6.toLong(), z[1, 0])
        assertEqualsOverride(11.toLong(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = LongArray(rows * cols) { it.toLong() }
        val x = LongMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = LongArray(rows * cols) { it.toLong() }
        val x = LongMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = LongMatrix(2, 2, (-1).toLong())
        x.setArray(longArrayOf(0.toLong(), 1.toLong(), 2.toLong(), 3.toLong()))

        assertEqualsOverride(0.toLong(), x[0, 0])
        assertEqualsOverride(1.toLong(), x[0, 1])
        assertEqualsOverride(2.toLong(), x[1, 0])
        assertEqualsOverride(3.toLong(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = LongMatrix(2, 2)
        m.dispose()
        assertEquals(LongMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: LongMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: LongMatrix, y: LongMatrix, result: LongMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: LongMatrix.NativePointer, m: LongMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: LongMatrix, y: LongMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: LongMatrix.NativePointer, m: LongMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Long, was: Long) = assertEquals(expected, was)
}
