package project.event.model;

import lombok.Data;

@Data
public class LogoffAccountInfo {
    private String partyId;

    private String oriAccount;

    private String newAccount;

}
