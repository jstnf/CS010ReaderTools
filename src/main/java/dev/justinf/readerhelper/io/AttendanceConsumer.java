package dev.justinf.readerhelper.io;

import dev.justinf.readerhelper.ReaderHelper;
import dev.justinf.readerhelper.data.LabEvent;
import dev.justinf.readerhelper.util.DateParseUtil;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AttendanceConsumer extends SheetConsumer {

    /*
     * 1 TIMESTAMP
     * 2 FIRST NAME
     * 3 LAST NAME
     * 4 NETID
     * 5 SID
     */

    private final ReaderHelper rh;

    public AttendanceConsumer(ReaderHelper rh, String filePath) {
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
                if (parts.length != 5) {
                    // Take care of any erroneous incomplete lines
                    System.out.println("BAD INPUT! Incomplete parts. (" + parts.length + ")");
                    System.out.println(line);
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

                // Now, create the LabEvent
                LabEvent le = new LabEvent(time.toEpochMilli(), LabEvent.Type.ATTENDANCE);
                // Import strings
                le.setFirstName(parts[1]);
                le.setLastName(parts[2]);
                le.setNetId(parts[3]);
                le.setStudentId(parts[4]);

                // Now, add to main program
                rh.processLabEvent(le);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}