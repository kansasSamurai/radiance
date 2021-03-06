/*
 * Copyright (c) 2018 Radiance Kormorant Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Radiance Kormorant Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.kormorant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.pushingpixels.flamingo.api.common.*
import org.pushingpixels.flamingo.api.common.CommandListener
import org.pushingpixels.flamingo.api.common.model.*
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback
import org.pushingpixels.neon.icon.ResizableIcon
import org.pushingpixels.neon.icon.ResizableIconFactory

interface ActionModelChangeInterface {
    fun stateChanged(model: ActionButtonModel)
}

interface PopupModelChangeInterface {
    fun stateChanged(model: PopupButtonModel)
}

@FlamingoElementMarker
open class KCommand {
    private val builder = Command.builder()
    private lateinit var button: AbstractCommandButton
    private var hasBeenConverted: Boolean = false

    var title: String? by NullableDelegate { hasBeenConverted }
    var icon: ResizableIcon? by NullableDelegate { hasBeenConverted }
    var iconFactory: ResizableIconFactory? by NullableDelegate { hasBeenConverted }
    var disabledIcon: ResizableIcon? by NullableDelegate { hasBeenConverted }
    var disabledIconFactory: ResizableIconFactory? by NullableDelegate { hasBeenConverted }
    var extraText: String? by NullableDelegate { hasBeenConverted }
    var action: CommandListener? by NullableDelegate { hasBeenConverted }
    var actionModelChangeListener: ActionModelChangeInterface? by NullableDelegate { hasBeenConverted }
    private var actionRichTooltip: KRichTooltip? by NullableDelegate { hasBeenConverted }
    var popupCallback: PopupPanelCallback? by NullableDelegate { hasBeenConverted }
    var popupModelChangeListener: PopupModelChangeInterface? by NullableDelegate { hasBeenConverted }
    private var popupRichTooltip: KRichTooltip? by NullableDelegate { hasBeenConverted }
    var isTitleClickAction: Boolean by NonNullDelegate { hasBeenConverted }
    var isTitleClickPopup: Boolean by NonNullDelegate { hasBeenConverted }

    // This is the only property that can be modified after a button has been
    // created from this command. The setter propagates the new value to the underlying
    // builder, as well as the button that has been created (if any).
    private var _isEnabled: Boolean = true
    var isEnabled: Boolean
        get() = _isEnabled
        set(value) {
            _isEnabled = value
            builder.setEnabled(value)
            if (hasBeenConverted) {
                button.isEnabled = value
            }
        }

    var isToggle: Boolean by NonNullDelegate { hasBeenConverted }
    var isToggleSelected: Boolean by NonNullDelegate { hasBeenConverted }
    var toggleGroup: KCommandToggleGroupModel? by NullableDelegate { hasBeenConverted }
    var isAutoRepeatAction: Boolean by NonNullDelegate { hasBeenConverted }
    var autoRepeatInitialInterval: Int by NonNullDelegate { hasBeenConverted }
    var autoRepeatSubsequentInterval: Int by NonNullDelegate { hasBeenConverted }
    var isFireActionOnRollover: Boolean by NonNullDelegate { hasBeenConverted }

    init {
        isTitleClickAction = false
        isTitleClickPopup = false
        isToggle = false
        isToggleSelected = false
        isAutoRepeatAction = false
        autoRepeatInitialInterval = JCommandButton.DEFAULT_AUTO_REPEAT_INITIAL_INTERVAL_MS
        autoRepeatSubsequentInterval = JCommandButton.DEFAULT_AUTO_REPEAT_SUBSEQUENT_INTERVAL_MS
        isFireActionOnRollover = false
    }

    fun actionRichTooltip(init: KRichTooltip.() -> Unit) {
        if (actionRichTooltip == null) {
            actionRichTooltip = KRichTooltip()
        }
        (actionRichTooltip as KRichTooltip).init()
    }

    fun popupRichTooltip(init: KRichTooltip.() -> Unit) {
        if (popupRichTooltip == null) {
            popupRichTooltip = KRichTooltip()
        }
        (popupRichTooltip as KRichTooltip).init()
    }

    companion object {
        fun populateBuilder(builder: Command.BaseBuilder<*, *>, command: KCommand) {
            builder.setTitle(command.title)
            builder.setIcon(command.icon)
            builder.setIconFactory(command.iconFactory)
            builder.setDisabledIcon(command.disabledIcon)
            builder.setDisabledIconFactory(command.disabledIconFactory)
            builder.setExtraText(command.extraText)
            builder.setAction(command.action)
            builder.setAutoRepeatAction(command.isAutoRepeatAction)

            builder.setActionRichTooltip(command.actionRichTooltip?.buildRichTooltip())
            builder.setPopupRichTooltip(command.popupRichTooltip?.buildRichTooltip())

            builder.setPopupCallback(command.popupCallback)

            if (command.isTitleClickAction) {
                builder.setTitleClickAction()
            }
            if (command.isTitleClickPopup) {
                builder.setTitleClickPopup()
            }

            if (command.isToggleSelected) {
                builder.setToggleSelected(command.isToggleSelected)
            } else {
                if (command.isToggle) {
                    builder.setToggle()
                }
            }
            if (command.toggleGroup != null) {
                builder.inToggleGroup(command.toggleGroup!!.javaCommandToggleModel)
            }

            builder.setEnabled(command.isEnabled)
        }
    }

    fun toJavaCommand(): Command {
        populateBuilder(builder, this)
        return builder.build()
    }

    internal fun toCommandButton(display: KCommandPresentation): AbstractCommandButton {
        button = toJavaCommand().project(display.toCommandDisplay()).buildButton()
        if (actionModelChangeListener != null) {
            button.actionModel.addChangeListener {
                actionModelChangeListener!!.stateChanged(button.actionModel)
            }
        }
        if ((popupModelChangeListener != null) && (button is JCommandButton)) {
            (button as JCommandButton).popupModel.addChangeListener {
                popupModelChangeListener!!.stateChanged((button as JCommandButton).popupModel)
            }
        }
        return button
    }
}

fun command(init: KCommand.() -> Unit): KCommand {
    val command = KCommand()
    command.init()
    return command
}

@FlamingoElementMarker
class KCommandPresentation {
    var commandDisplayState: CommandButtonDisplayState = CommandButtonDisplayState.FIT_TO_ICON
    var isFlat: Boolean = true
    var horizontalAlignment: Int = AbstractCommandButton.DEFAULT_HORIZONTAL_ALIGNMENT
    var horizontalGapScaleFactor: Double = AbstractCommandButton.DEFAULT_GAP_SCALE_FACTOR
    var verticalGapScaleFactor: Double = AbstractCommandButton.DEFAULT_GAP_SCALE_FACTOR
    var popupOrientationKind: JCommandButton.CommandButtonPopupOrientationKind
            = JCommandButton.CommandButtonPopupOrientationKind.DOWNWARD
    var commandIconDimension: Int? = null
    var isMenu: Boolean = false
    var actionKeyTip: String? = null
    var popupKeyTip: String? = null

    fun toCommandDisplay() : CommandPresentation {
        return CommandPresentation.builder()
                .setCommandDisplayState(commandDisplayState)
                .setFlat(isFlat)
                .setHorizontalAlignment(horizontalAlignment)
                .setHorizontalGapScaleFactor(horizontalGapScaleFactor)
                .setVerticalGapScaleFactor(verticalGapScaleFactor)
                .setPopupOrientationKind(popupOrientationKind)
                .setCommandIconDimension(commandIconDimension)
                .setActionKeyTip(actionKeyTip)
                .setPopupKeyTip(popupKeyTip)
                .setMenu(isMenu)
                .build()
    }
}

@FlamingoElementMarker
class KCommandGroup {
    var title: String? by NullableDelegate { false }
    internal val commands = arrayListOf<CommandConfig>()

    internal data class CommandConfig(val command: KCommand, val actionKeyTip: String?, val popupKeyTip: String?) {
        fun toProjection(): CommandProjection {
            return command.toJavaCommand().project(
                    CommandPresentation.builder()
                            .setActionKeyTip(actionKeyTip)
                            .setPopupKeyTip(popupKeyTip)
                            .build())
        }
    }

    operator fun KCommand.unaryPlus() {
        this@KCommandGroup.commands.add(CommandConfig(this, null, null))
    }

    fun command(actionKeyTip: String? = null, popupKeyTip: String? = null,
            init: KCommand.() -> Unit): KCommand {
        val command = KCommand()
        command.init()
        commands.add(CommandConfig(command, actionKeyTip, popupKeyTip))
        return command
    }

    fun toCommandGroupModel(): CommandProjectionGroupModel {
        return CommandProjectionGroupModel(title,
                commands.map { it -> it.toProjection() })
    }
}

fun DelayedCommandListener(listener: (CommandActionEvent) -> Unit): CommandListener {
    return CommandListener { event ->
        GlobalScope.launch(Dispatchers.Swing) {
            listener.invoke(event)
        }
    }
}





