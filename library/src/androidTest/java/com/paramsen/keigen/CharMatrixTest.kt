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
class CharMatrixTest {
    @Test
    fun plus() {
        val x = CharMatrix(2, 2, 1.toChar())
        val y = CharMatrix(2, 2, 2.toChar())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toChar())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = CharMatrix(2, 2, 1.toChar())
        val xPointer = x.nativePointer
        val y = CharMatrix(2, 2, 2.toChar())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toChar())
        assertEqualsOverride(y[0, 0], 2.toChar())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = CharMatrix(3, 2)
        val y = CharMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = CharMatrix(2, 2, 1.toChar())
        val y = CharMatrix(2, 2, 2.toChar())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toChar(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = CharMatrix(2, 2, 1.toChar())
        val xPointer = x.nativePointer
        val y = CharMatrix(2, 2, 2.toChar())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toChar(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = CharMatrix(2, 4, 1.toChar())
        val y = CharMatrix(4, 3, 2.toChar())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toChar())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = CharMatrix(2, 4, 1.toChar())
        val xPointer = x.nativePointer
        val y = CharMatrix(4, 3, 2.toChar())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toChar())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = CharMatrix(4, 4, 1.toChar())
        val xPointer = x.nativePointer
        val y = CharMatrix(4, 4, 2.toChar())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toChar()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = CharMatrix(2, 4, 1.toChar())
        val xPointer = x.nativePointer
        val y = CharMatrix(4, 2, 2.toChar())
        val yPointer = y.nativePointer
        val z = CharMatrix(2, 2, 0.toChar())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toChar()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = CharMatrix(2, 2, 2.toChar())

        val y = x * 2.toChar()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toChar()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = CharMatrix(2, 2, 2.toChar())
        val xPointer = x.nativePointer

        x *= 2.toChar()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toChar()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = CharMatrix(2, 2, 4.toChar())

        val y = x / 2.toChar()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toChar()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = CharMatrix(2, 2, 4.toChar())
        val xPointer = x.nativePointer

        x /= 2.toChar()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toChar()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = charArrayOf(0.toChar(), 1.toChar(), 2.toChar(), 3.toChar(), 4.toChar(), 5.toChar())
        val x = CharMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = charArrayOf(0.toChar(), 1.toChar(), 2.toChar(), 3.toChar(), 4.toChar(), 5.toChar())
        val x = CharMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = CharMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see CharMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = CharMatrix(4, 4, 1.toChar())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toChar(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = CharMatrix(4, 4)
        x[0, 0] = 2.toChar()
        x[1, 2] = 5.toChar()

        assertEqualsOverride(2.toChar(), x[0, 0])
        assertEqualsOverride(5.toChar(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toChar()
        val m = CharMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = CharArray(9) { it.toChar() }
        val m = CharMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toChar(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = charArrayOf(0.toChar(), 1.toChar(), 2.toChar(), 3.toChar())
        val x = CharMatrix(2, 2, data)
        val y = CharMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toChar(), z[0, 0])
        assertEqualsOverride(2.toChar(), z[0, 1])
        assertEqualsOverride(4.toChar(), z[1, 0])
        assertEqualsOverride(6.toChar(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = charArrayOf(0.toChar(), 1.toChar(), 2.toChar(), 3.toChar())
        val x = CharMatrix(2, 2, data)
        val y = CharMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toChar(), z[0, 0])
        assertEqualsOverride(3.toChar(), z[0, 1])
        assertEqualsOverride(6.toChar(), z[1, 0])
        assertEqualsOverride(11.toChar(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = CharArray(rows * cols) { it.toChar() }
        val x = CharMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = CharArray(rows * cols) { it.toChar() }
        val x = CharMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = CharMatrix(2, 2, (-1).toChar())
        x.setArray(charArrayOf(0.toChar(), 1.toChar(), 2.toChar(), 3.toChar()))

        assertEqualsOverride(0.toChar(), x[0, 0])
        assertEqualsOverride(1.toChar(), x[0, 1])
        assertEqualsOverride(2.toChar(), x[1, 0])
        assertEqualsOverride(3.toChar(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = CharMatrix(2, 2)
        m.dispose()
        assertEquals(CharMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: CharMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: CharMatrix, y: CharMatrix, result: CharMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: CharMatrix.NativePointer, m: CharMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: CharMatrix, y: CharMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: CharMatrix.NativePointer, m: CharMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Char, was: Char) = assertEquals(expected, was)
}
