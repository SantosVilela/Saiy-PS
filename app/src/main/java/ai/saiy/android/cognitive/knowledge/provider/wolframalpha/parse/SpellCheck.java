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

package ai.saiy.android.cognitive.knowledge.provider.wolframalpha.parse;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by benrandall76@gmail.com on 07/08/2016.
 */

@Root(name = "spellcheck")
public class SpellCheck {

    @Attribute(name = "word")
    private String word;

    @Attribute(name = "suggestion")
    private String suggestion;

    @Attribute(name = "text")
    private String text;

    public SpellCheck() {
    }

    public SpellCheck(@Attribute(name = "suggestion") final String suggestion,
                      @Attribute(name = "text") final String text,
                      @Attribute(name = "word") final String word) {
        this.suggestion = suggestion;
        this.text = text;
        this.word = word;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getText() {
        return text;
    }

    public String getWord() {
        return word;
    }
}
