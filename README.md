MC:Tools
=======================================================

A collection of libraries that somehow connected to Minecraft. There are some
modules that you may find useful.

Some time ago each module has its own git repository, but it was too hard to
maintain that way, so I moved them to the new location. The old repositories will
not be updated anymore.

Modules
-------------------------------------------------------

- [chat](modules/chat/README.md) -- Chat Message utilities
- [client](modules/client/README.md) -- Minecraft Java Edition client
- [mojang-api](modules/mojang-api/README.md) -- Mog API client
- [nbt](modules/nbt/README.md) -- NBT utilities, serializer and deserializer
- [paket](modules/paket/README.md) -- protocol base library
- [all](modules/all/README.md) -- all modules as one dependency
- [bom](modules/bom/README.md) -- Bill of Materials

Important Notice
-------------------------------------------------------

This library is not ready yet, mostly because it depends on
unfinished/experimental kotlinx-* stuff. Right now it is published in HandTruth
repository (https://mvn.handtruth.com). I can't guarantee that this repository
will persist.

Also, this library is meant to be multiplatform, but currently only JVM target
is supported. Library is targeting JVM 1.8 or higher.
