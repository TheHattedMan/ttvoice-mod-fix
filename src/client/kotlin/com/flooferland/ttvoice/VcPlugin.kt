package com.flooferland.ttvoice

import com.flooferland.ttvoice.data.AudioScheduler
import com.flooferland.ttvoice.data.ModState
import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import net.minecraft.client.Minecraft
import de.maxhenkel.voicechat.api.VolumeCategory
import de.maxhenkel.voicechat.api.audiochannel.ClientEntityAudioChannel
import de.maxhenkel.voicechat.api.audiochannel.ClientStaticAudioChannel
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel
import java.util.UUID

public class VcPlugin : VoicechatPlugin {
    companion object {
        var api: VoicechatClientApi? = null
        var clientChannel: ClientStaticAudioChannel? = null
        var volumeCategory: VolumeCategory? = null
        var uuid: UUID? = null
        val scheduler = AudioScheduler()

        val modName: String
            get() = "Simple Voice Chat"
        val muted: Boolean
            get() = api?.isMuted ?: false
        val connected: Boolean
            get() = api?.let { !it.isDisabled && !it.isDisconnected } ?: false

        fun sendFrame(frame: ShortArray) {
            scheduler.pushFrame(frame)
        }
    }

    override fun getPluginId(): String? {
        return TextToVoiceClient.MOD_ID
    }

    override fun registerEvents(registration: EventRegistration?) {
        if (registration == null) {
            TextToVoiceClient.LOGGER.error("Registration was null!")
            return;
        }

        registration.registerEvent(ClientVoicechatConnectionEvent::class.java, { packet ->
            api = packet.voicechat
            if (packet.isConnected && api != null) {
                volumeCategory = api?.volumeCategoryBuilder()
                    ?.setId("${TextToVoiceClient.MOD_ID}_voice")
                    ?.setName("Text-To-Voice")
                    ?.build()
                    .also { api?.registerClientVolumeCategory(it) }
                uuid = UUID.nameUUIDFromBytes((Minecraft.getInstance().player!!.uuid.toString() + "-ttvoice").toByteArray())
                // clientChannel = api?.createEntityAudioChannel(uuid, api?.fromEntity(Minecraft.getInstance().player!!))?.also { it.category = volumeCategory?.id }
                clientChannel = api?.createStaticAudioChannel(uuid)?.also { it.category = volumeCategory?.id }
            } else {
                volumeCategory.let { api?.unregisterClientVolumeCategory(volumeCategory) }
            }
        }, 10)

        // NOTE: SVC expects 48,000 hz audio at 16 bits mono. (consistent 960 frame size)
        //       Screw you SVC for being ridiculously undocumented
        registration.registerEvent(MergeClientSoundEvent::class.java, { packet ->
            val frame = scheduler.next() ?: return@registerEvent
            packet.mergeAudio(frame)
            if (ModState.config.general.hearSelf) {
                clientChannel?.play(frame)
            }
        }, 10)
    }
}
