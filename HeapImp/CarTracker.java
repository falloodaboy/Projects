import java.io.*;
import java.util.*;
public class CarTracker {

	public static void main(String[] args){
		File file;
		Scanner scan = null;
		PQCarChooser queue;
		try{
		 file = new File("cars.txt");
		 scan = new Scanner(file);
		}
		catch(RuntimeException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		Vector<Car> keys = new Vector<>();
		
		if(scan != null){
			int i = 0;
			while(scan.hasNextLine()){
				String s = scan.nextLine();
				if(s.charAt(0) == '#')
					continue;
	            String[] csv = s.split(":");
	            keys.add(new Car(csv[0], csv[1], csv[2], Double.parseDouble(csv[3]), Double.parseDouble(csv[4]), csv[5]));
	            i++;
			}
			System.out.println(i);
			queue = new PQCarChooser(i);
				//queue.printFile();
			// for(int j=0; j < keys.size(); j++){
			// 	System.out.println(keys.get(j));
			// }
			for(Car k : keys){
				//System.out.println("Car: " + k.VIN + " " + k.make  + " " + k.model + " " + k.price + " " + k.mileage + " added");
				queue.add(k);
			}
			// System.out.println("Emptying Heap");
			// queue.emptyHeap();
			// System.out.println();
			// System.out.println();
			// System.out.println("Emptying models: ");
			// queue.printAllModels();
			

			// System.out.println("Minimum price Ford Fiesta: ");
			// System.out.println(queue.getMinPriceCarMakeAndModel("Ford", "Fiesta").price);

			// System.out.println("Minimum mileage Ford Fiesta: ");
			// System.out.println(queue.getMinMileageCarMakeAndModel("Ford", "Fiesta").mileage);


			// System.out.println("Updating Ford Fiesta price in Fiesta model: ");
			// queue.update(keys.get(1).VIN, 'p', 20000, null);

			// System.out.println("Emptying Heap: ");
			// queue.emptyHeap();

			// System.out.println("Printing Models: ");
			// queue.printAllModels();


			// int well = 0;
			// while(queue.size() > 0){
			// 	queue.remove(keys.get(well).VIN);
			// 	well++;
			// }
			// queue.printAllModels();
		}
		else{
			throw new RuntimeException("Scanner is null");
		}
		


		Scanner newscan = new Scanner(System.in);
			System.out.println("Welcome to your car buying experience!");
			System.out.println("Enter 'E' if you would like to exit.");
			System.out.println("Please select one of the options below:");
			System.out.println("	1. add a car");
			System.out.println("	2. List of cars");
			System.out.println("	3. Update a car");
			System.out.println("	4. remove a car");
			System.out.println("	5. get the minimum price car");
			System.out.println("	6. get the minimum mileage car");
			System.out.println("	7. get the minimum priced car for a specific make and model");
			System.out.println("	8. get the minimum mileage car for a specific make and model");
		
		while(true){
			String choice = newscan.nextLine();
			int mode = 0;
			if(choice.equals("E"))
				break;
			else{
			  mode = Integer.parseInt(choice);
			}
			

				switch(mode){
				case 1:
					//prompt the user for details of the new car, build the Car object, add it to the queue
					System.out.println("Please enter the following details about the car you want to add:");
					System.out.print("Car VIN:");
					String newvin = newscan.nextLine();
					System.out.print("Car Make:");
					String newmake = newscan.nextLine();
					System.out.print("Car Model:");
					String newmodel = newscan.nextLine();
					System.out.print("Car Price:");
					double newprice = Double.parseDouble(newscan.nextLine());
					System.out.print("Car Mileage:");
					double newmileage = Double.parseDouble(newscan.nextLine());
					System.out.print("Car Color:");
					String newcolor = newscan.nextLine();
					Car newcar = new Car(newvin, newmake, newmodel, newprice, newmileage, newcolor);
					queue.add(newcar);
				break;
				case 2:
					//print the heap as it is. also print by models.
					System.out.println();
					System.out.println("Emptying Heap: ");
					queue.emptyHeap();
					System.out.println("Printing Models: ");
					queue.printAllModels();
				break;
				case 3:
					//ask for vin and update the car based on what needs updating.
					System.out.println();
					System.out.println("Enter the VIN of the car you would like to update:");
					String updatingvin = newscan.nextLine();
					if(!queue.contains(updatingvin)) {
						System.out.println("This car does not appear to be in the data structure.");
						break;
					}
					else{
						System.out.println("enter 'p' if updating car price, 'm' if updating mileage, or 'c' if updating color. ");
						char yaga = newscan.nextLine().charAt(0);
						if(yaga != 'c' && yaga != 'p' && yaga != 'm')
							System.out.println("Sorry, but you typed a letter which isn't supported by this app.");
						else{
							if(yaga == 'c'){
								System.out.print("Enter the new color for this car:");
								String newcolors = newscan.nextLine();
								queue.update(updatingvin, yaga, 0, newcolors);
							}
							else if(yaga == 'p'){
								System.out.print("Please enter the new price for this car:");
								double newprices = Double.parseDouble(newscan.nextLine());
								queue.update(updatingvin, yaga, newprices, null);
							}
							else if(yaga == 'm'){
								System.out.print("Please enter the new mileage for this car:");
								double newmileages = Double.parseDouble(newscan.nextLine());
								queue.update(updatingvin, yaga, newmileages, null);
							}
						}
							
					}
					

				break;
				case 4:
					//remove a car
					System.out.println("Please enter the VIN for the car you want to remove:");
					System.out.print("Car VIN:");
					String removevin = newscan.nextLine();
					if(!queue.contains(removevin)){
						System.out.println("This car does not appear to be in the data structure.");
						break;
					} 
					queue.remove(removevin);
				break;
				case 5:
					//get minimum price car from all cars
					System.out.println("The minimum price car is: ");
					System.out.println(queue.getMinPriceCar());
				break;
				case 6:	
					//get the minimum mileage car from all cars.
					System.out.println("The minimum mileage car is: ");
					System.out.println(queue.getMinMileageCar());
				break;
				case 7:
					//get the minimum price car from specific brand and model. Handle if it doesn't exist.
					System.out.println("Please enter the car brand: ");
					String makes = newscan.nextLine();
					System.out.println("Please enter the car model: ");
					String models = newscan.nextLine();
					System.out.println(queue.getMinPriceCarMakeAndModel(makes, models));
				break;
				case 8:	
					//get the minimum mileage car from specific brand and model. Handle if it doesn't exist.
					System.out.println("Please enter the car brand: ");
					String make = newscan.nextLine();
					System.out.println("Please enter the car model: ");
					String model = newscan.nextLine();
					System.out.println(queue.getMinMileageCarMakeAndModel(make, model));
				break;
			}





			// System.out.println("Welcome to your car buying experience!");
			// System.out.println("Enter 'E' if you would like to exit.");
			// System.out.println("Please select one of the options below:");
			System.out.println("	1. add a car");
			System.out.println("	2. List of cars");
			System.out.println("	3. Update a car");
			System.out.println("	4. remove a car");
			System.out.println("	5. get the minimum price car");
			System.out.println("	6. get the minimum mileage car");
			System.out.println("	7. get the minimum priced car for a specific make and model");
			System.out.println("	8. get the minimum mileage car for a specific make and model");
		}
	}
}