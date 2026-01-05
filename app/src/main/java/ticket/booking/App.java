package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        try {
            System.out.println("Running Train Booking System....");
            Scanner scanner = new Scanner(System.in);
            int option = 0;
            UserBookingService userBookingService;
            try {
                userBookingService = new UserBookingService();
            } catch (IOException ex) {
                System.out.println("Some error occurred. Please try again later");
                ex.printStackTrace();
                return;
            }

            User currentUser = null;

            while (option != 7) {
                System.out.println("\nChoose option:");
                System.out.println("1. Sign up");
                System.out.println("2. Login");
                System.out.println("3. Fetch Bookings");
                System.out.println("4. Search & Book Train");
                System.out.println("5. Cancel my Booking");
                System.out.println("6. Exit the App");

                try {
                    option = Integer.parseInt(scanner.nextLine()); // read full line and parse int
                } catch (NumberFormatException e) {
                    System.out.println("Invalid option. Try again.");
                    continue;
                }

                switch (option) {
                    case 1:
                        System.out.println("Enter the username to signup");
                        String nameToSignUp = scanner.nextLine();
                        System.out.println("Enter the password to signup");
                        String passwordToSignUp = scanner.nextLine();

                        User newUser = new User(
                                nameToSignUp,
                                passwordToSignUp,
                                UserServiceUtil.hashPassword(passwordToSignUp),
                                new ArrayList<>(),
                                UUID.randomUUID().toString()
                        );

                        if (userBookingService.signUp(newUser)) {
                            System.out.println("Signup successful!");
                        } else {
                            System.out.println("Signup failed. Try again.");
                        }
                        break;

                    case 2:
                        System.out.println("Enter the username to Login");
                        String nameToLogin = scanner.nextLine();
                        System.out.println("Enter the password to Login");
                        String passwordToLogin = scanner.nextLine();


                        User loginUser = new User(
                                nameToLogin,
                                passwordToLogin,
                                UserServiceUtil.hashPassword(passwordToLogin), // FIXED!
                                new ArrayList<>(),
                                ""
                        );

                        try {
                            UserBookingService tempService = new UserBookingService(loginUser);
                            if (tempService.loginUser()) {
                                currentUser = loginUser;
                                userBookingService = tempService;
                                System.out.println("Login successful!");
                            } else {
                                System.out.println("Invalid username or password.");
                            }
                        } catch (IOException e) {
                            System.out.println("Login failed due to IO error.");
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        if (currentUser != null) {
                            System.out.println("Fetching your bookings...");
                            userBookingService.fetchBookings();
                        } else {
                            System.out.println("Please login first!");
                        }
                        break;

                    case 4:
                        if (currentUser == null) {
                            System.out.println("Please login first!");
                            break;
                        }
                        System.out.println("Type your source station");
                        String source = scanner.nextLine();

                        System.out.println("Type your destination station");
                        String dest = scanner.nextLine();

                        List<Train> trains = userBookingService.getTrains(source, dest);
                        if (trains.isEmpty()) {
                            System.out.println("No trains found for the given route.");
                            break;
                        }
                        int idx = 1;
                        for (Train t : trains) {
                            System.out.println(idx + ". Train ID: " + t.getTrainId() + " Train No: " + t.getTrainNo());
                            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                                System.out.println("Station: " + entry.getKey() + " Time: " + entry.getValue());
                            }
                            idx++;
                        }

                        System.out.println("Select a train by typing its number:");
                        int trainChoice;
                        try {
                            trainChoice = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid train number.");
                            break;
                        }

                        if (trainChoice < 1 || trainChoice > trains.size()) {
                            System.out.println("Invalid train selection.");
                            break;
                        }

                        Train selectedTrain = trains.get(trainChoice - 1);

                        System.out.println("Available seats (0 = free, 1 = booked):");
                        List<List<Integer>> seats = userBookingService.fetchSeats(selectedTrain);
                        for (int i = 0; i < seats.size(); i++) {
                            System.out.print("Row " + i + ": ");
                            for (int val : seats.get(i)) {
                                System.out.print(val + " ");
                            }
                            System.out.println();
                        }

                        System.out.println("Enter row number to book:");
                        int row;
                        try {
                            row = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid row number.");
                            break;
                        }

                        System.out.println("Enter seat number in that row:");
                        int col;
                        try {
                            col = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid seat number.");
                            break;
                        }

                        if (userBookingService.bookTrainSeat(selectedTrain, row, col)) {
                            System.out.println("Seat booked successfully! Enjoy your journey.");
                        } else {
                            System.out.println("Cannot book this seat. It may be already booked or invalid.");
                        }
                        break;

                    case 5:
                        if (currentUser == null) {
                            System.out.println("Please login first!");
                            break;
                        }
                        System.out.println("Loading your bookings...");
                        String ticketId = scanner.nextLine();
                        userBookingService.cancelBooking(ticketId);
                        break;

                    case 6:
                        System.out.println("Exiting app...");
                        option = 7;
                        break;

                    default:
                        System.out.println("Invalid option. Try again.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred!");
            e.printStackTrace();
        }
    }
}