package app.ultradev.adventure

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.kyori.adventure.nbt.BinaryTag

class NbtFormat(override val serializersModule: SerializersModule) : SerialFormat {
    companion object : SerialFormat {
        override val serializersModule: SerializersModule
            get() = EmptySerializersModule()

        val DEFAULT = NbtFormat(serializersModule)

        inline fun <reified T> encodeToBinaryTag(
            value: T,
            serializer: SerializationStrategy<T> = serializer()
        ): BinaryTag = DEFAULT.encodeToBinaryTag(value, serializer)

        inline fun <reified T> decodeFromBinaryTag(
            tag: BinaryTag,
            deserializer: DeserializationStrategy<T> = serializer()
        ): T = DEFAULT.decodeFromBinaryTag(tag, deserializer)
    }

    inline fun <reified T> encodeToBinaryTag(value: T, serializer: SerializationStrategy<T> = serializer()): BinaryTag {
        val encoder = NbtEncoder()
        serializer.serialize(encoder, value)
        return encoder.result ?: throw SerializationException("No result produced by NbtEncoder")
    }

    inline fun <reified T> decodeFromBinaryTag(
        tag: BinaryTag,
        deserializer: DeserializationStrategy<T> = serializer()
    ): T {
        val decoder = NbtDecoder(tag)
        return deserializer.deserialize(decoder)
    }
}