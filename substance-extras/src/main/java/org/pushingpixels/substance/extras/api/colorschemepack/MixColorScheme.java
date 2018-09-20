/*
 * Copyright (c) 2005-2018 Substance Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
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
package org.pushingpixels.substance.extras.api.colorschemepack;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseColorScheme;
import org.pushingpixels.substance.api.colorscheme.SubstanceColorScheme;

/**
 * Mixed color scheme.
 * 
 * @author Kirill Grouchnikov
 */
public class MixColorScheme extends BaseColorScheme {
    /**
     * The main ultra-light color.
     */
    private Color mainUltraLightColor;

    /**
     * The main extra-light color.
     */
    private Color mainExtraLightColor;

    /**
     * The main light color.
     */
    private Color mainLightColor;

    /**
     * The main medium color.
     */
    private Color mainMidColor;

    /**
     * The main dark color.
     */
    private Color mainDarkColor;

    /**
     * The main ultra-dark color.
     */
    private Color mainUltraDarkColor;

    /**
     * The foreground color.
     */
    private Color foregroundColor;

    /**
     * The original color schemes.
     */
    private SubstanceColorScheme[] origSchemes;

    /**
     * Creates a new mixed color scheme.
     *
     * @param displayName
     * 		Display name for the new color scheme.
     * @param origSchemes
     * 		The original color schemes.
     */
    public MixColorScheme(String displayName,
            SubstanceColorScheme... origSchemes) {
        super(displayName, origSchemes[0].isDark());
        if ((origSchemes == null) || (origSchemes.length < 2)) {
            throw new IllegalArgumentException(
                    "At least two schemes needed for a mix scheme");
        }
        this.origSchemes = origSchemes;
        this.foregroundColor = this.origSchemes[0].getForegroundColor();
        this.mainUltraDarkColor = this.origSchemes[0].getUltraDarkColor();
        this.mainDarkColor = this.origSchemes[0].getDarkColor();
        this.mainMidColor = this.origSchemes[0].getMidColor();
        this.mainLightColor = this.origSchemes[0].getLightColor();
        this.mainExtraLightColor = this.origSchemes[0].getExtraLightColor();
        this.mainUltraLightColor = this.origSchemes[0].getUltraLightColor();

        StringBuffer idBuf = new StringBuffer();
        idBuf.append("Mixed ");
        String delim = "";
        for (SubstanceColorScheme origScheme : origSchemes) {
            idBuf.append(delim);
            idBuf.append(origScheme.getDisplayName());
            delim = " & ";
        }
    }

    @Override
    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    @Override
    public Color getUltraLightColor() {
        return this.mainUltraLightColor;
    }

    @Override
    public Color getExtraLightColor() {
        return this.mainExtraLightColor;
    }

    @Override
    public Color getLightColor() {
        return this.mainLightColor;
    }

    @Override
    public Color getMidColor() {
        return this.mainMidColor;
    }

    @Override
    public Color getDarkColor() {
        return this.mainDarkColor;
    }

    @Override
    public Color getUltraDarkColor() {
        return this.mainUltraDarkColor;
    }

    /**
     * Returns the original color schemes of <code>this</code> mixed color
     * scheme.
     *
     * @return The original color schemes of <code>this</code> mixed color
     * 	scheme.
     */
    public SubstanceColorScheme[] getOrigSchemes() {
        return this.origSchemes;
    }

    @Override
    public SubstanceColorScheme tint(double tintFactor) {
        SubstanceColorScheme[] tinted = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            tinted[i] = this.origSchemes[i].tint(tintFactor);
        }
        return new MixColorScheme("Tinted " + this.displayName + " "
                + tintFactor, tinted);
    }

    @Override
    public SubstanceColorScheme tone(double toneFactor) {
        SubstanceColorScheme[] toned = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            toned[i] = this.origSchemes[i].tone(toneFactor);
        }
        return new MixColorScheme("Toned " + this.displayName + " "
                + toneFactor, toned);
    }

    @Override
    public SubstanceColorScheme shade(double shadeFactor) {
        SubstanceColorScheme[] shaded = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            shaded[i] = this.origSchemes[i].shade(shadeFactor);
        }
        return new MixColorScheme("Shaded " + this.displayName + " "
                + shadeFactor, shaded);
    }

    @Override
    public SubstanceColorScheme saturate(double saturateFactor) {
        SubstanceColorScheme[] saturated = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            saturated[i] = this.origSchemes[i].saturate(saturateFactor);
        }
        return new MixColorScheme("Saturated " + this.displayName + " "
                + saturateFactor, saturated);
    }

    @Override
    public SubstanceColorScheme hueShift(double hueShiftFactor) {
        SubstanceColorScheme[] hieShifted = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            hieShifted[i] = this.origSchemes[i].hueShift(hueShiftFactor);
        }
        return new MixColorScheme("Hue-shifted " + this.displayName + " "
                + hueShiftFactor, hieShifted);
    }

    @Override
    public SubstanceColorScheme invert() {
        SubstanceColorScheme[] inverted = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            inverted[i] = this.origSchemes[i].invert();
        }
        return new MixColorScheme("Inverted " + this.displayName, inverted);
    }

    @Override
    public SubstanceColorScheme negate() {
        SubstanceColorScheme[] negated = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            negated[i] = this.origSchemes[i].negate();
        }
        return new MixColorScheme("Negated " + this.displayName, negated);
    }

    @Override
    public SubstanceColorScheme blendWith(SubstanceColorScheme otherScheme,
            double likenessToThisScheme) {
        SubstanceColorScheme[] blended = new SubstanceColorScheme[this.origSchemes.length];
        for (int i = 0; i < this.origSchemes.length; i++) {
            blended[i] = this.origSchemes[i].blendWith(otherScheme, likenessToThisScheme);
        }
        return new MixColorScheme("Blended " + this.displayName + " and "
                + otherScheme.getDisplayName() + " " +likenessToThisScheme, blended);
    }
}
