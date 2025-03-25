package app.ultradev.adventure

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class Test {
    @Serializable
    data class TestClass(val someInt: Int, val someString: String)

    @Test
    fun testEncoder() {
        val test = TestClass(42, "Hello, World!")
        val encoded = NbtFormat.encodeToBinaryTag(test)
        val decoded = NbtFormat.decodeFromBinaryTag<TestClass>(encoded)

        assert(test == decoded)
    }

    @Serializable
    sealed interface TestPolymorphic {
        @Serializable
        data class TestPolymorphicA(val someInt: Int) : TestPolymorphic

        @Serializable
        data class TestPolymorphicB(val someString: String) : TestPolymorphic
    }

    @Test
    fun testPolymorphicEncoder() {
        val testA = TestPolymorphic.TestPolymorphicA(42)
        val encodedA = NbtFormat.encodeToBinaryTag<TestPolymorphic>(testA)
        val decodedA = NbtFormat.decodeFromBinaryTag<TestPolymorphic>(encodedA)

        assert(testA == decodedA)

        val testB = TestPolymorphic.TestPolymorphicB("Hello, World!")
        val encodedB = NbtFormat.encodeToBinaryTag<TestPolymorphic>(testB)
        val decodedB = NbtFormat.decodeFromBinaryTag<TestPolymorphic>(encodedB)

        assert(testB == decodedB)
    }
}