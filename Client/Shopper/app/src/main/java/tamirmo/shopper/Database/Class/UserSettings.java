package tamirmo.shopper.Database.Class;

public class UserSettings {

    private boolean toVibrate;
    private boolean toSound;
    private boolean toNotify;

    public UserSettings(){
        toVibrate = true;
        toSound = true;
        toNotify = true;
    }

    public boolean getToNotify(){
        return toNotify;
    }

    public boolean getToVibrate(){
        return toVibrate;
    }

    public boolean getToSound(){
        return toSound;
    }

    public void setToNotify(boolean toNotify) {
        this.toNotify = toNotify;
    }

    public void setToVibrate(boolean toVibrate) {
        this.toVibrate = toVibrate;
    }

    public void setToSound(boolean toSound) {
        this.toSound = toSound;
    }
}
