package com.zhangke.architect.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val ApplicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)