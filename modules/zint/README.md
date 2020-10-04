# Module tools-zint

This module contains encoding-decoding functions for ZInt format. ZInt is an integer number format for storage and
transport purposes. There are 2 main ZInt format kinds: UZInt and SZInt.

## SZInt format

This format was created for numbers which values are usually located near 0 on a scale of integers.

This is an integer number format that consists of 2 parts: sign bit and one's compliment integer value bits.

TODO()

## UZInt format

Actually, this is a [VarInt](https://developers.google.com/protocol-buffers/docs/encoding#varints).
