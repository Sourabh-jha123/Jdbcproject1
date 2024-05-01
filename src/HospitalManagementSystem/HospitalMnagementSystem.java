package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalMnagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Jha@#123";
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGMENT SYSTEM");
                System.out.println(" 1. Add Patient");
                System.out.println(" 2. view patient");
                System.out.println(" 3. view doctors");
                System.out.println(" 4. Book Appointments");
                System.out.println(" 5. Exit");
                System.out.println("Enter your choice");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewpatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("THANK YOU! for using Hospital Management System");
                        System.out.println("Made By Sourabh Kumar Jha");
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter patient id:");
        int patientsid = scanner.nextInt();
        System.out.println("Enter doctor id");
        int doctorid = scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentsDate = scanner.next();
        if(patient.getPatientById(patientsid) && doctor.getDoctorById(doctorid)){
            if(checkDoctorAvailability(doctorid, appointmentsDate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patients_id, doctors_id, appointments_date)VALUES(?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientsid);
                    preparedStatement.setInt(2,doctorid);
                    preparedStatement.setString(3, appointmentsDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked");
                    }else{
                        System.out.println("Failed to book Appoinment");
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("Either patient or doctor doesn't Exist");
        }
    }
   public static boolean checkDoctorAvailability(int doctorsid, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctors_id = ? AND appointments_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorsid);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
   }
}
