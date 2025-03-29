package project;

public enum StudentStatus {
    ACTIVE, INACTIVE, GRADUATED;

    @Override
    public String toString() {
        return switch (this) {
            case ACTIVE -> LanguageSupport.getString("status.active");
            case INACTIVE -> LanguageSupport.getString("status.inactive");
            case GRADUATED -> LanguageSupport.getString("status.graduated");
        };
    }
}
