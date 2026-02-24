@file:Suppress("unused")

package com.flooferland.ttvoice.util

import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import com.flooferland.espeak.Espeak
import com.flooferland.ttvoice.speech.SpeechUtil
import kotlin.math.roundToInt

object Utils {
    fun noAudioMixerError(): String {
        return "Please select an audio mixer using /ttvoice mixer set"
    }

    fun lerp(a: Double, b: Double, t: Double): Double =
        a * (1 - t) + b * t
    fun lerp(a: Short, b: Short, t: Double): Short =
        (a + (b - a) * t).roundToInt().toShort()

    fun openLink(parent: Screen, link: String) {
        //? if >1.20.1 {
        /*ConfirmLinkScreen.confirmLinkNow(parent, link)
        *///?} else {
        ConfirmLinkScreen.confirmLinkNow(link, parent, true)
        //?}
    }

    fun <T: Any> createCycleButton(stringifier: (T) -> MutableComponent, initialValue: T): CycleButton.Builder<T> =
        //? if >1.21.10 {
        /*CycleButton.Builder(
            stringifier
        ) { initialValue }
        *///?} else {
        CycleButton.Builder<T>(stringifier)
            .withInitialValue(initialValue)!!
        //? }
}