package app.ultradev.adventure

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.ByteBinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.EndBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.LongBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

/**
 * A custom decoder that converts a [BinaryTag] (produced by [NbtEncoder]) back into a Kotlin object.
 */
@OptIn(ExperimentalSerializationApi::class)
class NbtDecoder(private val tag: BinaryTag) : AbstractDecoder() {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (tag is CompoundBinaryTag) {
            0
        } else {
            CompositeDecoder.DECODE_DONE
        }
    }

    // When decoding a nullable value, an EndBinaryTag denotes null.
    override fun decodeNotNullMark(): Boolean {
        return tag !is EndBinaryTag
    }

    override fun decodeNull(): Nothing? {
        if (tag is EndBinaryTag) {
            return null
        } else {
            throw SerializationException("Expected EndBinaryTag to denote null, but found ${tag::class.simpleName}")
        }
    }

    override fun decodeInt(): Int {
        if (tag is IntBinaryTag) {
            return tag.value()
        } else {
            throw SerializationException("Expected IntBinaryTag but found ${tag::class.simpleName}")
        }
    }

    override fun decodeString(): String {
        if (tag is StringBinaryTag) {
            return tag.value()
        } else {
            throw SerializationException("Expected StringBinaryTag but found ${tag::class.simpleName}")
        }
    }

    override fun decodeBoolean(): Boolean {
        if (tag is ByteBinaryTag) {
            return tag.value() != 0.toByte()
        } else {
            throw SerializationException("Expected ByteBinaryTag for Boolean but found ${tag::class.simpleName}")
        }
    }

    override fun decodeLong(): Long {
        if (tag is LongBinaryTag) {
            return tag.value()
        } else {
            throw SerializationException("Expected LongBinaryTag but found ${tag::class.simpleName}")
        }
    }

    override fun decodeDouble(): Double {
        if (tag is DoubleBinaryTag) {
            return tag.value()
        } else {
            throw SerializationException("Expected DoubleBinaryTag but found ${tag::class.simpleName}")
        }
    }

    override fun decodeFloat(): Float {
        if (tag is FloatBinaryTag) {
            return tag.value()
        } else {
            throw SerializationException("Expected FloatBinaryTag but found ${tag::class.simpleName}")
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        if (tag is StringBinaryTag) {
            return enumDescriptor.getElementIndex(tag.value())
        } else {
            throw SerializationException("Expected IntBinaryTag for enum but found ${tag::class.simpleName}")
        }
    }

    // For structured types we expect a compound tag.
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (tag !is CompoundBinaryTag) {
            throw SerializationException("Expected CompoundBinaryTag for structure but found ${tag::class.simpleName}")
        }
        return NbtCompositeDecoder(tag)
    }
}
