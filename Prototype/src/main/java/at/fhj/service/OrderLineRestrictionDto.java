package at.fhj.service;

public class OrderLineRestrictionDto {
    private boolean tollLocked, skipTollCheck, skipLotCheck;
    private String lotNumber;

    public boolean isTollLocked() {
        return tollLocked;
    }

    public void setTollLocked(boolean tollLocked) {
        this.tollLocked = tollLocked;
    }

    public boolean isSkipTollCheck() {
        return skipTollCheck;
    }

    public void setSkipTollCheck(boolean skipTollCheck) {
        this.skipTollCheck = skipTollCheck;
    }

    public boolean isSkipLotCheck() {
        return skipLotCheck;
    }

    public void setSkipLotCheck(boolean skipLotCheck) {
        this.skipLotCheck = skipLotCheck;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
}
