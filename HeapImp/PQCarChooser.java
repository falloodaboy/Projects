import java.io.*;
import java.util.*;

public class PQCarChooser {
	private int n, countmain;
    private int maxN;
    private ArrayList<Integer> pqprice; //heap
    private ArrayList<Integer> qpprice; //indexable array.
    private ArrayList<Integer> pqmileage;
    private ArrayList<Integer> qpmileage;
    private ArrayList<Car> keys;
    private HashMap<String, Brand> brands;
    private HashMap<String, Integer> keymap; //keeps track of which index in keys maps to which VINS
   


    public PQCarChooser(int maxN){
        if(maxN <= 0){
            throw new IllegalArgumentException("integer provided to PriorityQueue is invalid");
        }
       
        this.maxN = maxN;
        n = 0;
        countmain = 0;
        pqprice = new ArrayList<>(2*maxN+1);
        qpprice = new ArrayList<>(2*maxN+1);
        pqmileage = new ArrayList<>(2*maxN + 1);
        qpmileage = new ArrayList<>(2*maxN + 1);
		keys = new ArrayList<>(maxN + 1);
        keymap = new HashMap<String, Integer>(2*(maxN+1), 0.50f);
        brands = new HashMap<String, Brand>(maxN+1, 0.50f);

        for(int i=0; i <= maxN; i++){
         //   System.out.println(i + " added");
            qpprice.add(i, new Integer(-1));
            qpmileage.add(i, new Integer(-1));
        }
        pqprice.add(0, null);
        pqmileage.add(0, null);
	}

    //set which uses method comparePrice for comparison
    private void swimPrice(int k) {
        while (k > 1 && greaterPrice(k/2, k)) {
            exch(pqprice, k, k/2);
            k = k/2;
        }
    }

    private void sinkPrice(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greaterPrice(j, j+1)) j++;
            if (!greaterPrice(k, j)) break;
            exch(pqprice, k, j);
            k = j;
        }
    }

    //set which uses method compareMileage for comparison.
    private void swimMileage(int k){
        while (k > 1 && greaterMileage(k/2, k)) {
            exch(pqmileage,k, k/2);
            k = k/2;
        }
    }

    private void sinkMileage(int k){
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greaterMileage(j, j+1)) j++;
            if (!greaterMileage(k, j)) break;
            exch(pqmileage, k, j);
            k = j;
        }
    }

    public Car getMinMileageCar(){
        if(n == 0){ 
            System.out.println("The Priority Queue is empty. Nothing to return.");
            return null;
           // throw new IllegalStateException("PriorityQueue is empty");
        }
        return keys.get(pqmileage.get(1).intValue());//keys[pqmileage.get(1)];
    }

    public Car getMinPriceCar(){
         if(n == 0){
            //throw new IllegalStateException("PriorityQueue is empty");
            System.out.println("The Priority Queue is empty. Nothing to return.");
            return null;
         }
            
        return keys.get(pqprice.get(1).intValue());//keys[pqprice.get(1)];
    }

    public Car getMinPriceCarMakeAndModel(String make, String model){
        if(brands.get(make) == null || brands.get(make).getModel(model) == null){
            System.out.println("This make and model is not found in the Priority Queue.");
            return null;
        }
        return brands.get(make).getModel(model).getMinPriceCar();
    }
    public Car getMinMileageCarMakeAndModel(String make, String model){
         if(brands.get(make) == null || brands.get(make).getModel(model) == null){
            System.out.println("This make and model is not found in the Priority Queue.");
            return null;
         }
          
        return brands.get(make).getModel(model).getMinMileageCar();
    }
    // public int delMin(){
    //     int minprice = qpprice[1];
    //     int minmileage = qpmileage[1];
    //     exch(pqprice, 1, n);
    //     exch(pqmileage, 1, n);
    //     n--;
    //     sinkPrice(1);
    //     sinkMileage(1);
    //     keys[minprice] = null; //no need to access keys[minmileage] as it should be the same index
    //     qpprice[minprice] = -1;
    //     qpmileage[minmileage] = -1;

    // }

    private boolean greaterMileage(int k, int v) { 
        return keys.get(pqmileage.get(k).intValue()).mileage > keys.get(pqmileage.get(v).intValue()).mileage;
    }
     private boolean greaterPrice(int k, int v) { 
        return keys.get(pqprice.get(k).intValue()).price > keys.get(pqprice.get(v).intValue()).price;
    }

    private void exch(ArrayList<Integer> arr, int i, int j){ 
        //need to define this method for other class objects
        //need to be careful with this function. 
        //Exchange needs to keep track of all data structures which are affected by the min heaps so as to ensure consistency.

        if(arr.equals(pqprice)){
            //swap pqprice values i and j
            //swap indices in qp for i and j
            //swap index values of the cars kept in their respective CarObjects.
            int swap = pqprice.get(i).intValue();
            pqprice.set(i, new Integer(pqprice.get(j).intValue()));
            pqprice.set(j, new Integer(swap));
            qpprice.set(pqprice.get(i).intValue(), new Integer(i)); //[pqprice[i]] = i;
            qpprice.set(pqprice.get(j).intValue(), new Integer(j));//[pqprice[j]] = j;

            //swap CarObject indices so that they are consistent with their respective heaps.
            Car carx = keys.get(pqprice.get(i).intValue());//[pqprice[i]]; //<-- pqprice takes an index in the heap and returns the value v for a specific key which can then be accessed by the keys array
         //   assert keymap.get(carx.VIN).intValue() == pqprice[i];
            Car cary = keys.get(pqprice.get(j).intValue());//[pqprice[j]]; 
            Model ex = brands.get(carx.make).getModel(carx.model);//should it be set to i? I think so as qp[pq[i]] = i.
            Model ey = brands.get(cary.make).getModel(cary.model);
            ex.cars.get(ex.carmap.get(carx.VIN).intValue()).indexprice = i;//ex.cars[ex.carmap.get(carx.VIN).intValue()].indexprice = i;
            if(ey.carmap.get(cary.VIN) != null)
                ey.cars.get(ey.carmap.get(cary.VIN).intValue()).indexprice = j;//ey.cars.[ey.carmap.get(cary.VIN).intValue()].indexprice = j;
            
            else{
                System.out.println("carx is : " + carx.VIN + " " + carx.make + " " + carx.model);
                System.out.println("cary model does not have this car: " + cary.VIN + " "  + cary.make + " " + cary.model);
            }
        }
        else if(arr.equals(pqmileage)){
            int swap = pqmileage.get(i).intValue();//pqmileage[i];
            pqmileage.set(i, new Integer(pqmileage.get(j).intValue()));//pqmileage[i] = pqmileage[j];
            pqmileage.set(j, new Integer(swap));// pqmileage[j] = swap;
            qpmileage.set(pqmileage.get(i).intValue(), new Integer(i));//qpmileage[pqmileage[i]] = i;
            qpmileage.set(pqmileage.get(j).intValue(), new Integer(j));//qpmileage[pqmileage[j]] = j;
           
            //swap CarObject indices so that they are consistent with their respective heaps.
            Car carx = keys.get(pqmileage.get(i));//keys[pqmileage[i]]; //<-- pqprice takes an index in the heap and returns the value v for a specific key which can then be accessed by the keys array
            Car cary = keys.get(pqmileage.get(j));//keys[pqmileage[j]]; 
            Model ex = brands.get(carx.make).getModel(carx.model);//should it be set to i? I think so as qp[pq[i]] = i.
            Model ey = brands.get(cary.make).getModel(cary.model);
            ex.cars.get(ex.carmap.get(carx.VIN).intValue()).indexmileage = i;//ex.cars[ex.carmap.get(carx.VIN).intValue()].indexmileage = i;
            ey.cars.get(ey.carmap.get(cary.VIN).intValue()).indexmileage = j;//ey.cars[ey.carmap.get(cary.VIN).intValue()].indexmileage = j;
        }
        else{
            System.out.println("Wrong ArrayList used for this exchange.");
            //throw new RuntimeException("Invalid int[] arr used for exch function in PriorityQueue");
        }
    }

    public void remove(String vin){
        // if(!validateVIN(vin)){
        //     throw new RuntimeException("car VIN is not valid. ValidateVIN() returned false either because vin is not in keymap or index is not in the desired range.");
        // } 
        if(!contains(vin)){
            System.out.println(vin + " is not in this data structure.");
           // throw new IllegalArgumentException("this vin is not in the heap");
            return;
        }
        
        int indexprice = qpprice.get(keymap.get(vin).intValue()).intValue();//qpprice[keymap.get(vin).intValue()];
        int indexmileage = qpmileage.get(keymap.get(vin).intValue()).intValue();//qpmileage[keymap.get(vin).intValue()];
        exch(pqprice, indexprice, n);
        exch(pqmileage, indexmileage, n);
        n--;
        
      
        swimPrice(indexprice);
        sinkPrice(indexprice);
        swimMileage(indexmileage);
        sinkMileage(indexmileage);
        
        Car yeet = keys.get(keymap.get(vin).intValue());//keys[keymap.get(vin).intValue()];
      //System.out.println("remove function is deleting: " + yeet.VIN);
        brands.get(yeet.make).getModel(yeet.model).delete(vin);
        
        keys.set(keymap.get(vin).intValue(), null);//keys[keymap.get(vin).intValue()] = null;
        qpprice.set(keymap.get(vin).intValue(), new Integer(-1));//qpprice[keymap.get(vin).intValue()] = -1;
        qpmileage.set(keymap.get(vin).intValue(), new Integer(-1));//qpmileage[keymap.get(vin).intValue()] = -1;
        keymap.replace(vin, null);
    }


    public void emptyHeap(){
        System.out.println("Printing Prices Queue");
        
        for(int i=1; i <= n; i++){
          System.out.println(keys.get(pqprice.get(i).intValue()).VIN + " price: " + keys.get(pqprice.get(i).intValue()).price); // System.out.println(keys[pqprice[i]].VIN + " price: " + keys[pqprice[i]].price);
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Printing Mileage Queue");
        
        for(int i=1; i <= n; i++){
           // System.out.println(keys[pqmileage[i]].VIN + " mileage: " + keys[pqmileage[i]].mileage);
            System.out.println(keys.get(pqmileage.get(i).intValue()).VIN + " mileage: " + keys.get(pqmileage.get(i).intValue()).mileage);
        }
    }


    public void printAllModels(){
        Iterator<Brand> iterbrand = brands.values().iterator();
        while(iterbrand.hasNext()){
            iterbrand.next().printModels();
        }
    }



    public void add(Car car){
        if(contains(car.VIN)){
            //throw new IllegalArgumentException("Car vin already exists in heap.");
            System.out.println("This car is already in the Priority Queue.");
            return;
        }

        if(brands.get(car.make) == null){ //addresses nonexistence of brand in this data structure
           // System.out.println("added make: " + car.make);
            brands.put(car.make, new Brand(car.make));
        }
        if(brands.get(car.make).getModel(car.model) == null){ //addresses nonexistence of model type in this brand
          //  System.out.println("added model: " + car.model);
            brands.get(car.make).addModel(car.model, new Model(car.model, maxN));
        }
       
        n++;
        keys.add(countmain, car);//keys[countmain] = car;
        keymap.put(car.VIN, new Integer(countmain));
        qpprice.add(countmain, new Integer(n));//qpprice[countmain] = n;
        qpmileage.add(countmain, new Integer(n));//qpmileage[countmain] = n;
        pqprice.add(n, countmain);//pqprice[n] = countmain;
        pqmileage.add(n, countmain);//pqmileage[n] = countmain;
        brands.get(car.make).getModel(car.model).insert(new CarObject(car, qpprice.get(countmain).intValue(), qpmileage.get(countmain).intValue()));
        this.swimPrice(n);
        this.swimMileage(n);
       

        countmain++;
    }
    

    public int size(){
        return n;
    }

    public boolean contains(String vin){
        return keymap.get(vin) != null;
    }
  
    //y is the mode. if y == p, then update price, if y == m, update mileage. if y==c, update color.
    public void update(String vin, char y, double number, String color){ 
        if(y == 'p'){
            int heapindex = qpprice.get(keymap.get(vin).intValue()).intValue();//qpprice[keymap.get(vin).intValue()];
            Car k = keys.get(keymap.get(vin).intValue());//keys[keymap.get(vin).intValue()];
            k.price = number;
            brands.get(k.make).getModel(k.model).update(vin, y, number, color);
            swimPrice(heapindex);
            sinkPrice(heapindex);
        }
        else if(y == 'm'){
          int heapindex = qpmileage.get(keymap.get(vin).intValue()).intValue();//qpmileage[keymap.get(vin).intValue()];
          Car k = keys.get(keymap.get(vin).intValue());//keys[keymap.get(vin).intValue()];
          k.mileage = number;
          brands.get(k.make).getModel(k.model).update(vin, y, number, color);
          swimMileage(heapindex);
          sinkMileage(heapindex);
        }
        else if(y == 'c'){
            Car k = keys.get(keymap.get(vin).intValue());//keys[keymap.get(vin).intValue()];
            k.Color = color;
        }
        else {
            System.out.println("This char mode is not supported.");
         // throw new IllegalArgumentException("char y is not supported.");
        }
    }
    public boolean validateVIN(String vin){
        if(keymap.get(vin) != null){
            int index = keymap.get(vin).intValue();
            if(index >= 0 && index < maxN)
                return true;
            else
                System.out.println(keymap.get(vin).intValue());
        }
        System.out.println(keymap.get(vin));
        return false;
    }










    private class Brand implements Comparable<PQCarChooser.Brand> {
        HashMap<String, Model> models; //build hashmap using the comparator given in the constructor.
        String brandname;


        public Brand(String name){
            brandname = name;
            models = new HashMap<String, Model>(maxN + 1, 0.50f);
        }

        public int compareTo(Brand b){
            return this.brandname.compareTo(b.brandname);
        }


        public void addModel(String n, Model m){
            models.put(n, m);
        }

        public Model getModel(String n){
            return models.get(n);
        }

        public void printModels(){
                System.out.println("Brand: " + brandname);
                System.out.println();
                Iterator<Model> iter = models.values().iterator();
                while(iter.hasNext()){
                    iter.next().printCars();
                }
        }
    }

   









    private class Model implements Comparable<PQCarChooser.Model> {
      
        public ArrayList<Integer> pqprices;
        public ArrayList<Integer> qpprices;
        public ArrayList<Integer> pqmileages;
        public ArrayList<Integer> qpmileages;
        public HashMap<String, Integer> carmap; //keeps track of vins that map to indices in the cars array
        public ArrayList<CarObject> cars;
        public String name;
        public int n2, count;
        public int max;
        

        //probably don't need num as all model arrays will be resizable 
        //provide the size of the cars file to allow for each array containing the cars in the worst case. 
        public Model(String name, int num){ 
            this.name = name;
            max = num;
            pqprices = new ArrayList<>(num + 1);//int[num + 1];
            qpprices = new ArrayList<>(num + 1);//int[num + 1];
            pqmileages = new ArrayList<>(num + 1);//int[num + 1];
            qpmileages = new ArrayList<>(num + 1);//int[num + 1];
            cars = new ArrayList<>(num + 1);//CarObject[num + 1];
            carmap = new HashMap<String, Integer>(2*max+1, 0.50f);
            n2 = 0;
            count = 0;
           
            for(int i=0; i <= max; i++) {
                qpprices.add(i, new Integer(-1));//qpprices[i] = -1;
                qpmileages.add(i,new Integer(-1)); //qpmileages[i] = -1;
            }
            pqprices.add(0, null);
            pqmileages.add(0, null);
        }

        public void printCarMap(){
           Iterator<Integer> iter = carmap.values().iterator();
           System.out.println("Model: " + name);
           while(iter.hasNext()){
                int wow = iter.next().intValue();
                System.out.println(cars.get(wow).car.VIN + " " + "");
           }
        }

        public int compareTo(Model b){
            return this.name.compareTo(b.name);
        }

        public void swimPrices(int k){
            while (k > 1 && greaterPrices(k/2, k)) { //change comparison functions
                exchange(pqprices,k, k/2);
                k = k/2;
            }
        }

        public void sinkPrices(int k){ //change comparison functions
            while (2*k <= n2) {
                int j = 2*k;
                if (j < n2 && greaterPrices(j, j+1)) j++;
                if (!greaterPrices(k, j)) break;
                exchange(pqprices, k, j);
                k = j;
            }
        }

        public void swimMileages(int k){
            while (k > 1 && greaterMileages(k/2, k)) {//change comparison functions
                exchange(pqmileages, k, k/2);
                k = k/2;
            }
        }

        public void sinkMileages(int k){ //change comparison functions
            while (2*k <= n2) {
                int j = 2*k;
                if (j < n2 && greaterMileages(j, j+1)) j++;
                if (!greaterMileages(k, j)) break;
                exchange(pqmileages, k, j);
                k = j;
            }
        }
    
        public void insert(CarObject carob){
            if(contains(carob)){
                  System.out.println("this carob is already stored within this model: " + name);
               // throw new IllegalArgumentException("the CarObject: " + carob.car.VIN + " already exists in this model type");
            }
            n2++;
            carmap.put(carob.car.VIN, new Integer(count)); //add to carmap this carobject and its index in the cars array.
            cars.add(count, carob);//cars[count] = carob; //add to the array
            qpprices.add(count, new Integer(n2));//qpprices[count]= n2;
            qpmileages.add(count, new Integer(n2));//qpmileages[count] = n2;
            pqprices.add(n2, new Integer(count));//pqprices[n2] = count;
            pqmileages.add(n2, new Integer(count));//pqmileages[n2] = count;
            swimPrices(n2);
            swimMileages(n2);
          
            // System.out.println("VIN: " + carob.car.VIN + " "+ "Model: " + name);
            // System.out.println("carmap value: " + carmap.get(carob.car.VIN));
            // System.out.println("qpprices[count]: " + qpprices[carmap.get(carob.car.VIN).intValue()]);
            // System.out.println("qpmileages[count]" + qpmileages[carmap.get(carob.car.VIN).intValue()]);

            // System.out.println("Printing qpprices:");
            // for(int all = 0; all < qpprices.length; all++){
            //     System.out.print(qpprices[all] + " ");
            // }
            // System.out.println();
            // System.out.println("Printing qpmileages:");
            // for(int all = 0; all < qpmileages.length; all++){
            //     System.out.print(qpmileages[all] + " ");
            // }
            // System.out.println();
            count++;
        }
        //overall, runtime is O(6logn) >> O(logn)
        public void delete(String vin){

            // if(!validateVINs(vin)){
            //     System.out.println("keymap at vin: " + vin +" : " + keymap.get(vin));
            //     throw new IllegalArgumentException("not a valid argument");
            // }
            if(!contains(cars.get(carmap.get(vin).intValue())/*cars[carmap.get(vin).intValue()]*/)){
                System.out.println("this vin is not found within this data structure.");
               // throw new IllegalArgumentException("the carobject: " + cars.get(carmap.get(vin).intValue()).car.VIN/*cars[carmap.get(vin).intValue()].car.VIN*/ + " is not in this pq");
            }
            
            int keyay = carmap.get(vin).intValue(); //gets the index in cars associated with this VIN.
            // System.out.println("Carmap value: " + carmap.get(vin));
            // System.out.println("qpprices[keyay]: " + qpprices[keyay]);
            // System.out.println("qpmileages[keyay]: " + qpmileages[keyay]);

            // System.out.println("Printing qpprices:");
            // for(int all = 0; all < qpprices.length; all++){
            //     System.out.print(qpprices[all] + " ");
            // }
            // System.out.println();
            // System.out.println("Printing qpmileages:");
            // for(int all = 0; all < qpmileages.length; all++){
            //     System.out.print(qpmileages[all] + " ");
            // }
            // System.out.println();
            // System.out.println("Printing pqmileages");
            // for(int all =0; all < pqmileages.length; all++){
            //     System.out.print(pqmileages[all] + " ");
            // }
            // System.out.println();

            // System.out.println("Printing pqprices");
            // for(int all =0; all < pqprices.length; all++){
            //     System.out.print(pqprices[all] + " ");
            // }
            // System.out.println();


            // System.out.println("Printing cars");

            // for(int all = 0; all < cars.length; all++){
            //     if(cars[all] != null)
            //         System.out.print("Y ");
            //     else
            //         System.out.print("null ");
            // }
            // System.out.println();

            int index1 = qpprices.get(carmap.get(vin).intValue()).intValue();//qpprices[carmap.get(vin).intValue()]; //can be different than index2.
            int index2 = qpmileages.get(carmap.get(vin).intValue()).intValue();//qpmileages[carmap.get(vin).intValue()];//can be different than index1.
            exchange(pqprices, index1, n2); //n2 here indicates the last element of the heap array.
            exchange(pqmileages, index2, n2);
            n2--;

            //System.out.println("index1: " + index1 + " index2: " + index2);

            swimPrices(index1);
            sinkPrices(index1);
            swimMileages(index2);
            sinkMileages(index2);
           
            qpprices.set(keyay, new Integer(-1));//qpprices[keyay] = -1;
            qpmileages.set(keyay, new Integer(-1));//qpmileages[keyay] = -1;

            cars.set(keyay, null);// cars[keyay] = null;
            carmap.replace(vin, null);

            // System.out.println("Printing qpprices:");
            // for(int all = 0; all < qpprices.length; all++){
            //     System.out.print(qpprices[all] + " ");
            // }
            // System.out.println();
            // System.out.println("Printing qpmileages:");
            // for(int all = 0; all < qpmileages.length; all++){
            //     System.out.print(qpmileages[all] + " ");
            // }
            // System.out.println();
           
        }


        public Car getMinPriceCar(){
            if(n2 == 0){
                System.out.println("Model: " + name + " 's has no car elements in it. Returning null.");
                return null;
            }
            assert cars.get(pqprices.get(1).intValue()) != null;//assert cars[pqprices[1]] != null;
            return cars.get(pqprices.get(1).intValue()).car;//return cars[pqprices[1]].car;
        }

        public Car getMinMileageCar(){
            if(n2 == 0){
                System.out.println("Model: " + name + " 's has no car elements in it. Returning null.");
                return null;
            }
            assert cars.get(pqmileages.get(1).intValue()) != null;// assert cars[pqmileages[1]] != null;
            return cars.get(pqmileages.get(1).intValue()).car;
        }
        public boolean contains(CarObject carob){
            return carmap.get(carob.car.VIN) != null; 
        }

        public void update(String vin, char y, double number, String color){
                if(y == 'p'){
                    //will need to swim and sink in the heap based on the change.
                    int heapindex = qpprices.get(carmap.get(vin).intValue()).intValue();//qpprices[carmap.get(vin).intValue()];
                    Car k = cars.get(carmap.get(vin).intValue()).car;//cars[carmap.get(vin).intValue()].car;
                    k.price = number;
                    swimPrices(heapindex);
                    sinkPrices(heapindex);
                }
                else if(y == 'm'){
                    //will need to swim and sink in the heap based on the change.
                    int heapindex = qpmileages.get(carmap.get(vin).intValue()).intValue();//qpmileages[carmap.get(vin).intValue()];
                    Car k = cars.get(carmap.get(vin).intValue()).car;//cars[carmap.get(vin).intValue()].car;
                    k.mileage = number;
                    swimMileages(heapindex);
                    sinkMileages(heapindex);
                }
                else if(y == 'c'){
                    //will need to swim and sink in the heap based on the change.
                    Car k = cars.get(carmap.get(vin).intValue()).car;//cars[carmap.get(vin).intValue()].car;
                    k.Color = color;
                }
                else {
                    System.out.println("This mode is not supported.");
                    //throw new IllegalArgumentException("the char y for update is not supported.");
                }
        }

        public void exchange(ArrayList<Integer> arr, int k, int v){
                if(arr.equals(pqprices)){
                    //swap CarObjects and update qpprices accordingly
                    int swap = pqprices.get(k).intValue();//pqprices[k];
                    pqprices.set(k, new Integer(pqprices.get(v).intValue()));//pqprices[k] = pqprices[v];
                    pqprices.set(v, new Integer(swap));//pqprices[v] = swap;
                    qpprices.set(pqprices.get(k).intValue(), new Integer(k));//qpprices[pqprices[k]] = k;
                    qpprices.set(pqprices.get(v).intValue(), new Integer(v));//qpprices[pqprices[v]] = v;
                }
                else if(arr.equals(pqmileages)){
                    int swap = pqmileages.get(k).intValue();//pqmileages[k];
                    pqmileages.set(k, new Integer(pqmileages.get(v).intValue()));//pqmileages[k] = pqmileages[v];
                    pqmileages.set(v, new Integer(swap));//pqmileages[v] = swap;
                    qpmileages.set(pqmileages.get(k).intValue(), new Integer(k));//qpmileages[pqmileages[k]] = k;
                    qpmileages.set(pqmileages.get(v).intValue(), new Integer(v));//qpmileages[pqmileages[v]] = v;
                }
                else{
                    throw new RuntimeException("Invalid int[] arr used for exch function in PriorityQueue");
                }
        }

        public boolean greaterMileages(int k, int v){
            // if(/*cars[pqmileages[k]]*/cars.get(pqmileages.get(k).intValue()) == null || /*cars[pqmileages[k]].car*/ cars.get(pqmileages.get(k).intValue()).car == null || /*cars[pqmileages[v]]*/ cars.get(pqmileages.get(v).intValue()) == null || /*cars[pqmileages[v]].car*/cars.get(pqmileages.get(v).intValue()).car == null){
            //     System.out.println("index " + k + " is null in pqmileages");
            //     // System.out.println("null error occurred: printing pqmileages. indexes accessed: " + k + " " + v);
            //     // for(int i=0; i < pqmileages.length; i++)
            //     //     System.out.print(pqmileages[i] + " ");
            //     // System.out.println();
            //     // for(int i=0; i < cars.length; i++){
            //     //    if(cars[pqmileages[i]] != null)
            //     //     System.out.print(pqmileages[i] + " "); //printing heap value
            //     //    else
            //     //     System.out.print("null" + " ");
            //     // }
            //     // System.out.println();
            //     return false;
            // }
            // else
                return cars.get(pqmileages.get(k).intValue()).car.mileage > cars.get(pqmileages.get(v).intValue()).car.mileage; //cars[pqmileages[k]].car.mileage > cars[pqmileages[v]].car.mileage;
        }
        public boolean greaterPrices(int k, int v){
            // if(cars[pqprices[k]] == null){
            //     //System.out.println("index " + k + " is null in pqprices");
            //     return false;
            // }
            // else
                return cars.get(pqprices.get(k).intValue()).car.price > cars.get(pqprices.get(v).intValue()).car.price; //cars[pqprices[k]].car.price > cars[pqprices[v]].car.price;
        }

        public void printCars(){
            for(CarObject yeet : cars){
                if(yeet != null)
                    System.out.println(yeet.car.toString());
            }
        }

        public boolean validateVINs(String vin){
            if(keymap.get(vin) != null){
                int index = keymap.get(vin).intValue();
                return index >= 0 && index < max;
            }
                return false;
          
        }
    }
    
   







    private class CarObject implements Comparable<PQCarChooser.CarObject> {
        public Car car; //car object representation
        public int indexprice; // index in either the min price heap or min mileage heap
        public int indexmileage;
        //index here is the position this CarObject holds in the min heap of all cars.


        public CarObject(Car car, int i, int j){
            this.car = car;
            indexprice = i;
            indexmileage = j;
        }


        public int compareTo(CarObject b){
            return 0;
        }
        //return true if this CarObject's car price is greater than CarObject b. return false if it is equal or smaller.
        public boolean isPriceGreater(CarObject b){
            return this.car.price > b.car.price;
        }
        //return true if this CarObject's mileage is greater than CarObject b. return false if it is equal or smaller.
        public boolean isMileageGreater(CarObject b){
            return this.car.mileage > b.car.mileage;
        }
    }
}