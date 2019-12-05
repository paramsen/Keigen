package com.paramsen.keigen

import androidx.annotation.IntRange

/**
 * @author Pär Amsen 11/2019
 */
class FloatMatrix {
    var nativePointer: NativePointer = NativePointer.nullPtr()
    var rows: Int = -1
    var cols: Int = -1

    init {
        KeigenNativeBridgeShared.loadNativeLibrary()
    }

    private constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, nativePointer: NativePointer) {
        this.nativePointer = nativePointer
        this.rows = rows
        this.cols = cols
    }

    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, fill: Float = 0.toFloat()): this(rows, cols, NativePointer(KeigenNativeBridgeFloat.initializeFill(rows, cols, fill)))

    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, data: FloatArray, @IntRange(from = 1) outerStride: Int = 1, @IntRange(from = 1) innerStride: Int = cols) : this(rows, cols, NativePointer.nullPtr()) {
        throwIfMatrixSizeExceedDataSize(data.size)
        throwIfStrideOutsideBounds(data.size, outerStride, innerStride)
        nativePointer = NativePointer(KeigenNativeBridgeFloat.initializeWithData(rows, cols, data, outerStride, innerStride))
    }

    operator fun plus(m: FloatMatrix): FloatMatrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return FloatMatrix(rows, cols, NativePointer(KeigenNativeBridgeFloat.matrixPlus(nativePointer.get(), m.nativePointer.get())))
    }

    operator fun plusAssign(m: FloatMatrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridgeFloat.matrixPlusAssign(nativePointer.get(), m.nativePointer.get())
    }

    operator fun minus(m: FloatMatrix): FloatMatrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return FloatMatrix(rows, cols, NativePointer(KeigenNativeBridgeFloat.matrixMinus(nativePointer.get(), m.nativePointer.get())))
    }

    operator fun minusAssign(m: FloatMatrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridgeFloat.matrixMinusAssign(nativePointer.get(), m.nativePointer.get())
    }

    operator fun times(m: FloatMatrix): FloatMatrix {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        return FloatMatrix(rows, m.cols, NativePointer(KeigenNativeBridgeFloat.matrixTimes(nativePointer.get(), m.nativePointer.get())))
    }

    /**
     * When rows==cols this operation is performed in place. Else a new native FloatMatrix is allocated
     * to fit the result, and the old one freed. For use in a loop or similar, it might be better
     * to use FloatMatrix.multiplyIntoDst.
     */
    operator fun timesAssign(m: FloatMatrix) {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        if(rows == m.rows && cols == m.cols) {
            KeigenNativeBridgeFloat.matrixTimesAssignRequireSquare(nativePointer.get(), m.nativePointer.get())
        } else {
            val oldNativePointer = nativePointer
            nativePointer = NativePointer(KeigenNativeBridgeFloat.matrixTimes(nativePointer.get(), m.nativePointer.get()))
            KeigenNativeBridgeShared.dispose(oldNativePointer.get())
            cols = m.cols
        }
    }

    /**
     * The result of (this * multiplyBy) is stored in (dst). No new allocations will be made to fit
     * the result into the dst FloatMatrix.
     */
    fun multiplyIntoDst(multiplyBy: FloatMatrix, dst: FloatMatrix) {
        check(dst.rows == rows && dst.cols == multiplyBy.cols) { "result must fit into dst (dst size (${dst.rows}x${dst.cols}) does not match (${rows}x${multiplyBy.cols}))" }
        KeigenNativeBridgeFloat.matrixTimesIntoDst(nativePointer.get(), multiplyBy.nativePointer.get(), dst.nativePointer.get())
    }

    operator fun times(scalar: Float): FloatMatrix {
        return FloatMatrix(rows, cols, NativePointer(KeigenNativeBridgeFloat.matrixTimesScalar(nativePointer.get(), scalar)))
    }

    operator fun timesAssign(scalar: Float) {
        KeigenNativeBridgeFloat.matrixTimesAssignScalar(nativePointer.get(), scalar)
    }

    operator fun div(scalar: Float): FloatMatrix {
        return FloatMatrix(rows, cols, NativePointer(KeigenNativeBridgeFloat.matrixDivScalar(nativePointer.get(), scalar)))
    }

    operator fun divAssign(scalar: Float) {
        KeigenNativeBridgeFloat.matrixDivAssignScalar(nativePointer.get(), scalar)
    }

    fun transpose() = FloatMatrix(cols, rows, NativePointer(KeigenNativeBridgeFloat.matrixTranspose(nativePointer.get())))

    /**
     * This actually creates a new native FloatMatrix due to restrictions in Eigen (which are
     * probably solvable in the C++ layer to actually re-use the same data allocation). The old
     * native FloatMatrix is disposed, and the new assigned to this Kotlin FloatMatrix.
     */
    fun transposeInPlace() {
        val newNativePointer = NativePointer(KeigenNativeBridgeFloat.matrixTranspose(nativePointer.get()))
        KeigenNativeBridgeShared.dispose(nativePointer.get())
        nativePointer = newNativePointer
        val oldRows = rows
        val oldCols = cols
        rows = oldCols
        cols = oldRows
    }

    operator fun get(row: Int, col: Int): Float {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridgeFloat.get(nativePointer.get(), row, col)
    }

    operator fun set(row: Int, col: Int, value: Float) {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridgeFloat.set(nativePointer.get(), row, col, value)
    }

    fun getArray(dst: FloatArray = FloatArray(rows * cols)): FloatArray {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(dst)
        KeigenNativeBridgeFloat.getArray(nativePointer.get(), dst)
        return dst
    }

    fun setArray(src: FloatArray) {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(src)
        KeigenNativeBridgeFloat.setArray(nativePointer.get(), src)
    }

    //TODO swap underlying data
    //TODO other operations (v2+)
    //TODO equals and hash

    fun dispose() {
        if(nativePointer == NativePointer.nullPtr()) return
        KeigenNativeBridgeShared.dispose(nativePointer.get())
        nativePointer = NativePointer.nullPtr()
    }

    private fun throwIfNullPointer() =
        check(nativePointer != NativePointer.nullPtr()) { "native pointer is null" }

    private fun throwIfDimensNotEqual(m: FloatMatrix) =
        check(rows == m.rows && cols == m.cols) { "dimensions must equal (this: [$rows, $cols], other: [${m.rows}, ${m.cols}])" }

    private fun throwIfOutsideBounds(row: Int, col: Int) =
        check(row < rows && col < cols) { "index overflow (access [$row, $col], bounds [$rows, $cols])" }

    private fun throwIfInvalidMultiplicationDimens(m: FloatMatrix) =
        check(cols == m.rows) { "cols must equal rows ($cols, ${m.rows})" }

    private fun throwIfMatrixSizeExceedDataSize(dataSize: Int) =
        check(dataSize >= rows * cols) { "matrix must fit inside data (data size $dataSize, matrix size: ${rows * cols})" }

    private fun throwIfStrideOutsideBounds(dataSize: Int, outerStride: Int, innerStride: Int) =
        check(dataSize >= rows * outerStride + innerStride) { "strides must not overflow array (array length $dataSize, last stride index: ${rows * outerStride + innerStride})" }

    private fun throwIfMatrixOutsideBounds(array: FloatArray) =
        check(array.size <= rows * cols) { "matrix must fit in array bounds (array length: ${array.size}, matrix length: ${rows * cols})" }


    data class NativePointer(private val nativePointer: Long) {
        fun get() = nativePointer

        companion object {
            private const val NULL_PTR = 0L
            fun nullPtr() = NativePointer(NULL_PTR)
        }
    }
}
