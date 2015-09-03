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

    private String service = System.getenv().getOrDefault("SERVICE", "unknown");

    boolean logLocation = true;

    boolean logException = true;

    boolean logThreadName = true;

    @Override
    protected void append(ILoggingEvent event) {
        try {
            StackTraceElementProxy[] stack = null;
            String stackTrace = "";
            String fileName = null;
            Integer lineNumber = null;
            String className = null;
            String methodName = null;
            String exnClass = null;
            String exnMessage = null;
            if (event.getThrowableProxy() != null) {
                stack = event.getThrowableProxy()
                        .getStackTraceElementProxyArray();
                // if one wants to log the exception name and message, just
                // do it
                if (logException) {
                    exnClass = event.getThrowableProxy().getClassName();
                    exnMessage = event.getThrowableProxy().getMessage();
                }
                // the location information if any is available and it is
                // enabled
                if (stack.length > 0 && logLocation) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(": ");
                    if (exnClass != null) sb.append(exnClass);
                    if (exnMessage != null) {
                        sb.append("(");
                        sb.append(exnMessage);
                        sb.append(") ");
                    }
                    for(StackTraceElementProxy trace : stack) {
                        sb.append(trace.toString());
                        sb.append("\n ");
                    }
                    stackTrace = sb.toString();
                    StackTraceElement elt = stack[0].getStackTraceElement();
                    fileName = elt.getFileName();
                    lineNumber = elt.getLineNumber();
                    className = elt.getClassName();
                    methodName = elt.getMethodName();
                }
            }
            // get the message id if any
            Map<String, String> mdc = event.getMDCPropertyMap();

            List<Object> messages = new ArrayList<>();
            // the formatted human readable message
            messages.add(event.getFormattedMessage() + stackTrace);

            // the log level
            messages.add("PRIORITY=%i");
            messages.add(levelToInt(event.getLevel(), mdc.get(LEVEL_OVERRIDE)));
            messages.add("SYSLOG_FACILITY=%i");
            messages.add(3);
            messages.add("SERVICE=%s");
            messages.add(service);
            mdc.remove(LEVEL_OVERRIDE);
            if(fileName != null) {
                messages.add("CODE_FILE=%s");
                messages.add(fileName);
            }
            if(lineNumber != null) {
                messages.add("CODE_LINE=%i");
                messages.add(lineNumber);
            }
            if(className != null && methodName != null) {
                messages.add("CODE_FUNC=%s.%s");
                messages.add(className);
                messages.add(methodName);
            }
            if(exnClass != null) {
                messages.add("EXN_NAME=%s");
                messages.add(exnClass);
            }
            if(exnMessage != null) {
                messages.add("EXN_MESSAGE=%s");
                messages.add(exnMessage);
            }

            // log thread name if enabled
            if (logThreadName) {
                messages.add("THREAD_NAME=%s");
                messages.add(event.getThreadName());
            }

            // log all mdc fields.
            for(String key : mdc.keySet()) {
                messages.add(key + "=%s");
                messages.add(mdc.get(key));
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
