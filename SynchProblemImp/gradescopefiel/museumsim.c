/*
	shared Variables:
	1. Visitors- (counting semaphore: initial value is 0)
	2. Guides-	 (counting semaphore: inital value is 0)
	3. tickets-	 (integer: initial value is tickets generated)
	4. accessBooth- (binary semaphore: initial value is 1)
	5. numPeopleInMuseum- (integer: initial value is 0)
	6. accessCountOfPeople- (binary semaphore: initial value is 1)
	7. guidesInMuseum-		(counting semaphore: initial value is 2)
	8. guidesMutex-			(binary semaphore: initial value is 1. used to control access to the guidesInMuseum semaphore)
	9. isMuseumOpen-		(boolean: initial value is FALSE)
	10. accessMuseumStatus-	(binary semaphore: initial value is 1)
	11. lastPerson- 		(binary semaphore: initial value is 0. Used by the last visitor process to release the guides)
	12. GuidesLeft-			(integer, incremented whenever a guide opens the museum. Decrements whenever the guide leaves)
	13. accessGuidesLeft-	(binary semaphroe: initial value is )
	14. GuideWaitingToLeave (boolean: initial value is false)
	15. GWaitMutex			(binary semaphore: initial value is 1)
	16. GWaitSem			(counting semaphore- initial value is 0)
	17. visCount 			(integer, initial value is equal to the number of visitors. Decreased by visitors as they tour)
	18. visCountMutex		(binary semaphore: initial value is 1)

	12 semaphores. 5 shared variables
*/



#include<stdlib.h>
#include<sys/mman.h>
#include<stdio.h>
#include <sys/time.h>
#include<stdbool.h>
#include<linux/unistd.h>


typedef struct {
	int Visitors;
	int Guides;
	int tickets;
	int accessBooth;
	int numPeopleInMuseum;
	int accessCountOfPeople;
	int guidesInMuseum;
	int guidesMutex;
	int lastPerson;
	int GuidesLeft;
	int accessGuidesLeft;
	int accessMuseumStatus;
	bool isMuseumOpen;
	bool guideWaitingToLeave;
	//int numVis;
	int GWaitMutex;
	int GWaitSem;
	bool areVisDone;
	int  visDoneMutex;
	struct timeval* time;
	int    visCount;
	int    visCountMutex;
} sharedvars;


	double numvisitors;
	double numguides;
	double sv, sg;
	double pv, pg;
	double dv, dg;
	int i = 0;
	double starttime;
	sharedvars* vars;
	uint sleeptime = 2;

long create(int value, char name[32], char key[32]) {
  return syscall(__NR_cs1550_create, value, name, key);
}

long open(char name[32], char key[32]) {
  return syscall(__NR_cs1550_open, name, key);
}

long down(long sem_id) {
  return syscall(__NR_cs1550_down, sem_id);
}

long up(long sem_id) {
  return syscall(__NR_cs1550_up, sem_id);
}

long close(long sem_id) {
  return syscall(__NR_cs1550_close, sem_id);
}







int visitorArrives(int time, int ID) {
	
	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}
	printf("Visitor %d arrives at time %d. \n", ID ,newtime);

	int ans = down(vars -> accessBooth);
	
	if(ans == -1){
		return -1;
	}

	if(vars -> tickets == 0){
		printf("Visitor %d leaves the museum at time %d. \n", ID ,newtime);
		up(vars -> accessBooth);
		return -1;
	}
	else{
		vars -> tickets -= 1;
	}


	ans = up(vars->accessBooth);

	if(ans == -1){
		return -1;
	}

	ans = up(vars->Visitors);

	if(ans == -1){
		return -1;
	}

	return 0;
}

void tourMuseum(int time, int ID) {
	
	int ans = down(vars -> Guides);

	ans = down(vars -> visDoneMutex);
		vars -> visCount--;

		if(vars -> visCount == 0){

			vars->areVisDone = true;

			up(vars -> Visitors);
			up(vars -> Visitors);
		}

	ans = up(vars -> visCountMutex);

	ans = up(vars -> visDoneMutex);

	if(ans == -1){
		return;
	}
	ans = down(vars -> accessCountOfPeople);

	if(ans == -1){
		ans = up(vars -> accessCountOfPeople);
		return;
	}

	vars -> numPeopleInMuseum += 1;

	ans = up(vars -> accessCountOfPeople);

	if(ans == -1){
		return;
	}

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("Visitor %d tours the museum at time %d. \n", ID ,newtime);

	sleep(sleeptime);
}

void visitorLeaves(int time, int ID) {
	
	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("Visitor %d leaves the museum at time %d. \n", ID ,newtime);
	
	int ans = down(vars -> accessCountOfPeople);

	if(ans == -1){
		return;
	}

	vars -> numPeopleInMuseum--;

	if(vars ->numPeopleInMuseum == 0){

		ans = up(vars -> lastPerson);

		if(ans == -1){
			ans = up(vars->accessCountOfPeople);
			return;
		}
	}

	ans = up(vars->accessCountOfPeople);

	if(ans == -1){
		return;
	}

	
}

int tourguideArrives(int time, int ID) {
	
	int ans = down(vars -> guidesInMuseum);

	


	if(ans == -1){
		return -1;
	}

	ans = down(vars -> guidesMutex);

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("Tour guide %d arrives at time %d. \n", ID ,newtime);

	if(ans == -1){
		return -1;
	}

	ans = down(vars -> Visitors);

	if(ans == -1){
		return -1;
	}


	ans = down(vars -> accessBooth);

	ans = down(vars -> visDoneMutex);

	if(ans == -1){
		return -1;
	}

	if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){

		up(vars -> guidesMutex);

		up(vars -> guidesInMuseum);

		ans = up(vars -> accessBooth);

		ans = up(vars -> visDoneMutex);
		return -1;
	}

	ans = up(vars -> accessBooth);

	ans = up(vars -> visDoneMutex);

	if(ans == -1){
		return -1;
	}

	up(vars -> guidesMutex);


	if(ans == -1){
		return -1;
	}

	

	return 0;
}

void openMuseum(int time, int ID) {
	
	int ans = down(vars -> accessGuidesLeft);


	vars -> GuidesLeft += 1;

	ans = up(vars -> accessGuidesLeft);

	ans = down(vars -> accessMuseumStatus);



	if(vars -> isMuseumOpen == false){
		vars -> isMuseumOpen = true;
	}

	ans = up(vars -> accessMuseumStatus);

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("Tour guide %d opens museum for tours at time %d. \n", ID ,newtime);


	ans = up(vars -> Guides);



	ans = down(vars -> accessBooth);

	ans = down(vars -> visDoneMutex);

		if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){

			ans = up(vars -> accessBooth);

			ans = up(vars -> visDoneMutex);
			return;
		}

		ans = up(vars -> accessBooth);

		ans = up(vars -> visDoneMutex);






	int counter = 1;

	for(; counter < 10; counter++){

		ans = down(vars -> Visitors);

		ans = down(vars -> accessBooth);

		ans = down(vars -> visDoneMutex);
		if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){

			ans = up(vars -> accessBooth);

			ans = up(vars -> visDoneMutex);
			break;
		}


		ans = up(vars -> accessBooth);

		ans = up(vars -> visDoneMutex);

		ans = up(vars -> Guides);
	}
}


void tourguideLeaves(int time, int ID) {
 
 	int ans = down(vars -> lastPerson);

	int newtime = 0;
	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}


	printf("Tour guide %d leaves the museum at time %d. \n", ID ,newtime);

	


 	
	 ans = down(vars -> accessGuidesLeft);

	if(vars->GuidesLeft == 2){

		ans = down(vars -> GWaitMutex);

		if(vars -> guideWaitingToLeave == true){

			ans = down(vars->accessMuseumStatus);

			vars -> isMuseumOpen = false;

			ans = up(vars -> accessMuseumStatus);

			ans = up(vars -> GWaitMutex);

			ans = down(vars -> guidesMutex);

			ans = up(vars -> guidesInMuseum);

			ans = up(vars -> guidesInMuseum);

			ans = up(vars-> GWaitSem);

			vars -> GuidesLeft -= 1;

			ans = up(vars->accessGuidesLeft);




			return;
		}
		else if(vars -> guideWaitingToLeave == false){
			vars -> guideWaitingToLeave = true;

			ans = up(vars -> GWaitMutex);

			ans = up(vars -> accessGuidesLeft);

			ans = up(vars -> lastPerson);

			ans = down(vars -> GWaitSem);

			ans = down(vars -> GWaitMutex);

			vars -> guideWaitingToLeave = false;

			ans = up(vars -> GWaitMutex);

			ans = down(vars -> accessGuidesLeft);

			vars -> GuidesLeft -= 1;

			ans = up(vars -> accessGuidesLeft);

			ans = up(vars -> guidesMutex); 

			return;
		}
	}
	else if(vars -> GuidesLeft == 1){
		ans = down(vars -> accessMuseumStatus);

		vars -> isMuseumOpen = false;

		ans = up(vars -> accessMuseumStatus);

		vars -> GuidesLeft -= 1;

		ans = up(vars -> accessGuidesLeft);

		ans = up(vars -> guidesInMuseum);
		return;

	}
	else if(vars -> GuidesLeft == 0){
		ans = up(vars -> guidesInMuseum);

		ans = up(vars -> accessGuidesLeft);
		return;
	}
}


int main(int argc, char* argv[]){

	int b = 1;
	for(; b < argc; b++){

		if(strncmp(argv[b], "-m", 2) == 0){
			numvisitors = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-k", 2) == 0){
			numguides = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-pv", 3) == 0){
			pv = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-dv", 3) == 0){
			dv = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-sv", 3) == 0){
			sv = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-pg", 3) == 0){
			pg = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-dg", 3) == 0){
			dg = atof(argv[(b+1)]);
		}
		else if(strncmp(argv[b], "-sg", 3) == 0){
			sg = atof(argv[(b+1)]);
		}
	}



	//allocate memory for the shared variables.
	void* ptr = mmap(NULL, sizeof(sharedvars), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);
	if(ptr == NULL){
		
		return;
	}

	


	vars = (sharedvars *) ptr;

	//map the shared variables.
	vars -> time  = mmap(NULL, sizeof(struct timeval), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);
	


	//make tickets available and set shared variables.
	vars -> tickets = numguides*10;
	
	vars -> numPeopleInMuseum = 0;

	vars -> GuidesLeft = 0;

	vars -> isMuseumOpen = false;

	vars -> guideWaitingToLeave = false;

	vars -> areVisDone = false;

	vars -> visCount = numvisitors;
	
	int ans = gettimeofday(vars->time, NULL);
	
	
	if(ans == -1) {
		return;
	}
	
	//set the time
	starttime = vars ->time->tv_sec + vars->time->tv_usec / 1000000;

	//create the semaphores with their initial values

	vars->lastPerson = create(0, "lastPerson", "key0");

	if(vars->lastPerson == -1){
		return;
	}


	vars -> Visitors = create(0, "Visitors", "key1");

	if(vars -> Visitors == -1){
		return;
	}


	 vars -> Guides = create(0, "Guides", "key2");

	if(vars -> Guides == -1){
		return;
	}
	vars -> accessBooth = create(1, "accessBooth", "key3");

	if(vars -> accessBooth == -1){
		return;
	}

	vars -> accessCountOfPeople = create(1, "accessCountOfPeople", "key4");

	if(vars -> accessCountOfPeople == -1){
		return;
	}
	vars -> accessMuseumStatus = create(1, "accessMuseumStatus", "key5");

	if(vars -> accessMuseumStatus == -1){
		return;
	}
	vars -> accessGuidesLeft = create(1, "accessGuidesLeft", "key6");

	if(vars -> accessGuidesLeft == -1){
		return;
	}
	vars -> guidesMutex = create(1, "guidesMutex", "key7");

	if(vars -> guidesMutex == -1){
		return;
	}
	vars -> guidesInMuseum = create(2, "guidesInMuseum", "key8");

	if(vars -> guidesInMuseum == -1){
		return;
	}

	vars -> GWaitMutex = create(1, "GWaitMutex", "Key9");

	vars -> GWaitSem = create(0, "GWaitSem", "Key10");

//	vars -> numVis = create(numvisitors, "numVis", "Key11");
	
	vars -> visDoneMutex = create(1, "visDoneMutex", "Key12");

	vars -> visCountMutex = create(1, "visCountMutex", "Key13");
	

	//fork the visitorarrival process and the tourguideArrival process
	int PID = fork();


	if(PID == 0){
		//VisitorArrivalProcess
		/*
			1. seed the random int.
			2. fork visitor processes with the delay if rand value is greater than pv
			3. if current process is the child of this process, then do what visitors do.
			4. the original process must wait for all child processes forked.
		*/
		
		int i = 0;
		int VisID = -1; //visitor number.
		srand(sv);
	
		for(i=0; i < numvisitors; i++){
			
			VisID++;
			int vPID = fork();

			

			if(vPID == 0){
				//do visitor stuff here.
				
				int arrivetime = -1;
				int resp = 2;





				 resp = visitorArrives(arrivetime, VisID);


				 if(resp == 0){ //check if the state of the museum is valid for this visitor.

					tourMuseum(arrivetime, VisID);
					 
					visitorLeaves(arrivetime, VisID);				 	

				 }

				exit(0);
			}

			else{
				
				int probability = rand() % 100 + 1; 
				if(probability > pv) {
					sleep(((int) dv));
				}
			}
		}

		for(i = 0; i < numvisitors; i++){ //wait for all of the child processes to finish.
			wait(NULL);
		}
	}

	else{
		//TourGuideArrivalProcess
		
		int i = 0;
		int GuideID = -1; //Tour guide number
		srand(sg);
		for(i=0; i < numguides; i++){
			int gPID = fork();
			GuideID++;
			

			if(gPID == 0){

				int resp = 2;
				int arrivetime = -1;
				//do tour guide stuff here.



				 resp = tourguideArrives(arrivetime, GuideID);

				  if(resp == 0){ //if conditions inside the museum are valid for the tourguide.
					

				 	openMuseum(arrivetime, GuideID);
				
				 	tourguideLeaves(arrivetime, GuideID);
				 }
	
				exit(0);
			}

			else{
				
				int probability = rand() % 100 + 1; 
				if(probability > pg){
					sleep(((int) dg));
				}
				
					 
				

			}

		}

				for(i=0; i < numguides; i++){
					wait(NULL);
				}
		

		wait(NULL); //wait for the VisitorArrivalProcess.
	}


	//close all of the semaphores used.
	close(vars -> Visitors);
	close(vars -> Guides);
	close(vars -> accessBooth);
	close(vars -> guidesInMuseum);
	close(vars -> guidesMutex);
	close(vars -> lastPerson);
	close(vars -> accessCountOfPeople);
	close(vars -> accessGuidesLeft);
	close(vars -> accessMuseumStatus);
	close(vars -> GWaitMutex);
	close(vars -> GWaitSem);
	close(vars -> visDoneMutex);
	close(vars -> visCountMutex);



	return 0;
		
}