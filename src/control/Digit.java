/*
 * Copyright (c) 2008, 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package control;

import com.sun.deploy.util.ArrayUtil;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simple 7 segment LED style digit. It supports the numbers 0 through 9.
 */
public class Digit extends Group {

    private static final boolean[][] DIGIT_COMBINATIONS = new boolean[][]{
        {true,  false, true,  true,  true,  true,  true},
        {false, false, false, false, true,  false, true},
        {true,  true,  true,  false, true,  true,  false},
        {true,  true,  true,  false, true,  false, true},
        {false, true,  false, true,  true,  false, true},
        {true,  true,  true,  true,  false, false, true},
        {true,  true,  true,  true,  false, true,  true},
        {true,  false, false, false, true,  false, true},
        {true,  true,  true,  true,  true,  true,  true},
        {true,  true,  true,  true,  true,  false, true}};
    private final double[][] points = new double[][]{
            {2, 0, 52, 0, 42, 10, 12, 10},
            {12, 49, 42, 49, 52, 54, 42, 59, 12f, 59f, 2f, 54f},
            {12, 98, 42, 98, 52, 108, 2, 108},
            {0, 2, 10, 12, 10, 47, 0, 52},
            {44, 12, 54, 2, 54, 52, 44, 47},
            {0, 56, 10, 61, 10, 96, 0, 106},
            {44, 61, 54, 56, 54, 106, 44, 96}};

    public enum Symbol {
        E(true,true,true,true,false,true,false);
        private boolean[] combinations;
        Symbol(boolean... display){
            combinations = display;
        }
        public boolean[] getCombinations(){
            return combinations;
        }
    }
    private final Color onColor;
    private final Color offColor;
    private final Effect onEffect;
    private final Effect offEffect;
    private final Polygon[] polygons;
    public Digit(Color onColor, Color offColor, Effect onEffect, Effect offEffect, double scale) {
        this.onColor = onColor;
        this.offColor = offColor;
        this.onEffect = onEffect;
        this.offEffect = offEffect;
        polygons = buildPolygons(scale);
        getChildren().addAll(polygons);
        autosize();
        showNumber(0);
    }

    private Polygon[] buildPolygons(double scale) {
        Polygon[] polygons = new Polygon[points.length];
        double[][] scaledPoints = points;
        for(int i = 0;i < scaledPoints.length;i++){
            for(int j = 0;j < scaledPoints[i].length;j++){
                scaledPoints[i][j] *= scale;
            }
            polygons[i] = new Polygon(scaledPoints[i]);
        }
        return polygons;
    }

    public final void showNumber(Integer num) {
        if (num < 0 || num > 9) {
            num = 0; // default to 0 for non-valid numbers
        }

        for (int i = 0; i < 7; i++) {
            polygons[i].setFill(DIGIT_COMBINATIONS[num][i] ? onColor : offColor);
            polygons[i].setEffect(DIGIT_COMBINATIONS[num][i] ? onEffect : offEffect);
        }
    }
    public final void showSymbol(Symbol symbol){
        boolean[] combinations = symbol.getCombinations();
        for (int i = 0; i < 7; i++) {
            polygons[i].setFill(combinations[i] ? onColor : offColor);
            polygons[i].setEffect(combinations[i] ? onEffect : offEffect);
        }
    }
}
