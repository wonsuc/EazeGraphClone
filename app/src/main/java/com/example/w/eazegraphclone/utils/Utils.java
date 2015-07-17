/**
 *
 *   Copyright (C) 2014 Paul Cech
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.w.eazegraphclone.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.example.w.eazegraphclone.models.BaseModel;
import com.example.w.eazegraphclone.models.Point2D;

import java.util.List;

/**
 * static helper method들로 구성된 Helper class
 */
public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * DP를 PX로 변환하기
     *
     * @param _Dp the dp value to convert in pixel
     * @return the converted value in pixels
     */
    public static float dpToPx(float _Dp) {
        return _Dp * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Calculates the legend positions and which legend title should be displayed or not.
     *
     * Important: the LegendBounds in the _Models should be set and correctly calculated before this
     * function is called!
     * @param _Models The graph data which should have the BaseModel class as parent class.
     * @param _StartX Left starting point on the screen. Should be the absolute pixel value!
     * @param _Paint  The correctly set Paint which will be used for the text painting in the later process
     */
    public static void calculateLegendInformation(List<? extends BaseModel> _Models, float _StartX, float _EndX, Paint _Paint) {
        float textMargin = Utils.dpToPx(10.f);
        float lastX = _StartX;

        // calculate the legend label positions and check if there is enough space to display the label,
        // if not the label will not be shown
        for (BaseModel model : _Models) {

            Rect textBounds = new Rect();
            RectF legendBounds = model.getLegendBounds();

            _Paint.getTextBounds(model.getLegendLabel(), 0, model.getLegendLabel().length(), textBounds);
            model.setTextBounds(textBounds);

            float centerX = legendBounds.centerX();
            float centeredTextPos = centerX - (textBounds.width() / 2);
            float textStartPos = centeredTextPos - textMargin;

            // check if the text is too big to fit on the screen
            if (centeredTextPos + textBounds.width() > _EndX - textMargin) {
                model.setShowLabel(false);
            } else {
                // check if the current legend label overrides the label before
                // if the label overrides the label before, the current label will not be shown.
                // If not the label will be shown and the label position is calculated
                if (textStartPos < lastX) {
                    if (lastX + textMargin < legendBounds.left) {
                        model.setLegendLabelPosition((int) (lastX + textMargin));
                        model.setShowLabel(true);
                        lastX = lastX + textMargin + textBounds.width();
                    } else {
                        model.setShowLabel(false);
                    }
                } else {
                    model.setShowLabel(true);
                    model.setLegendLabelPosition((int) centeredTextPos);
                    lastX = centerX + (textBounds.width() / 2);
                }
            }

        }

    }

    /**
     * Returns an string with or without the decimal places.
     * @param _value        The value which should be converted
     * @param _showDecimal  Indicates whether the decimal numbers should be shown or not
     * @return              A generated string of the value.
     */
    public static String getFloatString(float _value, boolean _showDecimal) {
        if (_showDecimal) {
            return _value+"";
        }
        else {
            return ((int) _value) + "";
        }
    }

    /**
     * 사용되어진 Paint와 그 Paint의 설정을 기반으로 가능한 특정 텍스트의 가능한 최대값의 높이를 반환한다.
     *
     * @param _Paint Paint object which will be used to display a text.
     * @param _Text  The text which should be measured. If null, a default text is chosen, which
     *               has a maximum possible height
     * @return Maximum text height in px.
     */
    public static float calculateMaxTextHeight(Paint _Paint, String _Text) {
        Rect height = new Rect();
        String text = _Text == null ? "MgHITasger" : _Text;
        _Paint.getTextBounds(text, 0, text.length(), height);
        return height.height();
    }

    @SuppressLint("NewApi")
    public static void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @SuppressLint("NewApi")
    public static void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

}
