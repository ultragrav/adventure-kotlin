package app.ultradev.adventure

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
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
import net.kyori.adventure.nbt.ShortBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

/**
 * A composite decoder for reading structured compound tags.
 */
@OptIn(ExperimentalSerializationApi::class)
class NbtCompositeDecoder(private val compound: CompoundBinaryTag) : CompositeDecoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    private var currentIndex = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (currentIndex < compound.size()) currentIndex++ else CompositeDecoder.DECODE_DONE
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is IntBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected IntBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is StringBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected StringBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Decoder {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        return NbtDecoder(element)
    }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        return NbtDecoder(element).run { decodeSerializableValue(deserializer) }
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is ByteBinaryTag) {
            return element.value() != 0.toByte()
        } else {
            throw SerializationException("Expected ByteBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeByteElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Byte {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is ByteBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected ByteBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeCharElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Char {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is StringBinaryTag) {
            val value = element.value()
            if (value.length == 1) {
                return value[0]
            } else {
                throw SerializationException("Expected single character for key '$key', but found string '$value'")
            }
        } else {
            throw SerializationException("Expected StringBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeShortElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Short {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is ShortBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected IntBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is LongBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected LongBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is DoubleBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected DoubleBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        val key = descriptor.getElementName(index)
        val element = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        if (element is FloatBinaryTag) {
            return element.value()
        } else {
            throw SerializationException("Expected FloatBinaryTag for key '$key', but found ${element::class.simpleName}")
        }
    }

    /**
     * An optional override to handle nullable elements. If the tag stored under the key is an [EndBinaryTag],
     * we treat it as a null value.
     */
    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        val key = descriptor.getElementName(index)
        val element: BinaryTag = compound[key]
            ?: throw SerializationException("Missing element for key '$key'")
        return if (element is EndBinaryTag) {
            null
        } else {
            NbtDecoder(element).run { decodeSerializableValue(deserializer) }
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // No additional action is needed on ending the structure.
    }
}
