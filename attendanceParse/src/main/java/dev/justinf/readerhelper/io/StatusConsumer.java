package dev.justinf.readerhelper.io;

import dev.justinf.readerhelper.ReaderHelper;
import dev.justinf.readerhelper.data.LabEvent;
import dev.justinf.readerhelper.util.DateParseUtil;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class StatusConsumer extends SheetConsumer {

    /*
     * 1 TIMESTAMP
     * 2 FIRST NAME
     * 3 LAST NAME
     * 4 NETID
     * 5 SID
     * 6 STATUS
     * 7 QUESTION (unused)
     * 8 STATUS UPDATE
     */

    private final ReaderHelper rh;

    public StatusConsumer(ReaderHelper rh, String filePath) {
        super(filePath);
        this.rh = rh;
    }

    @Override
    public void consume() {
        try (Scanner inFile = new Scanner(file)) {
            while (inFile.hasNextLine()) {
                String line = inFile.nextLine();
                String[] parts = line.split("\t", -1); // we use TSV
                if (parts[0].equalsIgnoreCase("Timestamp")) continue; // This is the first line, we should skip
                if (parts.length < 8) {
                    // Take care of any erroneous incomplete lines
                    System.out.println("BAD INPUT! Incomplete parts. (" + parts.length + ")");
                    System.out.println(line);
                    continue;
                }
                // Attempt to parse the date - if we can't move on
                Instant time;
                try {
                    time = DateParseUtil.fromTimestamp(parts[0]);
                } catch (DateTimeParseException ignored) {
                    System.out.println("BAD INPUT! Invalid date.");
                    System.out.println(line);
                    continue;
                }

                // We must determine what kind of status this is
                // We use the status column (parts[5]) for this
                LabEvent.Type type;
                switch (parts[5].toUpperCase()) {
                    case "CHECK OFF FOR READER":
                        // We must now use parts[7] to check if the lab was finished
                        type = parts[7].equalsIgnoreCase("Done") ? LabEvent.Type.LAB_DONE : LabEvent.Type.LAB_NOT_DONE;
                        break;
                    case "QUESTION FOR TA":
                        type = LabEvent.Type.QUESTION;
                        break;
                    default:
                        type = LabEvent.Type.UNKNOWN_STATUS;
                        break;
                }

                // Now, create the LabEvent
                LabEvent le = new LabEvent(time.toEpochMilli(), type);
                // Import strings
                le.setFirstName(parts[1]);
                le.setLastName(parts[2]);
                le.setNetId(parts[3]);
                le.setStudentId(parts[4]);
                le.setStatus(parts[5]);
                // Support for extra columns
                for (int i = 6; i < parts.length; i++) {
                    le.setStatus(le.getStatus() + "/" + parts[i]);
                }

                // Now, add to main program
                rh.processLabEvent(le);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}