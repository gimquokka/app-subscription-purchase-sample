package co.subscription.support

import java.time.ZoneId
import java.time.ZonedDateTime

fun Long.toZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(
    java.time.Instant.ofEpochMilli(this),
    ZoneId.of("Asia/Seoul"),
)