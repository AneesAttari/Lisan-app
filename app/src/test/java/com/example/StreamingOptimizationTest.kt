package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.LisanViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StreamingOptimizationTest {

    private lateinit var app: Application
    private lateinit var viewModel: LisanViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        viewModel = LisanViewModel(app)
    }

    @Test
    fun testStreamingMessageInitialStateIsNull() = runBlocking {
        val streaming = viewModel.streamingMessage.first()
        assertNull("streamingMessage state must initially be null", streaming)
    }

    @Test
    fun testClearChatResetsStreamingState() = runBlocking {
        viewModel.clearChat()
        val streaming = viewModel.streamingMessage.first()
        assertNull("clearChat must reset streamingMessage to null", streaming)
    }
}
