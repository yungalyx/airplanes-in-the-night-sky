import java.util.ArrayList;


public class FlightSystem {

    private ArrayList<Flight> flights;
    private int NumberOfLikes;

    public FlightSystem() {
        this.flights = new ArrayList();
        this.GenerateFlights();
        this.NumberOfLikes = 0;
    }

    public void GenerateFlights() {
        this.flights.add(new Flight(1, "PRG", "SIN", new DateTime("20-05-2019-10-40"), 600.55, 50));
        this.flights.add(new Flight(2, "SIN", "FRA", new DateTime("22-05-2019-06-00"), 500, 45));
        this.flights.add(new Flight(3, "SIN", "DXB", new DateTime("20-05-2019-18-40"), 500, 45));
        this.flights.add(new Flight(4, "PRG", "SIN", new DateTime("13-05-2019-11-20"), 555.40, 100));
    }

    public ArrayList<Flight> QueryFlightsBySourceAndDest(String source, String dest) {
        ArrayList<Flight> result = new ArrayList();
        for (Flight item : this.flights) {
            if (item.getSource().equalsIgnoreCase(source) && item.getDestination().equalsIgnoreCase(dest)) {
                result.add(item);
            }
        }
        return result;

    }

    public Flight QueryFlightByID(int id) {

        for (Flight item : this.flights) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public int QueryNumberOfFlights() {

        return this.flights.size();
    }

    public int GiveUsALike() {
        this.NumberOfLikes += 1;
        return this.NumberOfLikes;
    }

    public boolean MakeAnReservation(int flightID, int numberOfSeats) {

        boolean success = false;
        for (int i = 0; i < this.flights.size(); i++) {

            if (this.flights.get(i).getId() == flightID && this.flights.get(i).CheckSeatAvailabilityReservation(numberOfSeats) == true) {
                this.flights.get(i).UpdateAvailabilityReservation(numberOfSeats);
                success = true;
                break;
            }
        }

        return success;
    }

}
