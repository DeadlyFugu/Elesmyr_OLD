package net.halite.hbt;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 29/03/13
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class HBTDouble extends HBTTag {
    private double data;
    public HBTDouble(String name, double data) {
        super(name);
        this.data=data;
    }

    @Override
    public String toString() {
        return "Double "+getName()+": "+data;
    }

    public double getData() {
        return data;
    }
}
