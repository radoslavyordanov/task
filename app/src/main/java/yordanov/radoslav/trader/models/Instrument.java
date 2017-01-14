package yordanov.radoslav.trader.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import yordanov.radoslav.trader.AppDatabase;

@Table(database = AppDatabase.class)
public class Instrument extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id; // package-private recommended, not required

    @Column
    private String name; // private with getters and setters

    @Column
    private double lowestPrice;

    @Column
    private double highestPrice;

    @Column
    private int decimalNumbers;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public int getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers(int decimalNumbers) {
        this.decimalNumbers = decimalNumbers;
    }
}
