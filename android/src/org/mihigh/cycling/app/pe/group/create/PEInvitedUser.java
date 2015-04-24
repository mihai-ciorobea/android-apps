package org.mihigh.cycling.app.pe.group.create;

import java.io.Serializable;

public class PEInvitedUser implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;

    public String email;

    public PEInvitedUser(String email) {
        this.email = email;
    }

}