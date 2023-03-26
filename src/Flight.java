/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author
 */
public class Flight {

    private int id;
    private String source;
    private String destination;
    private DateTime departure_time;
    private double airfare;
    private int number_of_seats_available;

    Flight(int i, String prg, String sin, DateTime dateTime, double d, int i0) {
       this.id=i;
       this.source=prg;
       this.destination=sin;
       this.departure_time=dateTime;
       this.airfare=d;
       this.number_of_seats_available=i0;
       
               
    }

    public void UpdateAvailabilityReservation(int n_of_seats_to_reserve) {
        this.setNumber_of_seats_available(this.getNumber_of_seats_available() - n_of_seats_to_reserve);
    }

    public boolean CheckSeatAvailabilityReservation(int n_of_seats_to_reserve) {
        if (n_of_seats_to_reserve <= this.getNumber_of_seats_available()) {
            return true;
        } else {
            return false;
        }
    }

    public String Get_printable_string() {
        String result = "----------------------------------------------------------------------------------------------------\n";
        result += "Flight " + this.getId() + " from " + this.getSource() + " to " + this.getDestination() + " will be departing on " + this.getDeparture_time().Get_printable_string() + "\n";
        result += "Airfare: " + this.getAirfare() + "\n";
        result += "Number of seats available: " + this.getNumber_of_seats_available() + '\n';
        return result;
    }

    @Override
    public String toString() {
        return this.getId() + "," + this.getSource() + "," + this.getDestination() + "," + this.getDeparture_time().toString() + "," + this.getAirfare() + "," + this.getNumber_of_seats_available();

    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDest(String destination) {
        this.destination = destination;
    }

    /**
     * @return the departure_time
     */
    public DateTime getDeparture_time() {
        return departure_time;
    }

    /**
     * @param departure_time the departure_time to set
     */
    public void setDeparture_time(DateTime departure_time) {
        this.departure_time = departure_time;
    }

    /**
     * @return the airfare
     */
    public double getAirfare() {
        return airfare;
    }

    /**
     * @param airfare the airfare to set
     */
    public void setAirfare(double airfare) {
        this.airfare = airfare;
    }

    /**
     * @return the number_of_seats_available
     */
    public int getNumber_of_seats_available() {
        return number_of_seats_available;
    }

    /**
     * @param number_of_seats_available the number_of_seats_available to set
     */
    public void setNumber_of_seats_available(int number_of_seats_available) {
        this.number_of_seats_available = number_of_seats_available;
    }
}
