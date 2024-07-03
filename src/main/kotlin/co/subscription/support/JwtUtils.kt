package co.subscription.support

import java.util.Base64

fun String.toDecodedPayload(): String =
    this.split(".")[1].toDecodedString()

private fun String.toDecodedString(): String =
    Base64.getUrlDecoder().decode(this).decodeToString()