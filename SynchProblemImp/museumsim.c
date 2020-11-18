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
	16. numVis 				(counting semaphore: initial value is numvisitors)
	17. GWaitSem			(counting semaphore- initial value is 0)


	12 semaphores. 5 shared variables
*/



#include<stdlib.h>
#include<sys/mman.h>
#include<stdio.h>
#include <sys/time.h>
#include<stdbool.h>
#include<linux/unistd.h>
#include <string.h>

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
	int numVis;
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
	

//	printf("visitorArrives: down accessBooth\n");
	int ans = down(vars -> accessBooth);

	if(ans == -1){
		printf("failed to down on the accessBooth semaphore.\n");
		printf("visitor %d leaves the museum at time %d \n", ID ,time);
		return -1;
	}

	if(vars -> tickets == 0){
		printf("Not enough tickets for visitor, leaving.\n");

	//	printf("visitorArrives: up accessBooth after checking for 0 tickets\n");
		up(vars -> accessBooth);
		return -1;
	}
	else{
		vars -> tickets -= 1;
	}

//	printf("visitorArrives: up accessBooth\n");
	ans = up(vars->accessBooth);

	if(ans == -1){
		printf("failed to up on the accessBooth semaphore.\n");
		return -1;
	}

	printf("visitorArrives: up Visitors.\n");
	ans = up(vars->Visitors);

	if(ans == -1){
		printf("failed to up on the Visitors semaphore.\n");
		return -1;
	}

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("visitor %d arrives at time %d \n", ID ,newtime);



	return 0;
}

void tourMuseum(int time, int ID) {
	

	printf("tourMuseum: down Guides\n");
	int ans = down(vars -> Guides);

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("visitor %d tours the museum at time %d \n", ID ,newtime);

	printf("tourMuseum: down visDoneMutex \n");
	ans = down(vars -> visDoneMutex);
		vars -> visCount--;

		if(vars -> visCount == 0){
			printf("tourMuseum: final visitor tours for the day.\n");

			vars->areVisDone = true;

			up(vars -> Visitors);
			up(vars -> Visitors);
		}

	printf("tourMuseum: up visCountMutex \n");
	ans = up(vars -> visCountMutex);

	printf("tourMuseum: up visDoneMutex \n");
	ans = up(vars -> visDoneMutex);



	if(ans == -1){
		printf("failed to down on the Guides semaphore.\n");
		return;
	}

//	printf("tourMuseum: down accessCountOfPeople\n");
	ans = down(vars -> accessCountOfPeople);

	if(ans == -1){
		printf("failed to down on the accessCountOfPeople semaphore.\n");
		return;
	}

	vars -> numPeopleInMuseum += 1;

//	printf("tourMuseum: up accessCountOfPeople\n");
	ans = up(vars -> accessCountOfPeople);

	if(ans == -1){
		printf("failed to up on the accessCountOfPeople semaphore.\n");
		return;
	}

	printf("tourMuseum: sleep. \n");
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


	printf("visitor %d leaves at time %d \n", ID ,newtime);
	
	// printf("visitorLeaves: down visCountMutex \n");
	// int ans = down(vars -> visCountMutex);

	// printf("visitorLeaves: down visDoneMutex \n");
	// ans = down(vars -> visDoneMutex);
	// 	vars -> visCount--;

	// 	if(vars -> visCount == 0){
	// 		printf("visitorLeaves: final visitor has arrived for the day.\n");

	// 		vars->areVisDone = true;

	// 		up(vars -> numVis);
	// 		up(vars -> numVis);
	// 	}

	// printf("visitorLeaves: up visCountMutex \n");
	// ans = up(vars -> visCountMutex);

	// printf("visitorLeaves: up visDoneMutex \n");
	// ans = up(vars -> visDoneMutex);
//	printf("visitorLeaves: down accessCountOfPeople. \n");
	int ans = down(vars -> accessCountOfPeople);

	if(ans == -1){
		printf("failed to down on the accessCountOfPeople semaphore.\n");
		return;
	}

	vars -> numPeopleInMuseum--;

	if(vars ->numPeopleInMuseum == 0){

		printf("visitorLeaves: up lastPerson \n");
		ans = up(vars -> lastPerson);

		if(ans == -1){
			printf("failed to up on the lastPerson semaphore.\n");
		}
	}

//	printf("visitorLeaves: up accessCountOfPeople. \n");
	ans = up(vars->accessCountOfPeople);

	if(ans == -1){
		printf("failed to up on the accessCountOfPeople semaphore.\n");
	}

	
}

int tourguideArrives(int time, int ID) {
	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("tourguide %d arrives at time %d \n", ID ,newtime);


	printf("tourguideArrives: down guidesInMuseum. \n");
	int ans = down(vars -> guidesInMuseum);

	if(ans == -1){
		printf("failed to down on the guidesInMuseum semaphore.\n");
		return -1;
	}

	printf("tourguideArrives: down guidesMutex. \n");
	ans = down(vars -> guidesMutex);

	// printf("tourguideArrives: down initial numVis.\n");
	// ans = down(vars -> numVis);

	if(ans == -1){
		printf("failed to down on the guidesMutex semaphore.\n");
		return -1;
	}


	printf("tourguideArrives: down Visitors. \n");
	ans = down(vars -> Visitors);

	if(ans == -1){
		printf("failed to down on the Visitors semaphore in the tourguideArrives function.\n");
		return -1;
	}


	printf("tourguideArrives: down accessBooth. \n");
	ans = down(vars -> accessBooth);

	printf("tourguideArrives: down visDoneMutex. \n");
	ans = down(vars -> visDoneMutex);

	if(ans == -1){
		printf("failed to down on the accessBooth semaphore.\n");
		return -1;
	}

	if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){
		printf("insufficient conditions. tourguide %d leaves at %d \n", ID ,time);
		printf("tourguideArrives: tickets == 0 up guidesMutex \n");
		up(vars -> guidesMutex);
		printf("tourguideArrives: tickets == 0 up guidesInMuseum \n");
		up(vars -> guidesInMuseum);
		printf("tourguideArrives: tickets == 0 up accessBooth \n");
		ans = up(vars -> accessBooth);
		printf("tourguideArrives: tickets == 0 up visDoneMutex. \n");
		ans = up(vars -> visDoneMutex);
		return -1;
	}

	printf("tourguideArrives: up accessBooth \n");
	ans = up(vars -> accessBooth);
	printf("tourguideArrives: up visDoneMutex. \n");
	ans = up(vars -> visDoneMutex);

	if(ans == -1){
		printf("failed to up on the accessBooth semaphore.\n");
		return -1;
	}

	printf("tourguideArrives: up guidesMutex \n");
	up(vars -> guidesMutex);


	if(ans == -1){
		printf("failed to up on the guidesMutex semaphore.\n");
		return -1;
	}

	

	return 0;
}

void openMuseum(int time, int ID) {
	
	printf("openMuseum: down accessGuidesLeft. \n");
	int ans = down(vars -> accessGuidesLeft);


	vars -> GuidesLeft += 1;

	printf("openMuseum: up accessGuidesLeft. \n");
	ans = up(vars -> accessGuidesLeft);




	
	printf("openMuseum: down accessMuseumStatus. \n");
	ans = down(vars -> accessMuseumStatus);



	if(vars -> isMuseumOpen == false){
		vars -> isMuseumOpen = true;
	}

	printf("openMuseum: up accessMuseumStatus. \n");
	ans = up(vars -> accessMuseumStatus);

	int newtime = 0;
 	int ans2 = gettimeofday(vars->time, NULL);
	if(ans2 == 0){
		newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
	}
	else{
		printf("gettimeofday failed.\n");
	}



	printf("tourguide %d opens museum at time %d \n", ID ,newtime);


	printf("openMuseum: up initial Guides. \n");
	ans = up(vars -> Guides);

	// printf("openMuseum: down initial numVis.\n");
	// ans = down(vars -> numVis);

	printf("openMuseum: down accessBooth. \n");
	ans = down(vars -> accessBooth);

	printf("openMuseum: down visDoneMutex. \n");
	ans = down(vars -> visDoneMutex);

		if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){
			printf("openMuseum: up accessBooth. tickets == 0 \n");
			ans = up(vars -> accessBooth);

			printf("openMuseum: up visDoneMutex. tickets == 0\n");
			ans = up(vars -> visDoneMutex);
			return;
		}

		printf("openMuseum: up accessBooth. \n");
		ans = up(vars -> accessBooth);

		printf("openMuseum: up visDoneMutex. \n");
		ans = up(vars -> visDoneMutex);






	int counter = 1;

	for(; counter < 10; counter++){

		printf("openMuseum: down Visitors for loop. \n");
		ans = down(vars -> Visitors);

		// printf("openMuseum: down numVis for loop.\n");
		// ans = down(vars -> numVis);

		printf("openMuseum: down accessBooth for loop. \n");
		ans = down(vars -> accessBooth);

		printf("openMuseum: down visDoneMutex for loop. \n");
		ans = down(vars -> visDoneMutex);
		if(vars -> tickets == 0 && vars -> areVisDone == true || vars -> areVisDone == true){
			printf("openMuseum: up accessBooth for loop. Tickest == 0 \n");
			ans = up(vars -> accessBooth);
			printf("openMuseum: up visDoneMutex for loop. \n");
			ans = up(vars -> visDoneMutex);
			break;
		}

		printf("openMuseum: up accessBooth for loop. \n");
		ans = up(vars -> accessBooth);
		printf("openMuseum: up visDoneMutex for loop. \n");
		ans = up(vars -> visDoneMutex);

		printf("openMuseum: up Guides for loop. \n");
		ans = up(vars -> Guides);
	}
}


void tourguideLeaves(int time, int ID) {
 


 	printf("tourguideLeaves: down on lastPerson\n");
 	int ans = down(vars -> lastPerson);
 	
 	int newtime = 0;
 	
 

 	printf("tourguideLeaves: down on accessGuidesLeft. \n");
	 ans = down(vars -> accessGuidesLeft);

	if(vars->GuidesLeft == 2){

		printf("tourguideLeaves: down on GWaitMutex. \n");
		ans = down(vars -> GWaitMutex);

		if(vars -> guideWaitingToLeave == true){
			// printf("tourguideLeaves: down on LastPerson. \n");
			// ans = down(vars -> lastPerson);

			printf("tourguideLeaves: down on accessMuseumStatus \n");
			ans = down(vars->accessMuseumStatus);

			vars -> isMuseumOpen = false;

			printf("tourguideLeaves: up on accessMuseumStatus \n");
			ans = up(vars -> accessMuseumStatus);

			printf("tourguideLeaves: up on GWaitMutex \n");
			ans = up(vars -> GWaitMutex);

			printf("tourguideLeaves: down on guidesMutex \n");
			ans = down(vars -> guidesMutex);

			printf("tourguideLeaves: up on guidesInMuseum \n");
			ans = up(vars -> guidesInMuseum);

			printf("tourguideLeaves: up on guidesInMuseum \n");
			ans = up(vars -> guidesInMuseum);

			printf("tourguideLeaves: up on GWaitSem \n");
			ans = up(vars-> GWaitSem);

		//	ans = up(vars -> guidesMutex); //could move this into the other guide process function.

			vars -> GuidesLeft -= 1;

			printf("tourguideLeaves: up on accessGuidesLeft \n");
			ans = up(vars->accessGuidesLeft);

			int ans2 = gettimeofday(vars->time, NULL);
			if(ans2 == 0){
				newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
			}
			else{
				printf("gettimeofday failed.\n");
			}



		 	printf("tourguide %d leaves at time %d \n", ID ,newtime);
			return;
		}
		else if(vars -> guideWaitingToLeave == false){
			vars -> guideWaitingToLeave = true;

			printf("tourguideLeaves: up on GWaitMutex \n");
			ans = up(vars -> GWaitMutex);

			printf("tourguideLeaves: up on accessGuidesLeft \n");
			ans = up(vars -> accessGuidesLeft);

			printf("tourguideLeaves: waking up the other guide process by up on lastPerson\n");
			ans = up(vars -> lastPerson);

			printf("tourguideLeaves: down on GWaitSem \n");
			ans = down(vars -> GWaitSem);

			printf("tourguideLeaves: down on GWaitMutex \n");
			ans = down(vars -> GWaitMutex);

			vars -> guideWaitingToLeave = false;

			printf("tourguideLeaves: up on GWaitMutex \n");
			ans = up(vars -> GWaitMutex);

			printf("tourguideLeaves: down on accessGuidesLeft \n");
			ans = down(vars -> accessGuidesLeft);

			vars -> GuidesLeft -= 1;

			printf("tourguideLeaves: up on accessGuidesLeft \n");
			ans = up(vars -> accessGuidesLeft);

			printf("tourguideLeaves: up on guidesMutex \n");
			ans = up(vars -> guidesMutex); // may prevent other guides from entering the museum before the first 2 leave.
			
			int ans2 = gettimeofday(vars->time, NULL);
			if(ans2 == 0){
				newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
			}
			else{
				printf("gettimeofday failed.\n");
			}



		 	printf("tourguide %d leaves at time %d \n", ID ,newtime);
			return;
		}
	}
	else if(vars -> GuidesLeft == 1){
		// printf("tourguideLeaves: down on lastPerson. \n");
		// ans = down(vars -> lastPerson);

		printf("tourguideLeaves: down on accessMuseumStatus. \n");
		ans = down(vars -> accessMuseumStatus);

		vars -> isMuseumOpen = false;

		printf("tourguideLeaves: up on accessMuseumStatus. \n");
		ans = up(vars -> accessMuseumStatus);

		vars -> GuidesLeft -= 1;

		printf("tourguideLeaves: up on accessGuidesLeft \n");
		ans = up(vars -> accessGuidesLeft);

		printf("tourguideLeaves: up on guidesInMuseum \n");
		ans = up(vars -> guidesInMuseum);
		int ans2 = gettimeofday(vars->time, NULL);
		if(ans2 == 0){
			newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
		}
		else{
			printf("gettimeofday failed.\n");
		}



	 	printf("tourguide %d leaves at time %d \n", ID ,newtime);

		return;

	}
	else if(vars -> GuidesLeft == 0){
		printf("tourguideLeaves: up on guidesInMuseum \n");
		ans = up(vars -> guidesInMuseum);

		printf("tourguideLeaves: up on accessGuidesLeft \n");
		ans = up(vars -> accessGuidesLeft);

		int ans2 = gettimeofday(vars->time, NULL);
		if(ans2 == 0){
			newtime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
		}
		else{
			printf("gettimeofday failed.\n");
		}



	 	printf("tourguide %d leaves at time %d \n", ID ,newtime);
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
	//get the appropriate inputs for this file.
	// numvisitors = atof(argv[1]);
	// numguides = atof(argv[2]);
	// pv = atof(argv[3]);
	// dv = atof(argv[4]);
	// sv = atof(argv[5]);
	// pg = atof(argv[6]);
	// dg = atof(argv[7]);
	// sg = atof(argv[8]);

	printf("allocating memory with mmap for the struct.\n");
	//allocate memory for the shared variables.
	void* ptr = mmap(NULL, sizeof(sharedvars), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);
	if(ptr == NULL){
		printf("ERROR: Could not allocate memory for shared variables.\n");
		return;
	}

	

	printf("setting vars to the ptr.\n");
	vars = (sharedvars *) ptr;

	printf("allocating memory with mmap for the timeval struct.\n");
	vars -> time  = mmap(NULL, sizeof(struct timeval), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);
	
	if(vars->time == NULL){
		printf("failed to allocate memory for the timeval struct.\n");
	}



	printf("setting vars non-semaphore variables. \n");
	//make tickets available
	vars -> tickets = numguides*10;
	
	vars -> numPeopleInMuseum = 0;

	vars -> GuidesLeft = 0;

	vars -> isMuseumOpen = false;

	vars -> guideWaitingToLeave = false;

	vars -> areVisDone = false;

	vars -> visCount = numvisitors;
	// printf("calling gettimeofday on vars->time.\n");
	// int ans = gettimeofday(vars->time, NULL);
	// printf("finished gettimeofday on vars->time.\n");
	
	// if(ans == -1) {
	// 	printf("the time val return -1.");
	// 	return;
	// }
	
	starttime = -1;


	//create the semaphores with their initial values

	vars->lastPerson = create(0, "lastPerson", "key0");

	if(vars->lastPerson == -1){
		printf("failed to create lastPerson semaphore.\n");
		return;
	}


	vars -> Visitors = create(0, "Visitors", "key1");

	if(vars -> Visitors == -1){
		printf("failed to create Visitors semaphore\n");
	}


	 vars -> Guides = create(0, "Guides", "key2");

	if(vars -> Guides == -1){
		printf("failed to create Guides semaphore\n");
		return;
	}
	vars -> accessBooth = create(1, "accessBooth", "key3");

	if(vars -> accessBooth == -1){
		printf("failed to create accessBooth semaphore\n");
		return;
	}

	vars -> accessCountOfPeople = create(1, "accessCountOfPeople", "key4");

	if(vars -> accessCountOfPeople == -1){
		printf("failed to create accessCountOfPeople semaphore.\n");
		return;
	}
	vars -> accessMuseumStatus = create(1, "accessMuseumStatus", "key5");

	if(vars -> accessMuseumStatus == -1){
		printf("failed to create accessMuseumStatus semaphore\n");
		return;
	}
	vars -> accessGuidesLeft = create(1, "accessGuidesLeft", "key6");

	if(vars -> accessGuidesLeft == -1){
		printf("failed to create accessGuidesLeft semaphore\n");
		return;
	}
	vars -> guidesMutex = create(1, "guidesMutex", "key7");

	if(vars -> guidesMutex == -1){
		printf("failed to create guidesMutex semaphore\n");
		return;
	}
	vars -> guidesInMuseum = create(2, "guidesInMuseum", "key8");

	if(vars -> guidesInMuseum == -1){
		printf("failed to create guidesInMuseum semaphore\n");
		return;
	}

	vars -> GWaitMutex = create(1, "GWaitMutex", "Key9");

	vars -> GWaitSem = create(0, "GWaitSem", "Key10");

	vars -> numVis = create(numvisitors, "numVis", "Key11");
	
	vars -> visDoneMutex = create(1, "visDoneMutex", "Key12");

	vars -> visCountMutex = create(1, "visCountMutex", "Key13");
	//fork the visitorarrival process and the tourguideArrival process
	int PID = fork();


	if(PID == 0){
		//VisitorArrivalProcess
		/*
			1. seed the random int.
			2. fork visitor processes with the delay if rand value is greater than 50
			3. if current process is the child of this process, then do what visitors do.
			4. the original process must wait for all child processes forked.
		*/
		srand(sv);
		int i = 0;
		int VisID = -1;
		for(i=0; i < numvisitors; i++){
			VisID++;
			// down(vars -> accessCountOfPeople);

			// if(vars -> numPeopleInMuseum == 0){
			// 	printf("The museum is now empty.\n");
			// }
			// up(vars -> accessCountOfPeople);


			int vPID = fork();

			if(vPID == 0){
				//do visitor stuff here.
				
				int arrivetime = -1;
				int resp = 2;
				if(starttime == -1){
					arrivetime = 0;
					int ans = gettimeofday(vars->time, NULL);

					if(ans == 0){
						starttime = vars->time->tv_sec + vars->time->tv_usec/ 1000000;

					}
					else{
						printf("gettimeofday failed.\n");
					}
				}
				else{
					//calculate how much time has elapsed since the start.

					int ans = gettimeofday(vars->time, NULL);
					if(ans == 0){
						arrivetime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;

					}
					else{
						printf("gettimeofday failed.\n");
					}
				}




				 resp = visitorArrives(arrivetime, VisID);


				 if(resp == 0){
					 int ans = gettimeofday(vars->time, NULL);
						if(ans == 0){
							arrivetime = (vars->time->tv_sec + vars->time->tv_usec/ 1000000) - starttime;

						}
						else{
							printf("gettimeofday failed.\n");
						}

					 tourMuseum(arrivetime, VisID);
					 

					    ans = gettimeofday(vars->time, NULL);
						if(ans == 0){
							arrivetime = (vars->time->tv_sec + vars->time->tv_usec/ 1000000) - starttime;

						}
						else{
							printf("gettimeofday failed.\n");
						}
					visitorLeaves(arrivetime, VisID);				 	

				 }
				 //procdone++;
				
				// if(){
				// 		printf("total visitors have been reached.\n");

				// 		printf("visitor process: down visDoneMutex \n");
				// 		down(vars -> visDoneMutex);
				// 		vars -> areVisDone = true;
				// 		printf("visitor process: up visDoneMutex \n");
				// 		up(vars -> visDoneMutex);
				// 		int j = 0;
				// 		printf("visitor process: up numVis for each numguide. \n");
				// 		for(; j < numguides; j++){
				// 			up(vars->numVis);
				// 		}
				// 		// printf("visitor process: down accessBooth \n");
				// 		// up(vars -> numVis);

				// 		// printf("visitor process: down accessBooth \n");
				// 		// up(vars -> numVis);

				// 	}


					
				//printf("This is a visitor process.\n");
				exit(0);
			}

			else{

				int probability = rand() % 100 + 1; 

				if(probability > pv) {
					sleep(dv);
				} 
			}
			
			


		}

		for(i = 0; i < numvisitors; i++){ //wait for all of the child processes to finish.
			wait(NULL);
		}
	}

	else{
		//TourGuideArrivalProcess
		srand(sg);
		int i = 0;
		int GuideID = -1;
		for(i=0; i < numguides; i++){
			GuideID++;
			int gPID = fork();

			if(gPID == 0){

				//printf("This is a guide process.\n");
				int resp = 2;
				int arrivetime = -1;
				//do tour guide stuff here.
				if(starttime == -1){
					arrivetime = 0;

					int ans = gettimeofday(vars->time, NULL);

					if(ans == 0){
						starttime = vars->time->tv_sec + vars->time->tv_usec / 1000000;
					}
					else{
						printf("gettimeofday failed.\n");
					}

					
				}
				else{
					//calculate how much time has elapsed since the start.
					int ans = gettimeofday(vars->time, NULL);
					if(ans == 0){
						arrivetime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
					}
					else{
						printf("gettimeofday failed.\n");
					}
					
				}
				//check here if tickets are 0. If yes, then tour guide should leave.


				 resp = tourguideArrives(arrivetime, GuideID);

				 if(resp == 0){
					
					int ans = gettimeofday(vars->time, NULL);
					if(ans == 0){
						arrivetime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
					}
					else{
						printf("gettimeofday failed.\n");
					}
				 	openMuseum(arrivetime, GuideID);
				
				 	ans = gettimeofday(vars->time, NULL);
					if(ans == 0){
						arrivetime = (vars->time->tv_sec + vars->time->tv_usec / 1000000) - starttime;
					}
					else{
						printf("gettimeofday failed.\n");
					}
				 	tourguideLeaves(arrivetime, GuideID);
				 }
	
				exit(0);
			}

			else{
				int probability = rand() % 100 + 1; 

				if(probability > pg)
					sleep(dg);

			}

		}

				for(i=0; i < numguides; i++){
					wait(NULL);
				}
		

		wait(NULL); //wait for the VisitorArrivalProcess.
	}

	close(vars -> Visitors);
	close(vars -> Guides);
	close(vars -> accessBooth);
	close(vars -> guidesInMuseum);
	close(vars -> guidesMutex);
	close(vars -> lastPerson);
	close(vars -> accessCountOfPeople);
	close(vars -> accessGuidesLeft);
	close(vars -> accessMuseumStatus);
	close(vars -> numVis);
	close(vars -> GWaitMutex);
	close(vars -> GWaitSem);
	close(vars -> visDoneMutex);
	close(vars -> visCountMutex);



	return 0;
		
}

































	// if(ans == 0){
	// 	starttime = vars->time->tv_sec + vars->time->tv_usec;
	// }
	// else{
	// 	printf("the time val return -1.");
	// }
	


















		// printf("tourguideLeaves: down lastPerson \n");
	// int ans = down(vars -> lastPerson);




	// printf("tourguide leaves at time: %d \n", time);

	//  printf("tourguideLeaves: down accessGuidesLeft \n");
	//  ans = down(vars -> accessGuidesLeft);



	// if(vars -> GuidesLeft == 2){
	// 	printf("tourguideLeaves: down guidesMutex \n");
	// 	ans = down(vars -> guidesMutex);
	


	// 	vars -> GuidesLeft -= 1;

	// 	printf("tourguideLeaves: up guidesInMuseum \n");
	// 	ans = up(vars -> guidesInMuseum);

	// 	printf("tourguideLeaves: up lastPerson \n");
	// 	ans = up(vars -> lastPerson);


	// }

	// else if(vars -> GuidesLeft == 1){
	// 	vars -> GuidesLeft -= 1;

	// 	printf("tourguideLeaves: down accessMuseumStatus \n");
	// 	ans = down(vars -> accessMuseumStatus);


	// 	vars -> isMuseumOpen = false;

	// 	printf("tourguideLeaves: up accessMuseumStatus \n");
	// 	ans = up(vars -> accessMuseumStatus);


	// 	printf("tourguideLeaves: up guidesInMuseum \n");
	// 	ans = up(vars -> guidesInMuseum);

	// 	printf("tourguideLeaves: up guidesMutex \n");
	// 	ans = up(vars -> guidesMutex);
	

	// }
	// else if(vars -> GuidesLeft == 0){
	// 		printf("tourguideLeaves: up guidesInMuseum \n");
	// 		ans = up(vars -> guidesInMuseum);

	// }
	// else{
	// 	printf("GuidesLeft is a number not recognized: %d\n", vars -> GuidesLeft);
	// }

	// printf("tourguideLeaves: up accessGuidesLeft \n");
	// ans = up(vars -> accessGuidesLeft);