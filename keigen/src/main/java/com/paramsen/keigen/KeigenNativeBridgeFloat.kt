package com.paramsen.keigen

/**
 * @author Pär Amsen 11/2019
 */
object KeigenNativeBridgeFloat {
    external fun initializeFill(rows: Int, cols: Int, fill: Float): Long
    external fun initializeWithData(rows: Int, cols: Int, fill: FloatArray, outerStride: Int, innerStride: Int): Long

    external fun matrixPlus(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixPlusAssign(nativePointerA: Long, nativePointerB: Long)

    external fun matrixMinus(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixMinusAssign(nativePointerA: Long, nativePointerB: Long)

    external fun matrixTimes(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixTimesAssignRequireSquare(nativePointerA: Long, nativePointerB: Long)
    external fun matrixTimesIntoDst(a: Long, b: Long, dst: Long)

    external fun matrixTimesScalar(nativePointer: Long, scalar: Float): Long
    external fun matrixTimesAssignScalar(nativePointer: Long, scalar: Float)

    external fun matrixDivScalar(nativePointer: Long, scalar: Float): Long
    external fun matrixDivAssignScalar(nativePointer: Long, scalar: Float)

    external fun matrixTranspose(nativePointer: Long): Long

    external fun get(nativePointer: Long, row: Int, col: Int): Float
    external fun set(nativePointer: Long, row: Int, col: Int, value: Float)
    external fun getArray(nativePointer: Long, dst: FloatArray)
    external fun setArray(nativePointer: Long, src: FloatArray)
}
