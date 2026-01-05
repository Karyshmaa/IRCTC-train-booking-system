package ticket.booking.services;

import com.fasterxml.jackson.core.JsonParser;//reads json content token by  token
import com.fasterxml.jackson.core.type.TypeReference;//helps json deal with generic types
import com.fasterxml.jackson.databind.ObjectMapper;//main class of jackson used for serialization and deserialization

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.*;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;


public class UserBookingService {
    
    public User user;// stores current user logged in or being handled
    private List<User> userList;// list of all users fetched from local Json database
    private ObjectMapper objectMapper = new ObjectMapper();//to map values to the classes like train,ticket and user we use jackson object mapper
    private static final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    //constructor called when creating a service with a specific user
    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    //loads user list used for searching trains or fetching data without a logged-in user
    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    //converts json array into list<user>
    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }

    //method to check if the current user exists in userList and has the correct password
    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 ->{
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
           }).findFirst();
           return foundUser.isPresent();
    }


     public Boolean signUp(User user1){
        try{
            userList.add(user1); //adds new user to the list
            saveUserListToFile(); //saves back to json file using saveUserListToFile()
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
     }


    //Ensuring persistence -> data is saved permanently in JSON
     private void saveUserListToFile() throws IOException{
        File userFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(userFile, userList);
     }


     //json --> Java Object (User) --> Deserialize
    //Java Object (User) --> json --> Serialize


    public void fetchBookings() {
        Optional<User> userFetched = userList.stream()
                .filter(user1 -> user1.getName().equals(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword()))
                .findFirst();

        if (userFetched.isPresent()) {
            userFetched.get().printTickets();
        } else {
            System.out.println("No bookings found or user authentication failed.");
        }
    }



    public Boolean cancelBooking(String ticketId){

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable, therefore finalTicketId1 is just a copy
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }


    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }


    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }


    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }
}