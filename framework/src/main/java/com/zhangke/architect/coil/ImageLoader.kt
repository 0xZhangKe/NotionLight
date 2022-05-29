package com.zhangke.architect.coil

import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zhangke.framework.utils.appContext

val CoilImageLoader = ImageLoader(appContext).newBuilder()
    .components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }
    .build()