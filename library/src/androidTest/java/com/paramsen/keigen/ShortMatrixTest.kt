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
class ShortMatrixTest {
    @Test
    fun plus() {
        val x = ShortMatrix(2, 2, 1.toShort())
        val y = ShortMatrix(2, 2, 2.toShort())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toShort())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = ShortMatrix(2, 2, 1.toShort())
        val xPointer = x.nativePointer
        val y = ShortMatrix(2, 2, 2.toShort())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toShort())
        assertEqualsOverride(y[0, 0], 2.toShort())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = ShortMatrix(3, 2)
        val y = ShortMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = ShortMatrix(2, 2, 1.toShort())
        val y = ShortMatrix(2, 2, 2.toShort())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toShort(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = ShortMatrix(2, 2, 1.toShort())
        val xPointer = x.nativePointer
        val y = ShortMatrix(2, 2, 2.toShort())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toShort(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = ShortMatrix(2, 4, 1.toShort())
        val y = ShortMatrix(4, 3, 2.toShort())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toShort())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = ShortMatrix(2, 4, 1.toShort())
        val xPointer = x.nativePointer
        val y = ShortMatrix(4, 3, 2.toShort())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toShort())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = ShortMatrix(4, 4, 1.toShort())
        val xPointer = x.nativePointer
        val y = ShortMatrix(4, 4, 2.toShort())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toShort()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = ShortMatrix(2, 4, 1.toShort())
        val xPointer = x.nativePointer
        val y = ShortMatrix(4, 2, 2.toShort())
        val yPointer = y.nativePointer
        val z = ShortMatrix(2, 2, 0.toShort())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toShort()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = ShortMatrix(2, 2, 2.toShort())

        val y = x * 2.toShort()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toShort()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = ShortMatrix(2, 2, 2.toShort())
        val xPointer = x.nativePointer

        x *= 2.toShort()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toShort()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = ShortMatrix(2, 2, 4.toShort())

        val y = x / 2.toShort()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toShort()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = ShortMatrix(2, 2, 4.toShort())
        val xPointer = x.nativePointer

        x /= 2.toShort()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toShort()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = shortArrayOf(0.toShort(), 1.toShort(), 2.toShort(), 3.toShort(), 4.toShort(), 5.toShort())
        val x = ShortMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = shortArrayOf(0.toShort(), 1.toShort(), 2.toShort(), 3.toShort(), 4.toShort(), 5.toShort())
        val x = ShortMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = ShortMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see ShortMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = ShortMatrix(4, 4, 1.toShort())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toShort(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = ShortMatrix(4, 4)
        x[0, 0] = 2.toShort()
        x[1, 2] = 5.toShort()

        assertEqualsOverride(2.toShort(), x[0, 0])
        assertEqualsOverride(5.toShort(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toShort()
        val m = ShortMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = ShortArray(9) { it.toShort() }
        val m = ShortMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toShort(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = shortArrayOf(0.toShort(), 1.toShort(), 2.toShort(), 3.toShort())
        val x = ShortMatrix(2, 2, data)
        val y = ShortMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toShort(), z[0, 0])
        assertEqualsOverride(2.toShort(), z[0, 1])
        assertEqualsOverride(4.toShort(), z[1, 0])
        assertEqualsOverride(6.toShort(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = shortArrayOf(0.toShort(), 1.toShort(), 2.toShort(), 3.toShort())
        val x = ShortMatrix(2, 2, data)
        val y = ShortMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toShort(), z[0, 0])
        assertEqualsOverride(3.toShort(), z[0, 1])
        assertEqualsOverride(6.toShort(), z[1, 0])
        assertEqualsOverride(11.toShort(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = ShortArray(rows * cols) { it.toShort() }
        val x = ShortMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = ShortArray(rows * cols) { it.toShort() }
        val x = ShortMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = ShortMatrix(2, 2, (-1).toShort())
        x.setArray(shortArrayOf(0.toShort(), 1.toShort(), 2.toShort(), 3.toShort()))

        assertEqualsOverride(0.toShort(), x[0, 0])
        assertEqualsOverride(1.toShort(), x[0, 1])
        assertEqualsOverride(2.toShort(), x[1, 0])
        assertEqualsOverride(3.toShort(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = ShortMatrix(2, 2)
        m.dispose()
        assertEquals(ShortMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: ShortMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: ShortMatrix, y: ShortMatrix, result: ShortMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: ShortMatrix.NativePointer, m: ShortMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: ShortMatrix, y: ShortMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: ShortMatrix.NativePointer, m: ShortMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Short, was: Short) = assertEquals(expected, was)
}
