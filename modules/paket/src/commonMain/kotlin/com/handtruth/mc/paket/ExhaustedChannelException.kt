package com.handtruth.mc.paket

public open class ExhaustedChannelException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(cause: Throwable?) : super(cause)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
}
