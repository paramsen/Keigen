package com.paramsen.keigen

import androidx.annotation.IntRange

/**
 * @author Pär Amsen 11/2019
 */
class Matrix(@IntRange(from = 1) var rows: Int, @IntRange(from = 1) var cols: Int, var nativePointer: Long) {
    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, fill: Float = 0f): this(rows, cols, KeigenNativeBridge.initializeFill(rows, cols, fill))

    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, data: FloatArray, @IntRange(from = 1) outerStride: Int = 1, @IntRange(from = 1) innerStride: Int = cols) : this(rows, cols, NULL_PTR) {
        throwIfMatrixSizeExceedDataSize(data.size)
        throwIfStrideOutsideBounds(data.size, outerStride, innerStride)
        nativePointer = KeigenNativeBridge.initializeWithData(rows, cols, data, outerStride, innerStride)
    }
    //matrix operations:
    // +,
    operator fun plus(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return Matrix(rows, cols, KeigenNativeBridge.matrixPlus(nativePointer, m.nativePointer))
    }

    // +=,
    operator fun plusAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridge.matrixPlusAssign(nativePointer, m.nativePointer)
    }
    // -,
    operator fun minus(m: Matrix): Matrix {
        TODO("impl")
    }
    // -=,
    operator fun minusAssign(m: Matrix) {
        TODO("impl")
    }
    // *,
    operator fun times(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        return Matrix(rows, m.cols, KeigenNativeBridge.matrixTimes(nativePointer, m.nativePointer))
    }
    // *=,
    operator fun timesAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        if(rows == m.rows && cols == m.cols) {
            KeigenNativeBridge.matrixTimesAssignRequireSquare(nativePointer, m.nativePointer)
        } else {
            val oldNativePointer = nativePointer
            nativePointer = KeigenNativeBridge.matrixTimes(nativePointer, m.nativePointer)
            KeigenNativeBridge.dispose(oldNativePointer)
            cols = m.cols
        }
    }
    // /,
    operator fun div(m: Matrix): Matrix {
        TODO("impl")
    }
    // /=
    operator fun divAssign(m: Matrix) {
        TODO("impl")
    }

    //scalar operations:
    // +,
    operator fun plus(scalar: Float): Matrix {
        TODO("impl")
    }
    // +=,
    operator fun plusAssign(scalar: Float) {
        TODO("impl")
    }
    // -,
    operator fun minus(scalar: Float): Matrix {
        TODO("impl")
    }
    // -=,
    operator fun minusAssign(scalar: Float) {
        TODO("impl")
    }
    // *,
    operator fun times(scalar: Float): Matrix {
        TODO("impl")
    }
    // *=,
    operator fun timesAssign(scalar: Float) {
        TODO("impl")
    }
    // /,
    operator fun div(scalar: Float): Matrix {
        TODO("impl")
    }
    // /=
    operator fun divAssign(scalar: Float) {
        TODO("impl")
    }

    //get index
    operator fun get(row: Int, col: Int): Float {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.get(nativePointer, row, col)
    }
    //get raw data
    //set index
    operator fun set(row: Int, col: Int, value: Float) {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.set(nativePointer, row, col, value)
    }

    fun getArray(dst: FloatArray = FloatArray(rows * cols)): FloatArray {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(dst)
        KeigenNativeBridge.getArray(nativePointer, dst)
        return dst
    }

    fun setArray(src: FloatArray) {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(src)
        KeigenNativeBridge.setArray(nativePointer, src)
    }

    //swap underlying data
    //other operations (v2+)
    //equals and hash

    fun dispose() {
        if(nativePointer == NULL_PTR) return
        KeigenNativeBridge.dispose(nativePointer)
        nativePointer = NULL_PTR
    }

    private fun throwIfNullPointer() =
        check(nativePointer != NULL_PTR) { "native pointer is null" }

    private fun throwIfDimensNotEqual(m: Matrix) =
        check(rows == m.rows && cols == m.cols) { "dimensions must equal (this: [$rows, $cols], other: [${m.rows}, ${m.cols}])" }

    private fun throwIfOutsideBounds(row: Int, col: Int) =
        check(row < rows && col < cols) { "index overflow (access [$row, $col], bounds [$rows, $cols])" }

    private fun throwIfInvalidMultiplicationDimens(m: Matrix) =
        check(cols == m.rows) { "cols must equal rows ($cols, ${m.rows})" }

    private fun throwIfMatrixSizeExceedDataSize(dataSize: Int) =
        check(dataSize >= rows * cols) { "matrix must fit inside data (data size $dataSize, matrix size: ${rows * cols})" }

    private fun throwIfStrideOutsideBounds(dataSize: Int, outerStride: Int, innerStride: Int) =
        check(dataSize >= rows * outerStride + innerStride) { "strides must not overflow array (array length $dataSize, last stride index: ${rows * outerStride + innerStride})" }

    private fun throwIfMatrixOutsideBounds(array: FloatArray) =
        check(array.size <= rows * cols) { "matrix must fit in array bounds (array length: ${array.size}, matrix length: ${rows * cols})" }

    companion object {
        const val NULL_PTR = 0L
    }
}