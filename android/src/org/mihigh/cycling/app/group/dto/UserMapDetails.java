package org.mihigh.cycling.app.group.dto;

import java.io.Serializable;
import java.util.List;

public class UserMapDetails implements Serializable {

    public User user;
    public JoinedRide.JoinedStatus joinedStatus;
    public ProgressStatus progressStatus;
    public List<Coordinates> coordinates;

}
