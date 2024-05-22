package de.milschschnitte.crunchytransmitter.reciever;

public enum EnumWeekdays {
    MONDAY("Montag"),
    TUESDAY("Dienstag"),
    WEDNESDAY("Mittwoch"),
    THURSDAY("Donnerstag"),
    FRIDAY("Freitag"),
    SATURDAY("Samstag"),
    SUNDAY("Sonntag");

    private final String germanName;

    EnumWeekdays(String germanName) {
        this.germanName = germanName;
    }

    public String getGermanName() {
        return germanName;
    }

    public static EnumWeekdays fromGermanName(String germanName) {
        // Entfernen der zusätzlichen Anführungszeichen, falls vorhanden
        if (germanName.startsWith("\"") && germanName.endsWith("\"")) {
            germanName = germanName.substring(1, germanName.length() - 1);
        }
        
        for (EnumWeekdays day : values()) {
            if (day.getGermanName().equalsIgnoreCase(germanName)) {
                return day;
            }
        }
        return null;
    }
}
