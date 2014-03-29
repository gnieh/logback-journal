/*
 * This file is part of the logback-journal project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gnieh.logback;

import org.slf4j.MDC;

/**
 * Some constants that can be used to log some specific data. These constants
 * are used as keys in {@link MDC}.
 * 
 * @author Lucas Satabin
 * 
 */
public class SystemdJournal {

    private SystemdJournal() {
        // cannot be instantiated
    }

    /** The MESSAGE_ID used for messages logged in this thread */
    public static String MESSAGE_ID = "MESSAGE_ID";

}
