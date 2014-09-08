package org.mihigh.cycling.app.group.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JoinedRide implements Serializable {

    public long id;
    public String name;
    public User owner;
    public Date startDate;
    public JoinedStatus joinedStatus;
    public List<Coordinates> coordinates;


    public enum JoinedStatus {
        MINE, PENDING, ACCEPTED, DECLINED;
    };

    public JoinedRide() {
    }

}



