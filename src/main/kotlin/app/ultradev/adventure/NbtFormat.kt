package app.ultradev.adventure

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.kyori.adventure.nbt.BinaryTag

object NbtFormat : SerialFormat {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    inline fun <reified T> encodeToBinaryTag(value: T, serializer: SerializationStrategy<T> = serializer()): BinaryTag {
        val encoder = NbtEncoder()
        serializer.serialize(encoder, value)
        return encoder.result ?: throw SerializationException("No result produced by NbtEncoder")
    }

    inline fun <reified T> decodeFromBinaryTag(tag: BinaryTag, deserializer: DeserializationStrategy<T> = serializer()): T {
        val decoder = NbtDecoder(tag)
        return deserializer.deserialize(decoder)
    }
}