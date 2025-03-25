package app.ultradev.adventure

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.nbt.*

@OptIn(ExperimentalSerializationApi::class)
class NbtCompositeEncoder(private val parent: NbtEncoder, private val builder: CompoundBinaryTag.Builder) : CompositeEncoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        val key = descriptor.getElementName(index)
        builder.put(key, IntBinaryTag.intBinaryTag(value))
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        val key = descriptor.getElementName(index)
        builder.put(key, StringBinaryTag.stringBinaryTag(value))
    }

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Encoder {
        TODO("Not yet implemented")
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        val key = descriptor.getElementName(index)
        val childEncoder = NbtEncoder()
        serializer.serialize(childEncoder, value)
        builder.put(key, childEncoder.result!!)
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        val key = descriptor.getElementName(index)
        if (value == null) {
            builder.put(key, EndBinaryTag.endBinaryTag())
        } else {
            val childEncoder = NbtEncoder()
            serializer.serialize(childEncoder, value)
            builder.put(key, childEncoder.result!!)
        }
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        val key = descriptor.getElementName(index)
        builder.put(key, ByteBinaryTag.byteBinaryTag(if (value) 1 else 0))
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        val key = descriptor.getElementName(index)
        builder.put(key, ByteBinaryTag.byteBinaryTag(value))
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        val key = descriptor.getElementName(index)
        builder.put(key, ShortBinaryTag.shortBinaryTag(value))
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        val key = descriptor.getElementName(index)
        builder.put(key, StringBinaryTag.stringBinaryTag(value.toString()))
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        val key = descriptor.getElementName(index)
        builder.put(key, LongBinaryTag.longBinaryTag(value))
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        val key = descriptor.getElementName(index)
        builder.put(key, DoubleBinaryTag.doubleBinaryTag(value))
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        val key = descriptor.getElementName(index)
        builder.put(key, FloatBinaryTag.floatBinaryTag(value))
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // No additional action needed here.
        parent.result = builder.build()
    }
}