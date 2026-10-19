package snd.komelia.image.coil

import coil3.PlatformContext
import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.size.Size
import io.ktor.utils.io.ClosedByteChannelException
import java.net.SocketException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class NetworkReadRetryInterceptorTest {
    @Test
    fun retriesBothThrownAndReturnedChannelErrorsButStopsOnOtherErrors() = runTest {
        var attempts = 0
        val terminal = IllegalArgumentException("bad image")
        val request = ImageRequest.Builder(PlatformContext.INSTANCE).data("test").build()
        val chain = object : Interceptor.Chain {
            override val request = request
            override val size = Size.ORIGINAL
            override fun withRequest(request: ImageRequest) = this
            override fun withSize(size: Size) = this
            override suspend fun proceed(): ImageResult {
                attempts++
                val aborted = ClosedByteChannelException(SocketException("Software caused connection abort"))
                return when (attempts) {
                    1 -> throw aborted
                    2 -> ErrorResult(null, request, aborted)
                    else -> ErrorResult(null, request, terminal)
                }
            }
        }
        val thrown = assertFailsWith<IllegalArgumentException> {
            NetworkReadRetryInterceptor().intercept(chain)
        }
        assertSame(terminal, thrown)
        assertEquals(3, attempts)
    }
}
