/*
 * Copyright (c) 2005-2018 Flamingo Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of
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
package org.pushingpixels.flamingo.api.common.model;

import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.neon.icon.ResizableIcon;

import javax.swing.event.*;
import java.beans.PropertyChangeEvent;

public class CommandProjection {
    private Command command;
    private CommandPresentation commandDisplay;

    private CommandButtonBuilder commandButtonBuilder;

    private static CommandButtonBuilder DEFAULT_BUILDER = new DefaultCommandButtonBuilder();

    public interface CommandButtonBuilder {
        AbstractCommandButton buildButton(CommandProjection commandProjection);
    }

    public static class DefaultCommandButtonBuilder implements CommandButtonBuilder {
        @Override
        public AbstractCommandButton buildButton(CommandProjection commandProjection) {
            Command command = commandProjection.getCommand();
            CommandPresentation commandDisplay = commandProjection.getCommandDisplay();

            String title = command.getTitle();
            ResizableIcon icon = (command.getIconFactory() != null)
                    ? command.getIconFactory().createNewIcon()
                    : command.getIcon();

            AbstractCommandButton result = commandDisplay.isMenu()
                    ? (command.isToggle() ? new JCommandToggleMenuButton(title, icon)
                    : new JCommandMenuButton(title, icon))
                    : (command.isToggle() ? new JCommandToggleButton(title, icon)
                    : new JCommandButton(title, icon));
            result.putClientProperty(FlamingoUtilities.COMMAND, command);
            return result;
        }
    }

    public CommandProjection(Command command, CommandPresentation commandDisplay) {
        this.command = command;
        this.commandDisplay = commandDisplay;
        this.commandButtonBuilder = DEFAULT_BUILDER;
    }

    public CommandProjection reproject(CommandPresentation newCommandDisplay) {
        return this.command.project(newCommandDisplay);
    }

    public void setCommandButtonBuilder(CommandButtonBuilder commandButtonBuilder) {
        if (commandButtonBuilder == null) {
            throw new IllegalArgumentException("Cannot pass null button builder");
        }
        this.commandButtonBuilder = commandButtonBuilder;
    }

    public Command getCommand() {
        return this.command;
    }

    public CommandPresentation getCommandDisplay() {
        return this.commandDisplay;
    }

    protected boolean hasAction() {
        return (this.command.getAction() != null);
    }

    protected boolean hasPopup() {
        return (this.command.getPopupCallback() != null);
    }

    private void populateButton(AbstractCommandButton button) {
        if (this.command.getDisabledIconFactory() != null) {
            button.setDisabledIcon(this.command.getDisabledIconFactory().createNewIcon());
        } else if (this.command.getDisabledIcon() != null) {
            button.setDisabledIcon(this.command.getDisabledIcon());
        }

        button.setExtraText(this.command.getExtraText());

        boolean hasAction = this.hasAction();
        boolean hasPopup = this.hasPopup();

        if (hasAction) {
            button.addCommandListener(this.command.getAction());
            button.setActionRichTooltip(this.command.getActionRichTooltip());
            button.setActionKeyTip(this.commandDisplay.getActionKeyTip());
        }

        if (!this.command.isToggle()) {
            JCommandButton jcb = (JCommandButton) button;
            if (hasPopup) {
                jcb.setPopupCallback(this.command.getPopupCallback());
                jcb.setPopupRichTooltip(this.command.getPopupRichTooltip());
                jcb.setPopupKeyTip(this.commandDisplay.getPopupKeyTip());
            }

            if (hasAction && hasPopup) {
                jcb.setCommandButtonKind(
                        this.command.isTitleClickAction()
                                ? JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION
                                : JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP);
            } else if (hasPopup) {
                jcb.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
            } else {
                jcb.setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_ONLY);
            }

            if (this.command.isAutoRepeatAction()) {
                jcb.setAutoRepeatAction(true);
                if (this.command.hasAutoRepeatIntervalsSet()) {
                    jcb.setAutoRepeatActionIntervals(this.command.getAutoRepeatInitialInterval(),
                            this.command.getAutoRepeatSubsequentInterval());
                }
            }

            jcb.setFireActionOnRollover(this.command.isFireActionOnRollover());
        }

        button.setEnabled(this.command.isEnabled());

        if (this.command.isToggleSelected()) {
            button.getActionModel().setSelected(true);
        }

        if (this.command.getPreviewListener() != null) {
            button.getActionModel().addChangeListener(new ChangeListener() {
                boolean wasRollover = false;

                @Override
                public void stateChanged(ChangeEvent e) {
                    boolean isRollover = button.getActionModel().isRollover();
                    if (wasRollover && !isRollover) {
                        command.getPreviewListener().onCommandPreviewCanceled(command);
                    }
                    if (!wasRollover && isRollover) {
                        command.getPreviewListener().onCommandPreviewActivated(command);
                    }
                    wasRollover = isRollover;
                }
            });
        }

        this.command.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("enabled".equals(evt.getPropertyName())) {
                button.setEnabled((Boolean) evt.getNewValue());
            }
            if ("icon".equals(evt.getPropertyName())) {
                button.setIcon((ResizableIcon) evt.getNewValue());
            }
            if ("isToggleSelected".equals(evt.getPropertyName())) {
                button.getActionModel().setSelected((Boolean) evt.getNewValue());
                if (command.getToggleGroupModel() != null) {
                    command.getToggleGroupModel().setSelected(command, (Boolean) evt.getNewValue());
                }
            }
            if ("extraText".equals(evt.getPropertyName())) {
                button.setExtraText((String) evt.getNewValue());
            }
            if ("action".equals(evt.getPropertyName())) {
                button.removeCommandListener((CommandListener) evt.getOldValue());
                button.addCommandListener((CommandListener) evt.getNewValue());
            }
        });
    }

    public AbstractCommandButton buildButton() {
        AbstractCommandButton result = this.commandButtonBuilder.buildButton(this);
        populateButton(result);

        result.setDisplayState(commandDisplay.getCommandDisplayState());
        result.setHorizontalAlignment(commandDisplay.getHorizontalAlignment());
        result.setHGapScaleFactor(commandDisplay.getHorizontalGapScaleFactor());
        result.setVGapScaleFactor(commandDisplay.getVerticalGapScaleFactor());
        result.setFlat(commandDisplay.isFlat());
        if (commandDisplay.getCommandIconDimension() != null) {
            result.updateCustomDimension(commandDisplay.getCommandIconDimension());
        }
        if (result instanceof JCommandButton) {
            ((JCommandButton) result).setPopupOrientationKind(
                    commandDisplay.getPopupOrientationKind());
        }
        return result;
    }

}
