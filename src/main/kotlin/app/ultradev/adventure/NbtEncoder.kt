package app.ultradev.adventure

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.ByteBinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.LongBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

/**
 * A custom encoder that produces a BinaryTag.
 */
@OptIn(ExperimentalSerializationApi::class)
class NbtEncoder : AbstractEncoder() {
    var result: BinaryTag? = null
    // We use a stack to handle nested compound structures.
    private val compoundStack = mutableListOf<CompoundBinaryTag.Builder>()

    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    // --- Primitive encodings ---
    override fun encodeInt(value: Int) {
        result = IntBinaryTag.intBinaryTag(value)
    }

    override fun encodeString(value: String) {
        result = StringBinaryTag.stringBinaryTag(value)
    }

    override fun encodeBoolean(value: Boolean) {
        // In this example, booleans are stored as a byte (1 for true, 0 for false)
        result = ByteBinaryTag.byteBinaryTag(if (value) 1 else 0)
    }

    override fun encodeLong(value: Long) {
        result = LongBinaryTag.longBinaryTag(value)
    }

    override fun encodeDouble(value: Double) {
        result = DoubleBinaryTag.doubleBinaryTag(value)
    }

    override fun encodeFloat(value: Float) {
        result = FloatBinaryTag.floatBinaryTag(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    // --- Handling structured data ---
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val builder = CompoundBinaryTag.builder()
        compoundStack.add(builder)
        return NbtCompositeEncoder(this, builder)
    }
}