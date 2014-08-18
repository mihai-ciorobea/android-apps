package org.mihigh.cycling.app.group.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JoinedRide implements Serializable {

    public String name;
    public Date startDate;
    public JoinedStatus joinedStatus;
    public List<Pair<Double, Double>> track;

    public enum JoinedStatus {
        MINE, PENDING, ACCEPTED
    }

    ;

    public JoinedRide() {
    }

    public JoinedRide(String name, Date startDate, JoinedStatus joinedStatus, List<Pair<Double, Double>> track) {
        this.name = name;
        this.startDate = startDate;
        this.joinedStatus = joinedStatus;
        this.track = track;
    }
}



