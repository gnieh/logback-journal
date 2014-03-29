package org.gnieh.logback;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Binding to the native journald library.
 * 
 * @author Lucas Satabin
 * 
 */
public interface SystemdJournalLibrary extends Library {

    SystemdJournalLibrary INSTANCE = (SystemdJournalLibrary) Native
            .loadLibrary("libsystemd-journal", SystemdJournalLibrary.class);

    int sd_journal_print(int priority, String format, Object... args);

    int sd_journal_send(String format, Object... args);

    int sd_journal_perror(String message);

}
