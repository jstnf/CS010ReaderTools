package dev.justinf.readerhelper.data;

import java.util.Date;

public class LabEvent implements Comparable<LabEvent> {

    public enum Type {
        ATTENDANCE, QUESTION, LAB_DONE, LAB_NOT_DONE, UNKNOWN_STATUS
    }

    private final long timestamp;
    private final Type type;

    // Verification strings (associated with each entry)
    private String firstName;
    private String lastName;
    private String netId;
    private String studentId;
    private String status;

    public LabEvent(long timestamp, Type type) {
        this.timestamp = timestamp;
        this.type = type;

        firstName = "???";
        lastName = "???";
        netId = "???";
        studentId = "???";
        status = "???";
    }

    @Override
    public String toString() {
        String result = (new Date(timestamp)).toString() + " " + type.name() + "     [" + lastName + ", " + firstName + " - " + netId + " / " + studentId;
        result = type != Type.ATTENDANCE ? result + " (" + status + ")]" : result + "]";
        return result;
    }

    @Override
    public int compareTo(LabEvent o) {
        return (int) (timestamp - o.timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
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

    public String getStatus() {
        return status;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}