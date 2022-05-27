package dev.justinf.readerhelper.data;

import java.util.Set;
import java.util.TreeSet;

public class StudentEntry implements Comparable<StudentEntry> {

    private final String firstName;
    private final String lastName;
    private final String netId;
    private final String studentId;

    private final Set<LabEvent> labEvents;

    public StudentEntry(String firstName, String lastName, String netId, String studentId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.netId = netId;
        this.studentId = studentId;
        labEvents = new TreeSet<>();
    }

    public void addLabEvent(LabEvent le) {
        labEvents.add(le);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNetId() {
        return netId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Set<LabEvent> getLabEvents() {
        return labEvents;
    }

    @Override
    public int compareTo(StudentEntry o) {
        int lastNameComparison = lastName.compareToIgnoreCase(o.lastName);
        return lastNameComparison == 0 ? firstName.compareToIgnoreCase(o.firstName) : lastNameComparison;
    }
}