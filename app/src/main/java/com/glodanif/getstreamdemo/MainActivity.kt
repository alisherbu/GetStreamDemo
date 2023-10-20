package com.glodanif.getstreamdemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.glodanif.getstreamdemo.databinding.ActivityMainBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var viewBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = this,
        )

        val client = ChatClient.Builder(API_KEY, applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()
        val user = User(
            id = "test-user-4",
            name = "Test User 4",
            image = "https://bit.ly/2TIt8NR"
        )
        client.connectUser(user, token = TEST_USER_4_TOKEN).enqueue { result ->
            if (result.isSuccess) {
                val viewModel: ChannelListViewModel by viewModels { ChannelListViewModelFactory() }
                viewModel.bindView(viewBinding.channelListView, this)
                toast("Success")
            } else {
                toast(result.errorOrNull()?.message ?: "Failure")
            }
        }
        viewBinding.btnCreate.setOnClickListener {
            client.createChannel(
                "messaging",
                viewBinding.edtChannel.text.toString(),
                listOf("test-user-3", "test-user-4"),
                emptyMap()
            ).enqueue {
                if (it.isSuccess) {
                    toast("Channel successfully created")
                } else {
                    toast(it.errorOrNull()?.message ?: "Failure")
                }
            }
        }
    }

    companion object {
        private const val API_KEY = "uwvc6bbpdbef"
        private const val TEST_USER_3_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdC11c2VyLTMifQ.k83Cg9eCxNopGCvR4RYGhkuYAKpjz5JDoWr8bKepuqY"

        private const val TEST_USER_4_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdC11c2VyLTQifQ.O1GutVLmDy-HWQ1TlRVfyEdkTWvWbwl4ifzg_JbG16o"
    }
}