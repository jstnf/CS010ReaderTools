package dev.justinf.readerhelper;

import dev.justinf.readerhelper.data.LabEvent;
import dev.justinf.readerhelper.data.StudentEntry;
import dev.justinf.readerhelper.io.AttendanceConsumer;
import dev.justinf.readerhelper.io.StatusConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ReaderHelper {

    private final AttendanceConsumer attendanceConsumer;
    private final StatusConsumer statusConsumer;
    private final Set<StudentEntry> students;
    private final Map<String, StudentEntry> studentsByNetId;
    private final Map<String, StudentEntry> studentsByStudentId;

    public ReaderHelper() {
        attendanceConsumer = new AttendanceConsumer(this, "./attendance.tsv");
        statusConsumer = new StatusConsumer(this, "./status.tsv");
        students = new TreeSet<>();
        studentsByNetId = new HashMap<>();
        studentsByStudentId = new HashMap<>();
    }

    public void run() {
        attendanceConsumer.consume();
        statusConsumer.consume();

        // Final stage: print all results and events
        System.out.println("\n\nRESULTS:");
        for (StudentEntry se : students) {
            System.out.println(se.getLastName() + ", " + se.getFirstName() + " BEGIN");
            for (LabEvent le : se.getLabEvents()) {
                System.out.println(le.toString());
            }
            System.out.println(se.getLastName() + ", " + se.getFirstName() + " END\n");
        }
    }

    public void processLabEvent(LabEvent le) {
        StudentEntry se;
        // Try student id first
        se = studentsByStudentId.get(le.getStudentId());
        if (se == null) {
            // Now try netId
            se = studentsByNetId.get(le.getNetId());
            if (se == null) {
                // We have a new student
                System.out.println("Detected new student: " + le.getLastName() + ", " + le.getFirstName());
                se = new StudentEntry(le.getFirstName(), le.getLastName(), le.getNetId(), le.getStudentId());
                students.add(se);
                studentsByNetId.put(se.getNetId(), se);
                studentsByStudentId.put(se.getStudentId(), se);
            } else {
                // We have a new student id
                studentsByStudentId.put(le.getStudentId(), se);
            }
        }

        se.addLabEvent(le);
    }

    public Set<StudentEntry> getStudents() {
        return students;
    }
}