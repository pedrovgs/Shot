package com.karumi.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> async(
    block: suspend CoroutineScope.() -> T
): T = withContext(Dispatchers.Default, block)