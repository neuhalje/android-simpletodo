/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.android.view.base.notification;


import javax.annotation.CheckForNull;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

/**
 * Send to the event bus to show a notification (Toast/Crouton)
 */
public final class ViewShowNotificationCommand {


    @CheckForNull
    public final CharSequence text;
    public final Style style;
    public final int textRessourceId;

    public static enum Style {
        /**
         * Default style for alerting the user.
         */
        ALERT,
        /**
         * Default style for confirming an action.
         */
        CONFIRM,
        /**
         * Default style for general information.
         */
        INFO
    }

    public static ViewShowNotificationCommand makeText(CharSequence text, Style style) {
        checkNotNull(text, "text must not be null");
        checkNotNull(style, "style must not be null");
        return new ViewShowNotificationCommand(text, style);
    }

    public static ViewShowNotificationCommand makeText(int textResourceId, Style style) {
        checkNotNull(style, "style must not be null");
        return new ViewShowNotificationCommand(textResourceId, style);
    }

    public boolean isFromTextResource() {
        return null == text;
    }

    private ViewShowNotificationCommand(CharSequence text, Style style) {
        this.text = text;
        this.style = style;
        this.textRessourceId = Integer.MAX_VALUE;
    }

    private ViewShowNotificationCommand(int textResourceId, Style style) {
        this.style = style;
        this.text = null;
        this.textRessourceId = textResourceId;
    }


}
