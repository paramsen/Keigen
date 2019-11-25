package com.paramsen.keigen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
    fun times() {
        val x = Matrix(2, 4, 1f)
        val y = Matrix(4, 3, 2f)

        val z = x * y

        assertEquals(z[0, 0], 8f)
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

        assertEquals(x[0, 0], 8f)
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

        assertEquals(x[0, 0], 8f)
        assertInPlaceOperation(xPointer, x)
        assertInPlaceOperation(yPointer, y)
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

    // === === === HELPERS === === ===

    fun assertOutOfPlaceOperation(x: Matrix, y: Matrix, result: Matrix) {
        assertNotEquals(x.nativePointer, result.nativePointer)
        assertNotEquals(y.nativePointer, result.nativePointer)
    }

    fun assertOutOfPlaceOperation(pointer: Long, m: Matrix) =
        assertNotEquals(pointer, m.nativePointer)

    fun assertInPlaceOperation(pointer: Long, m: Matrix) = assertEquals(pointer, m.nativePointer)

    fun assertShouldThrow(block: () -> Unit) = try {
        block()
        throw IllegalStateException("Should not have reached this!")
    } catch (e: Exception) {
        //OK
    }
}