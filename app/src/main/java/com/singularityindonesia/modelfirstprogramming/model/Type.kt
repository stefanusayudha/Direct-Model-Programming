package com.singularityindonesia.modelfirstprogramming.model


@JvmInline
value class Name(val value: String)

@JvmInline
value class FullName(val value: String)

@JvmInline
value class Title(val value: String)

typealias PageTitle = Title

@JvmInline
value class Abstract(val value: String)

sealed class Image {
    enum class Orientation {
        UNDEFINED, HORIZONTAL, VERTICAL
    }

    data object Loading : Image()

    data class Bitmap(
        val value: android.graphics.Bitmap
    ) : Image() {
        val orientation: Orientation = Orientation.UNDEFINED
    }
}