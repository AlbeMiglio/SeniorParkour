package it.albemiglio.seniorparkour.objects;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentParkour {

    private String parkourName;
    private long startingTime;

    private int checkpoint;

    public CurrentParkour(String parkourName, long time) {
        this.parkourName = parkourName;
        this.startingTime = time;
    }
}
