    public class Car implements Comparable<Car>{
        public String VIN;
        public String make;
        public String model;
        public double mileage;
        public String Color;
        public double price;

        public Car(String vin){
            //default settings
            VIN = vin;
            make = "Ford";
            model = "Fiesta";
            mileage = 0;
            Color = "white";
            price = 20000;
        }

        public Car(String vin, String make, String model, double price, double mileage, String color){
            this.VIN = vin;
            this.make = make;
            this.model = model;
            this.mileage = mileage;
            this.Color = color;
            this.price = price;
        }

        public String toString(){
            return new String(VIN + " : " + " : " + make + " : " + model + " : " + mileage + " : " + Color + " : " + price);
        }
        
        public int compareTo(Car b){
            return this.make.compareTo(b.make);
        }
    }   
