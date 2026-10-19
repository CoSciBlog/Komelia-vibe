package snd.komelia.image.coil

import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageResult
import snd.komelia.http.retryNetworkRead

/** Includes failures after response headers, which HTTP send retries cannot cover. */
class NetworkReadRetryInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult = retryNetworkRead {
        val result = chain.proceed()
        if (result is ErrorResult) throw result.throwable
        result
    }
}
