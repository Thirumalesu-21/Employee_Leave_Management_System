package employee_Leave_Management_System;

public class Validation {

    // NAME VALIDATION
    public static boolean isValidName(String name) {

        return name != null
                && !name.trim().isEmpty()
                && name.matches("[A-Za-z ]+");
    }

    // EMAIL VALIDATION
    public static boolean isValidEmail(String email) {

        return email != null
                && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // PHONE VALIDATION
    public static boolean isValidPhone(String phone) {

        return phone != null
                && phone.matches("[0-9]{10}");
    }

    // ID VALIDATION
    public static boolean isValidId(int id) {

        return id > 0;
    }

    // DEPARTMENT VALIDATION
    public static boolean isValidDepartment(String department) {

        return department != null
                && !department.trim().isEmpty()
                && department.matches("[A-Za-z ]+");
    }

    // LEAVE TYPE VALIDATION
    public static boolean isValidLeaveType(String leaveType) {

        if (leaveType == null) {
            return false;
        }

        return leaveType.equalsIgnoreCase("CASUAL")
                || leaveType.equalsIgnoreCase("SICK")
                || leaveType.equalsIgnoreCase("EARNED")
                || leaveType.equalsIgnoreCase("ANNUAL");
    }

    // LEAVE DAYS VALIDATION
    public static boolean isValidDays(int days) {

        return days > 0 && days <= 30;
    }

    // STATUS VALIDATION
    public static boolean isValidStatus(String status) {

        if (status == null) {
            return false;
        }

        return status.equalsIgnoreCase("PENDING")
                || status.equalsIgnoreCase("APPROVED")
                || status.equalsIgnoreCase("REJECTED");
    }
}