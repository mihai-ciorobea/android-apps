package org.mihigh.cycling.app.pe.group.details.settings;

import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

public class RequestData {
    UserInfo userInfo;
    PEGroupDetails groupDetails;

    public RequestData(PEGroupDetails groupDetails, UserInfo user) {
        this.groupDetails = groupDetails;
        userInfo = user;
    }
}
