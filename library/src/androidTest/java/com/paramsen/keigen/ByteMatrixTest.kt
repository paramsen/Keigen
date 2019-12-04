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
class ByteMatrixTest {
    @Test
    fun plus() {
        val x = ByteMatrix(2, 2, 1.toByte())
        val y = ByteMatrix(2, 2, 2.toByte())

        val z = x + y

        assertEqualsOverride(z[0, 0], 3.toByte())
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun plusAssign() {
        val x = ByteMatrix(2, 2, 1.toByte())
        val xPointer = x.nativePointer
        val y = ByteMatrix(2, 2, 2.toByte())
        val yPointer = y.nativePointer

        x += y

        assertEqualsOverride(x[0, 0], 3.toByte())
        assertEqualsOverride(y[0, 0], 2.toByte())
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun plusThrowIfWrongDimens() {
        val x = ByteMatrix(3, 2)
        val y = ByteMatrix(4, 5)

        assertShouldThrow { x + y }
        assertShouldThrow { x += y }
    }
    @Test
    fun minus() {
        val x = ByteMatrix(2, 2, 1.toByte())
        val y = ByteMatrix(2, 2, 2.toByte())

        val z = x - y

        forRowCol(z) { i, j -> assertEqualsOverride((-1).toByte(), z[i, j])}
        assertOutOfPlaceOperation(x, y, z)
    }

    @Test
    fun minusAssign() {
        val x = ByteMatrix(2, 2, 1.toByte())
        val xPointer = x.nativePointer
        val y = ByteMatrix(2, 2, 2.toByte())
        val yPointer = y.nativePointer

        x -= y

        forRowCol(x) { i, j -> assertEqualsOverride((-1).toByte(), x[i, j])}

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun times() {
        val x = ByteMatrix(2, 4, 1.toByte())
        val y = ByteMatrix(4, 3, 2.toByte())

        val z = x * y

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toByte())}
        assertOutOfPlaceOperation(x, y, z)
    }

    /**
     * Where rows and cols differ, the product cannot safely fit at the same native address
     * and thus a new native reference is created.
     */
    @Test
    fun timesAssignOutOfPlace() {
        val x = ByteMatrix(2, 4, 1.toByte())
        val xPointer = x.nativePointer
        val y = ByteMatrix(4, 3, 2.toByte())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toByte())}

        assertOutOfPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    /** Where rows and cols are equal, the product fit at the same native address safely. */
    @Test
    fun timesAssignInPlace() {
        val x = ByteMatrix(4, 4, 1.toByte())
        val xPointer = x.nativePointer
        val y = ByteMatrix(4, 4, 2.toByte())
        val yPointer = y.nativePointer

        x *= y

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 8.toByte()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
    }

    @Test
    fun multiplyIntoDst() {
        val x = ByteMatrix(2, 4, 1.toByte())
        val xPointer = x.nativePointer
        val y = ByteMatrix(4, 2, 2.toByte())
        val yPointer = y.nativePointer
        val z = ByteMatrix(2, 2, 0.toByte())
        val zPointer = z.nativePointer

        x.multiplyIntoDst(y, z)

        forRowCol(z) { i, j -> assertEqualsOverride(z[i, j], 8.toByte()) }

        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
        assertInPlaceOperation(zPointer, z)
    }

    @Test
    fun timesScalar() {
        val x = ByteMatrix(2, 2, 2.toByte())

        val y = x * 2.toByte()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 4.toByte()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun timesAssignScalar() {
        val x = ByteMatrix(2, 2, 2.toByte())
        val xPointer = x.nativePointer

        x *= 2.toByte()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 4.toByte()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun divScalar() {
        val x = ByteMatrix(2, 2, 4.toByte())

        val y = x / 2.toByte()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], 2.toByte()) }

        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun divAssignScalar() {
        val x = ByteMatrix(2, 2, 4.toByte())
        val xPointer = x.nativePointer

        x /= 2.toByte()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], 2.toByte()) }

        assertInPlaceOperation(xPointer, x)
    }

    @Test
    fun transpose() {
        val data = byteArrayOf(0.toByte(), 1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte())
        val x = ByteMatrix(2, 3, data)

        val y = x.transpose()

        forRowCol(x) { i, j -> assertEqualsOverride(x[i, j], y[j, i]) }
        assertOutOfPlaceOperation(x, y)
    }

    @Test
    fun transposeInPlace() {
        val data = byteArrayOf(0.toByte(), 1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte())
        val x = ByteMatrix(2, 3, data)
        val xPointer = x.nativePointer
        val y = ByteMatrix(2, 3, data)

        x.transposeInPlace()

        forRowCol(y) { i, j -> assertEqualsOverride(y[i, j], x[j, i]) }
        assertOutOfPlaceOperation(xPointer, x) //see ByteMatrix.transposeInPlace doc
    }

    @Test
    fun get() {
        val x = ByteMatrix(4, 4, 1.toByte())
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                assertEqualsOverride(1.toByte(), x[i, j])
            }
        }
    }

    @Test
    fun set() {
        val x = ByteMatrix(4, 4)
        x[0, 0] = 2.toByte()
        x[1, 2] = 5.toByte()

        assertEqualsOverride(2.toByte(), x[0, 0])
        assertEqualsOverride(5.toByte(), x[1, 2])
    }

    @Test
    fun initFill() {
        val fill = 2.toByte()
        val m = ByteMatrix(2, 2, fill)

        forRowCol(m) { i, j -> assertEqualsOverride(fill, m[i, j])}
    }

    @Test
    fun initWithDataContinuous() {
        val data = ByteArray(9) { it.toByte() }
        val m = ByteMatrix(3, 3, data)

        forRowCol(m) { i, j -> assertEqualsOverride((3 * i + j).toByte(), m[i, j]) }
    }

    @Test
    fun initWithDataAndPlus() {
        val data = byteArrayOf(0.toByte(), 1.toByte(), 2.toByte(), 3.toByte())
        val x = ByteMatrix(2, 2, data)
        val y = ByteMatrix(2, 2, data)
        val z = x + y

        assertEqualsOverride(0.toByte(), z[0, 0])
        assertEqualsOverride(2.toByte(), z[0, 1])
        assertEqualsOverride(4.toByte(), z[1, 0])
        assertEqualsOverride(6.toByte(), z[1, 1])
    }

    @Test
    fun initWithDataAndMultiply() {
        val data = byteArrayOf(0.toByte(), 1.toByte(), 2.toByte(), 3.toByte())
        val x = ByteMatrix(2, 2, data)
        val y = ByteMatrix(2, 2, data)
        val z = x * y

        assertEqualsOverride(2.toByte(), z[0, 0])
        assertEqualsOverride(3.toByte(), z[0, 1])
        assertEqualsOverride(6.toByte(), z[1, 0])
        assertEqualsOverride(11.toByte(), z[1, 1])
    }

    @Test
    fun getArray() {
        val rows = 2
        val cols = 4
        val inData = ByteArray(rows * cols) { it.toByte() }
        val x = ByteMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun getArrayLarge() {
        val rows = 4
        val cols = 1024
        val inData = ByteArray(rows * cols) { it.toByte() }
        val x = ByteMatrix(rows, cols, inData)
        val outData = x.getArray()

        assertTrue(inData.contentEquals(outData))
    }

    @Test
    fun setArray() {
        val x = ByteMatrix(2, 2, (-1).toByte())
        x.setArray(byteArrayOf(0.toByte(), 1.toByte(), 2.toByte(), 3.toByte()))

        assertEqualsOverride(0.toByte(), x[0, 0])
        assertEqualsOverride(1.toByte(), x[0, 1])
        assertEqualsOverride(2.toByte(), x[1, 0])
        assertEqualsOverride(3.toByte(), x[1, 1])
    }

    @Test
    fun dispose() {
        val m = ByteMatrix(2, 2)
        m.dispose()
        assertEquals(ByteMatrix.NativePointer.nullPtr(), m.nativePointer)
    }
    // === === === HELPERS === === ===

    fun forRowCol(m: ByteMatrix, forEach: (Int, Int) -> Unit) {
        for (i in 0 until m.rows)
            for (j in 0 until m.rows)
                forEach(i, j)
    }

    fun assertOutOfPlaceOperation(x: ByteMatrix, y: ByteMatrix, result: ByteMatrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: ByteMatrix.NativePointer, m: ByteMatrix) = assertNotEquals(pointer, m.nativePointer)
    fun assertOutOfPlaceOperation(x: ByteMatrix, y: ByteMatrix) = assertNotEquals(x.nativePointer, y.nativePointer)
    fun assertInPlaceOperation(pointer: ByteMatrix.NativePointer, m: ByteMatrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }

    fun assertEqualsOverride(expected: Byte, was: Byte) = assertEquals(expected, was)
}
