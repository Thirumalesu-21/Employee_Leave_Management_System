package employee_Leave_Management_System;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

public class Tablescreation {

    // =====================================================
    // CREATE TABLES
    // =====================================================

    public static void createTables(Connection con) {

        String employees = """
        CREATE TABLE IF NOT EXISTS employees (employee_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, phone VARCHAR(10) NOT NULL UNIQUE, department VARCHAR(50) NOT NULL)
        """;

        String leaveRequests = """
        CREATE TABLE IF NOT EXISTS leave_requests (leave_id INT AUTO_INCREMENT PRIMARY KEY, employee_id INT NOT NULL, leave_type VARCHAR(20) NOT NULL, days INT NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'PENDING', CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id))
        """;

        String leaveBalance = """
        CREATE TABLE IF NOT EXISTS leave_balance (balance_id INT AUTO_INCREMENT PRIMARY KEY, employee_id INT NOT NULL UNIQUE, total_leave INT NOT NULL DEFAULT 20, used_leave INT NOT NULL DEFAULT 0, remaining_leave INT NOT NULL DEFAULT 20, CONSTRAINT fk_balance_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id))
        """;

        try (Statement stmt = con.createStatement()) {

            stmt.executeUpdate(employees);
            stmt.executeUpdate(leaveRequests);
            stmt.executeUpdate(leaveBalance);

            System.out.println("All tables created successfully.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // ADD EMPLOYEE
    // =====================================================

    public static void addEmployee(
            Connection con,
            Scanner sc) {

        String sql = """
        INSERT INTO employees (name, email, phone, department) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            if (!Validation.isValidName(name)) {

                System.out.println("Invalid name.");
                return;
            }

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            if (!Validation.isValidEmail(email)) {

                System.out.println("Invalid email.");
                return;
            }

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            if (!Validation.isValidPhone(phone)) {

                System.out.println(
                        "Phone must contain exactly 10 digits.");

                return;
            }

            System.out.print("Enter Department: ");
            String department = sc.nextLine();

            if (!Validation.isValidDepartment(department)) {

                System.out.println("Invalid department.");
                return;
            }

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, department);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            int employeeId = 0;

            if (rs.next()) {

                employeeId = rs.getInt(1);
            }

            // CREATE INITIAL LEAVE BALANCE

            String balanceSql = """
            INSERT INTO leave_balance (employee_id, total_leave, used_leave, remaining_leave) VALUES (?, ?, ?, ?)
            """;

            try (PreparedStatement balancePs =
                         con.prepareStatement(balanceSql)) {

                balancePs.setInt(1, employeeId);
                balancePs.setInt(2, 20);
                balancePs.setInt(3, 0);
                balancePs.setInt(4, 20);

                balancePs.executeUpdate();
            }

            System.out.println(
                    "Employee added successfully.");

            System.out.println(
                    "Employee ID: " + employeeId);

            System.out.println(
                    "Initial Leave Balance: 20");

        } catch (SQLIntegrityConstraintViolationException e) {

            System.out.println(
                    "Email or phone already exists.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // VIEW EMPLOYEES
    // =====================================================

    public static void viewEmployees(Connection con) {

        String sql = """
        SELECT employee_id, name, email, phone, department FROM employees
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            System.out.println(
                    "\n========== EMPLOYEES ==========");

            while (rs.next()) {

                System.out.println(
                        "Employee ID : "
                                + rs.getInt("employee_id"));

                System.out.println(
                        "Name        : "
                                + rs.getString("name"));

                System.out.println(
                        "Email       : "
                                + rs.getString("email"));

                System.out.println(
                        "Phone       : "
                                + rs.getString("phone"));

                System.out.println(
                        "Department  : "
                                + rs.getString("department"));

                System.out.println(
                        "--------------------------------");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // SEARCH EMPLOYEE
    // =====================================================

    public static void searchEmployee(
            Connection con,
            Scanner sc) {

        String sql = """
        SELECT employee_id, name, email, phone, department FROM employees WHERE employee_id = ?
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Employee ID: ");

            int id =
                    Integer.parseInt(sc.nextLine());

            if (!Validation.isValidId(id)) {

                System.out.println("Invalid ID.");
                return;
            }

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                System.out.println(
                        "\n========== EMPLOYEE DETAILS ==========");

                System.out.println(
                        "ID         : "
                                + rs.getInt("employee_id"));

                System.out.println(
                        "Name       : "
                                + rs.getString("name"));

                System.out.println(
                        "Email      : "
                                + rs.getString("email"));

                System.out.println(
                        "Phone      : "
                                + rs.getString("phone"));

                System.out.println(
                        "Department : "
                                + rs.getString("department"));

            } else {

                System.out.println(
                        "Employee not found.");
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter a valid employee ID.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // UPDATE EMPLOYEE
    // =====================================================

    public static void updateEmployee(
            Connection con,
            Scanner sc) {

        String sql = """
        UPDATE employees SET name = ?, email = ?, phone = ?, department = ? WHERE employee_id = ?
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Employee ID: ");

            int id =
                    Integer.parseInt(sc.nextLine());

            if (!Validation.isValidId(id)) {

                System.out.println("Invalid ID.");
                return;
            }

            System.out.print("Enter New Name: ");
            String name = sc.nextLine();

            System.out.print("Enter New Email: ");
            String email = sc.nextLine();

            System.out.print("Enter New Phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter New Department: ");
            String department = sc.nextLine();

            if (!Validation.isValidName(name)) {

                System.out.println("Invalid name.");
                return;
            }

            if (!Validation.isValidEmail(email)) {

                System.out.println("Invalid email.");
                return;
            }

            if (!Validation.isValidPhone(phone)) {

                System.out.println("Invalid phone.");
                return;
            }

            if (!Validation.isValidDepartment(department)) {

                System.out.println("Invalid department.");
                return;
            }

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, department);
            ps.setInt(5, id);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Employee updated successfully.");

            } else {

                System.out.println(
                        "Employee not found.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            System.out.println(
                    "Email or phone already exists.");

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter a valid employee ID.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // DELETE EMPLOYEE
    // =====================================================

    public static void deleteEmployee(Connection con, Scanner sc) {

        String deleteLeaveRequests = """
        DELETE FROM leave_requests WHERE employee_id = ?
        """;

        String deleteLeaveBalance = """
        DELETE FROM leave_balance WHERE employee_id = ?
        """;

        String deleteEmployee = """
        DELETE FROM employees WHERE employee_id = ?
        """;

        try {

            System.out.print("Enter Employee ID: ");

            int id = Integer.parseInt(sc.nextLine());

            if (!Validation.isValidId(id)) {
                System.out.println("Invalid Employee ID.");
                return;
            }

            con.setAutoCommit(false);

            // Delete leave requests

            try (PreparedStatement ps =
                         con.prepareStatement(deleteLeaveRequests)) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Delete leave balance

            try (PreparedStatement ps =
                         con.prepareStatement(deleteLeaveBalance)) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Delete employee

            try (PreparedStatement ps =
                         con.prepareStatement(deleteEmployee)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    con.commit();

                    System.out.println(
                            "Employee deleted successfully.");

                } else {

                    con.rollback();

                    System.out.println(
                            "Employee not found.");
                }
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter a valid Employee ID.");

        } catch (SQLException e) {

            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {

            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // =====================================================
    // APPLY LEAVE
    // =====================================================

    public static void applyLeave(
            Connection con,
            Scanner sc) {

        String sql = """
        INSERT INTO leave_requests (employee_id, leave_type, days, status) VALUES (?, ?, ?, 'PENDING')
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Employee ID: ");

            int employeeId =
                    Integer.parseInt(sc.nextLine());

            if (!Validation.isValidId(employeeId)) {

                System.out.println("Invalid ID.");
                return;
            }

            // CHECK EMPLOYEE

            String employeeSql = """
            SELECT employee_id FROM employees WHERE employee_id = ?
            """;

            try (PreparedStatement check =
                         con.prepareStatement(employeeSql)) {

                check.setInt(1, employeeId);

                ResultSet rs =
                        check.executeQuery();

                if (!rs.next()) {

                    System.out.println(
                            "Employee does not exist.");

                    return;
                }
            }

            System.out.print(
                    "Enter Leave Type (CASUAL/SICK/EARNED/ANNUAL): ");

            String leaveType =
                    sc.nextLine().toUpperCase();

            if (!Validation.isValidLeaveType(
                    leaveType)) {

                System.out.println(
                        "Invalid leave type.");

                return;
            }

            System.out.print(
                    "Enter Number of Days: ");

            int days =
                    Integer.parseInt(sc.nextLine());

            if (!Validation.isValidDays(days)) {

                System.out.println(
                        "Days must be between 1 and 30.");

                return;
            }

            // CHECK BALANCE

            String balanceSql = """
            SELECT remaining_leave FROM leave_balance WHERE employee_id = ?
            """;

            try (PreparedStatement balancePs =
                         con.prepareStatement(balanceSql)) {

                balancePs.setInt(1, employeeId);

                ResultSet rs =
                        balancePs.executeQuery();

                if (rs.next()) {

                    int remaining =
                            rs.getInt("remaining_leave");

                    if (days > remaining) {

                        System.out.println(
                                "Insufficient leave balance.");

                        System.out.println(
                                "Available Leave: "
                                        + remaining);

                        return;
                    }

                } else {

                    System.out.println(
                            "Leave balance not found.");

                    return;
                }
            }

            ps.setInt(1, employeeId);
            ps.setString(2, leaveType);
            ps.setInt(3, days);

            ps.executeUpdate();

            System.out.println(
                    "Leave applied successfully.");

            System.out.println(
                    "Status: PENDING");

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter valid numeric values.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // APPROVE LEAVE - TRANSACTION
    // =====================================================

    public static void approveLeave(
            Connection con,
            Scanner sc) {

        try {

            // START TRANSACTION

            con.setAutoCommit(false);

            System.out.print("Enter Leave ID: ");

            int leaveId =
                    Integer.parseInt(sc.nextLine());

            // GET LEAVE REQUEST

            String leaveSql = """
            SELECT employee_id, days, status FROM leave_requests WHERE leave_id = ?
            """;

            int employeeId;
            int days;
            String status;

            try (PreparedStatement ps =
                         con.prepareStatement(leaveSql)) {

                ps.setInt(1, leaveId);

                ResultSet rs =
                        ps.executeQuery();

                if (!rs.next()) {

                    System.out.println(
                            "Leave request not found.");

                    con.rollback();
                    return;
                }

                employeeId =
                        rs.getInt("employee_id");

                days =
                        rs.getInt("days");

                status =
                        rs.getString("status");
            }

            // CHECK STATUS

            if (!status.equalsIgnoreCase("PENDING")) {

                System.out.println(
                        "Leave is already "
                                + status);

                con.rollback();
                return;
            }

            // GET BALANCE

            String balanceSql = """
            SELECT used_leave, remaining_leave FROM leave_balance WHERE employee_id = ?
            """;

            int usedLeave;
            int remainingLeave;

            try (PreparedStatement ps =
                         con.prepareStatement(balanceSql)) {

                ps.setInt(1, employeeId);

                ResultSet rs =
                        ps.executeQuery();

                if (!rs.next()) {

                    System.out.println(
                            "Leave balance not found.");

                    con.rollback();
                    return;
                }

                usedLeave =
                        rs.getInt("used_leave");

                remainingLeave =
                        rs.getInt("remaining_leave");
            }

            // CHECK REMAINING BALANCE

            if (days > remainingLeave) {

                System.out.println(
                        "Insufficient leave balance.");

                con.rollback();
                return;
            }

            // UPDATE LEAVE STATUS

            String updateLeave = """
            UPDATE leave_requests SET status = 'APPROVED' WHERE leave_id = ?
            """;

            try (PreparedStatement ps =
                         con.prepareStatement(updateLeave)) {

                ps.setInt(1, leaveId);

                ps.executeUpdate();
            }

            // CALCULATE BALANCE

            int newUsed =
                    usedLeave + days;

            int newRemaining =
                    remainingLeave - days;

            // UPDATE BALANCE

            String updateBalance = """
            UPDATE leave_balance SET used_leave = ?, remaining_leave = ? WHERE employee_id = ?
            """;

            try (PreparedStatement ps =
                         con.prepareStatement(updateBalance)) {

                ps.setInt(1, newUsed);
                ps.setInt(2, newRemaining);
                ps.setInt(3, employeeId);

                ps.executeUpdate();
            }

            // COMMIT

            con.commit();

            System.out.println(
                    "Leave approved successfully.");

            System.out.println(
                    "Used Leave: " + newUsed);

            System.out.println(
                    "Remaining Leave: "
                            + newRemaining);

        } catch (Exception e) {

            try {

                con.rollback();

                System.out.println(
                        "Transaction rolled back.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {

            try {

                con.setAutoCommit(true);

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // REJECT LEAVE
    // =====================================================

    public static void rejectLeave(
            Connection con,
            Scanner sc) {

        String sql = """
        UPDATE leave_requests SET status = 'REJECTED' WHERE leave_id = ? AND status = 'PENDING'
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Leave ID: ");

            int leaveId =
                    Integer.parseInt(sc.nextLine());

            ps.setInt(1, leaveId);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Leave rejected successfully.");

                System.out.println(
                        "Leave balance remains unchanged.");

            } else {

                System.out.println(
                        "Leave not found or already processed.");
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter a valid Leave ID.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // VIEW LEAVE REQUESTS
    // =====================================================

    public static void viewLeaveRequests(
            Connection con) {

        String sql = """
        SELECT lr.leave_id, e.name, lr.leave_type, lr.days, lr.status FROM leave_requests lr JOIN employees e ON lr.employee_id = e.employee_id
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            System.out.println(
                    "\n========== LEAVE REQUESTS ==========");

            while (rs.next()) {

                System.out.println(
                        "Leave ID   : "
                                + rs.getInt("leave_id"));

                System.out.println(
                        "Employee   : "
                                + rs.getString("name"));

                System.out.println(
                        "Leave Type : "
                                + rs.getString("leave_type"));

                System.out.println(
                        "Days       : "
                                + rs.getInt("days"));

                System.out.println(
                        "Status     : "
                                + rs.getString("status"));

                System.out.println(
                        "-----------------------------------");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // VIEW ONE LEAVE BALANCE
    // =====================================================

    public static void viewLeaveBalance(
            Connection con,
            Scanner sc) {

        String sql = """
        SELECT e.name, lb.total_leave, lb.used_leave, lb.remaining_leave FROM leave_balance lb JOIN employees e ON lb.employee_id = e.employee_id WHERE lb.employee_id = ?
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Employee ID: ");

            int employeeId =
                    Integer.parseInt(sc.nextLine());

            ps.setInt(1, employeeId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                System.out.println(
                        "\n========== LEAVE BALANCE ==========");

                System.out.println(
                        "Employee        : "
                                + rs.getString("name"));

                System.out.println(
                        "Total Leave     : "
                                + rs.getInt("total_leave"));

                System.out.println(
                        "Used Leave      : "
                                + rs.getInt("used_leave"));

                System.out.println(
                        "Remaining Leave : "
                                + rs.getInt("remaining_leave"));

            } else {

                System.out.println(
                        "Leave balance not found.");
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Enter a valid Employee ID.");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // VIEW ALL LEAVE BALANCES
    // =====================================================

    public static void viewAllLeaveBalances(
            Connection con) {

        String sql = """
        SELECT e.employee_id, e.name, lb.total_leave, lb.used_leave, lb.remaining_leave FROM employees e JOIN leave_balance lb ON e.employee_id = lb.employee_id
        """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            System.out.println(
                    "\n========== ALL LEAVE BALANCES ==========");

            while (rs.next()) {

                System.out.println(
                        "Employee ID   : "
                                + rs.getInt("employee_id"));

                System.out.println(
                        "Name          : "
                                + rs.getString("name"));

                System.out.println(
                        "Total Leave   : "
                                + rs.getInt("total_leave"));

                System.out.println(
                        "Used Leave    : "
                                + rs.getInt("used_leave"));

                System.out.println(
                        "Remaining     : "
                                + rs.getInt("remaining_leave"));

                System.out.println(
                        "----------------------------------------");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}