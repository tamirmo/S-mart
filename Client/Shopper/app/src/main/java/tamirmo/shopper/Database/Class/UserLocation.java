package tamirmo.shopper.Database.Class;

public class UserLocation {

    private int locationX;
    private int locationY;

    public UserLocation(){
        locationX = 0;
        locationY = 0;
    }

    public void setLocationX(int locationX){
        this.locationX = locationX;
    }

    public void setLocationY(int locationY){
        this.locationY = locationY;
    }

    public int getLocationX(){
        return locationX;
    }

    public int getLocationY(){
        return locationY;
    }
}
