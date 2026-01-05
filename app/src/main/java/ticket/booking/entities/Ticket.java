package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; //ignore certain fields when converting objects to json
import com.fasterxml.jackson.databind.PropertyNamingStrategy;//defines how field name in java should be mapped to json names
import com.fasterxml.jackson.databind.annotation.JsonNaming;// importing JsonNaming annotation from the Jackson library -> it tells Jackson how to name properties when converting between java objects to json
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)// converts ticketId to ticket_Id
public class Ticket{

    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private String dateOfTravel;
    private Train train;

    public Ticket(){}//default constructor

    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, Train train){
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s", ticketId, userId, source, destination, dateOfTravel);
    }

    public String getTicketId(){
        return ticketId;
    }
    public void setTicketId(String ticketId){
        this.ticketId = ticketId;
    }

    public String getSource(){
        return source;
    }
    public void setSource(String source){
        this.source = source;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination){
        this.destination = destination;
    }

    public String getDateOfTravel(){
        return dateOfTravel;
    }
    public void setDateOfTravel(String dateOfTravel){
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain(){
        return train;
    }
    public void setTrain(Train train){
        this.train = train;
    }

}