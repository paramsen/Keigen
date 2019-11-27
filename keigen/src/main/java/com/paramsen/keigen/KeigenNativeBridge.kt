package com.paramsen.keigen

/**
 * @author Pär Amsen 11/2019
 */
object KeigenNativeBridge {
    init {
        System.loadLibrary("eigen")
    }

    external fun initializeFill(rows: Int, cols: Int, fill: Float): Long
    external fun initializeWithData(rows: Int, cols: Int, fill: FloatArray, outerStride: Int, innerStride: Int): Long

    external fun matrixPlus(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixPlusAssign(nativePointerA: Long, nativePointerB: Long)

    external fun matrixTimes(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixTimesAssignRequireSquare(nativePointerA: Long, nativePointerB: Long)

    external fun get(nativePointer: Long, row: Int, col: Int): Float
    external fun set(nativePointer: Long, row: Int, col: Int, value: Float)
    external fun getArray(nativePointer: Long, dst: FloatArray)
    external fun setArray(nativePointer: Long, src: FloatArray)

    external fun dispose(nativePointer: Long)
}
