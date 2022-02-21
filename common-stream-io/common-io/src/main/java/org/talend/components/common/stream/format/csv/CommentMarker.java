/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.common.stream.format.csv;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@GridLayout({ //
        @GridLayout.Row({ "commentMarkerType", "commentMarker" }), //
})
public class CommentMarker implements Serializable {

    private static final long serialVersionUID = 406534099976881354L;

    @Getter
    @RequiredArgsConstructor
    public enum Type {

        SEMICOLON(';'),
        COMMA(','),
        SPACE(' '),
        HASH('#'),
        OTHER((char) 0);

        private final char marker;
    }

    @Option
    @Documentation("Comment marker type.")
    private Type commentMarkerType = Type.SPACE;

    @Option
    @ActiveIf(target = "commentMarkerType", value = "OTHER")
    @Documentation("Custom comment marker.")
    private Character commentMarker;

    public Character findCommentMarker() {
        if (commentMarkerType != Type.OTHER) {
            return commentMarkerType.getMarker();
        }

        return commentMarker;
    }
}
