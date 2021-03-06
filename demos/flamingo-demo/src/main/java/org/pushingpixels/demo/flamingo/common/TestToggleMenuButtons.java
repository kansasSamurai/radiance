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
package org.pushingpixels.demo.flamingo.common;

import org.pushingpixels.demo.flamingo.SkinSwitcher;
import org.pushingpixels.demo.flamingo.svg.logo.RadianceLogo;
import org.pushingpixels.demo.flamingo.svg.tango.transcoded.*;
import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.model.*;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.model.*;
import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.skin.BusinessSkin;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class TestToggleMenuButtons extends JFrame {
    private TestToggleMenuButtons() {
        this.setIconImage(RadianceLogo.getLogoImage(
                SubstanceCortex.GlobalScope.getCurrentSkin().getColorScheme(
                        SubstanceSlices.DecorationAreaType.PRIMARY_TITLE_PANE,
                        SubstanceSlices.ColorSchemeAssociationKind.FILL,
                        ComponentState.ENABLED)));

        CommandPresentation menuDisplay = CommandPresentation.builder().setMenu(true).build();

        JCommandButton singleChoice = new JCommandButton("single");
        singleChoice.setCommandButtonKind(CommandButtonKind.POPUP_ONLY);
        singleChoice.setDisplayState(CommandButtonDisplayState.MEDIUM);
        singleChoice.setFlat(false);

        CommandToggleGroupModel justifyGroup = new CommandToggleGroupModel();

        Command justifyLeft = Command.builder()
                .setTitle("left").setIcon(new Format_justify_left())
                .inToggleGroup(justifyGroup).build();
        Command justifyCenter = Command.builder()
                .setTitle("center").setIcon(new Format_justify_center())
                .inToggleGroup(justifyGroup).build();
        Command justifyRight = Command.builder()
                .setTitle("right").setIcon(new Format_justify_right())
                .inToggleGroup(justifyGroup).build();
        Command justifyFill = Command.builder()
                .setTitle("fill").setIcon(new Format_justify_fill())
                .inToggleGroup(justifyGroup).build();

        CommandPopupMenuContentModel justifyMenuContentModel =
                new CommandPopupMenuContentModel(new CommandProjectionGroupModel(
                        Arrays.asList(justifyLeft.project(menuDisplay),
                                justifyCenter.project(menuDisplay),
                                justifyRight.project(menuDisplay),
                                justifyFill.project(menuDisplay))));

        singleChoice.setPopupCallback((JCommandButton commandButton) ->
                new JCommandPopupMenu(justifyMenuContentModel,
                        CommandPopupMenuPresentationModel.builder().build()));

        JCommandButton multiChoice = new JCommandButton("multi");
        multiChoice.setCommandButtonKind(CommandButtonKind.POPUP_ONLY);
        multiChoice.setDisplayState(CommandButtonDisplayState.MEDIUM);
        multiChoice.setFlat(false);

        Command formatBold = Command.builder()
                .setTitle("bold").setIcon(new Format_text_bold())
                .setToggle().build();
        Command formatItalic = Command.builder()
                .setTitle("italic").setIcon(new Format_text_italic())
                .setToggle().build();
        Command formatUnderline = Command.builder()
                .setTitle("underline").setIcon(new Format_text_underline())
                .setToggle().build();
        Command formatStrikethrough = Command.builder()
                .setTitle("strikethrough").setIcon(new Format_text_strikethrough())
                .setToggle().build();

        CommandPopupMenuContentModel formatMenuContentModel =
                new CommandPopupMenuContentModel(new CommandProjectionGroupModel(
                        Arrays.asList(formatBold.project(menuDisplay),
                                formatItalic.project(menuDisplay),
                                formatUnderline.project(menuDisplay),
                                formatStrikethrough.project(menuDisplay))));

        multiChoice.setPopupCallback((JCommandButton commandButton) ->
                new JCommandPopupMenu(formatMenuContentModel,
                        CommandPopupMenuPresentationModel.builder()
                                .setToDismissOnCommandActivation(false).build()));

        JPanel main = new JPanel(new FlowLayout());
        main.add(singleChoice);
        main.add(multiChoice);
        this.add(main, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(SkinSwitcher.getSkinSwitcher(this));
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame.setDefaultLookAndFeelDecorated(true);
            SubstanceCortex.GlobalScope.setSkin(new BusinessSkin());

            new TestToggleMenuButtons().setVisible(true);
        });
    }
}
