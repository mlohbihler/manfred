package lohbihler.manfred.nmea.message;

public class PMTK001 extends NmeaMessage {
    public static enum CommandResponse {
        invalid, //
        unsupported, //
        failed, //
        success,
    }

    private final String command;
    private final CommandResponse response;

    PMTK001(String[] parts) {
        command = parts[0];
        response = CommandResponse.values()[Integer.parseInt(parts[1])];
    }

    @Override
    public String getNmeaMessageType() {
        return "PMTK001";
    }

    public String getCommand() {
        return command;
    }

    public CommandResponse getResponse() {
        return response;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        result = prime * result + ((response == null) ? 0 : response.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PMTK001 other = (PMTK001) obj;
        if (command == null) {
            if (other.command != null)
                return false;
        }
        else if (!command.equals(other.command))
            return false;
        if (response != other.response)
            return false;
        return true;
    }
}
