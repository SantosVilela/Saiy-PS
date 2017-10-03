/*
 * Copyright (c) 2016. Saiy Ltd. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ai.saiy.android.nlu.nuance;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by benrandall76@gmail.com on 20/05/2016.
 */
public class Concept {

    @SerializedName("ranges")
    private final int[][] ranges;

    @SerializedName("literal")
    private final String literal;

    @SerializedName("value")
    private final String value;

    public Concept(@NonNull final String literal, @NonNull final int[][] ranges,
                   @NonNull final String value) {
        this.literal = literal;
        this.ranges = ranges;
        this.value = value;
    }

    public String getLiteral() {
        return literal;
    }

    public String getValue() {
        return value;
    }

    public int[][] getRanges() {
        return ranges;
    }
}
