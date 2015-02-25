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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * An appender that send the events to systemd journal
 * 
 * @author Lucas Satabin
 * 
 */
public class SystemdJournalAppender extends AppenderBase<ILoggingEvent> {

    public static String LEVEL_OVERRIDE = "LEVEL_OVERRIDE";

    boolean logLocation = true;

    boolean logException = true;

    boolean logThreadName = true;

    @Override
    protected void append(ILoggingEvent event) {
        try {
            // get the message id if any
            Map<String, String> mdc = event.getMDCPropertyMap();

            List<Object> messages = new ArrayList<>();
            // the formatted human readable message
            messages.add(event.getFormattedMessage());

            // the log level
            messages.add("PRIORITY=%i");
            messages.add(levelToInt(event.getLevel(), mdc.get(LEVEL_OVERRIDE)));
            mdc.remove(LEVEL_OVERRIDE);

            if (event.getThrowableProxy() != null) {
                StackTraceElementProxy[] stack = event.getThrowableProxy()
                        .getStackTraceElementProxyArray();
                if (stack.length > 0) {

                    // the location information if any is available and it is
                    // enabled
                    if (logLocation) {
                        StackTraceElement elt = stack[0].getStackTraceElement();
                        messages.add("CODE_FILE=%s");
                        messages.add(elt.getFileName());
                        messages.add("CODE_LINE=%i");
                        messages.add(elt.getLineNumber());
                        messages.add("CODE_FUNC=%s.%s");
                        messages.add(elt.getClassName());
                        messages.add(elt.getMethodName());
                    }

                    // if one wants to log the exception name and message, just
                    // do it
                    if (logException) {
                        messages.add("EXN_NAME=%s");
                        messages.add(event.getThrowableProxy().getClassName());
                        messages.add("EXN_MESSAGE=%s");
                        messages.add(event.getThrowableProxy().getMessage());
                    }
                }
            }

            // log thread name if enabled
            if (logThreadName) {
                messages.add("THREAD_NAME=%s");
                messages.add(event.getThreadName());
            }

            // log all mdc fields.
            for(String key : mdc.keySet()) {
                messages.add(key + "=" + mdc.get(key));
            }
            // the vararg list is null terminated
            messages.add(null);

            SystemdJournalLibrary journald = SystemdJournalLibrary.INSTANCE;

            journald.sd_journal_send("MESSAGE=%s", messages.toArray());
        } catch (UnsatisfiedLinkError e) {
            System.out.println(event.getLevel() + "> " + event.getFormattedMessage());
        } catch (NoClassDefFoundError e) {
            // not on a journald system, fall back to system.out
            System.out.println(event.getLevel() + "> " + event.getFormattedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int levelToInt(Level l, String override) {
        if("NOTICE".equals(override)) {
            return 5;
        }
        if("CRITICAL".equals(override)) {
            return 2;
        }
        if("ALERT".equals(override)) {
            return 1;
        }
        if("EMERGENCY".equals(override)) {
            return 0;
        }
        switch (l.toInt()) {
        case Level.TRACE_INT:
        case Level.DEBUG_INT:
            return 7;
        case Level.INFO_INT:
            return 6;
        case Level.WARN_INT:
            return 4;
        case Level.ERROR_INT:
            return 3;
        default:
            throw new IllegalArgumentException("Unknown level value: " + l);
        }
    }

    public boolean isLogLocation() {
        return logLocation;
    }

    public void setLogLocation(boolean logLocation) {
        this.logLocation = logLocation;
    }

    public boolean isLogThreadName() {
        return logThreadName;
    }

    public void setLogThreadName(boolean logThreadName) {
        this.logThreadName = logThreadName;
    }

    public boolean isLogException() {
        return logException;
    }

    public void setLogException(boolean logException) {
        this.logException = logException;
    }

}
