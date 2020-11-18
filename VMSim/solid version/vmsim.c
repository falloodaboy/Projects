#include<stdlib.h>
#include<stdio.h>
#include<string.h>
#include<stdbool.h>
#include <unistd.h>
#include<ctype.h>


//virtual simulation of a page.
typedef struct {
	bool ref;
	bool dirty;
	bool valid;
	int proc;
	long pgnm;
} page;

typedef struct temp{
	bool ref;
	long pgnm;
	struct temp *next;

} node;

typedef struct nip{
	node *head;
	unsigned int count;

} queue;




/*
For FIFO operations for second chance algorithm.

*/
int insert_tail(node **q, long pgnm, bool val){
	//simply add to the tail of the node q.
	if((*q) == NULL){
		(*q) = (node *) malloc(sizeof(node));
		if((*q) == NULL){
			printf("failed to allocate memory for empty queue. \n");
			return -1;
		}
		
		(*q) -> next = NULL;
		(*q) -> pgnm = pgnm;
		if(val == true)
			(*q) -> ref = true;
		else
			(*q) -> ref = false;

		return 0;
	}
	else{


		node *cur = (*q);
		node *nnode = (node *) malloc(sizeof(node));

		if(nnode == NULL){
			printf("failed to allocate new node for FIFO \n");
			return -1;
		}

		nnode -> next = NULL;
		nnode -> pgnm = pgnm;
		if(val == true)
			nnode -> ref = true;
		else
			nnode -> ref = false;

		while(cur->next != NULL){
			cur = cur -> next;
		}

		cur->next = nnode;
	}
	
	return 0;

}

/*
For FIFO operations for second chance algorithm.
Returns the pgnm of the removed page.
*/
long remove_head(node **q){
	//implement second chance algorithm here.
	long gpg = 0;

	if(q == NULL){
		printf("remove_head: q is null. \n");
		return -1;
	}

	node *cur = (*q);
	node *iter = cur;

		//go to the end of the file.
		while(iter->next != NULL){
			iter = iter -> next;
		}

	while(cur->ref == true){
		node *temp = cur;
		cur = cur->next;
		(*q) = cur;
		temp->next = NULL;
		temp->ref = false;
		iter->next = temp; //previous end node now points to this block.
		iter = temp; //iter now points to the new end of the list.
	}

	//reference for the current q is now false.
	
	node *temp = (*q);
	(*q) = (*q) -> next;
	gpg = temp->pgnm;
	temp -> next = NULL;
	free(temp);

	// printf("remove_head: checking state of queue: \n");

	// node *pur = (*q);


	return gpg;
}


/*
	Steps to do this:
	1. Read command inputs and parse them.
	2. Set up data structures
	3. open trace file and start reading from it.
	4. Perform the logic to read/write to data structure sim
	5. Collect info as Data is read/written on data structure sim
*/
int main(int argc, char *argv[]){

	//file variables
	unsigned int address = 0;
	int proc = -1;
	char mode = 0;
	int frames;
	int pagesize;
	char* memsplit;
	char* tracefile;

	//Page Sim Variables
	int p1sp;
	int p2sp;
	int offset = 0;
	
	//statistics variables
	int numpgflts1 = 0;
	int numpgflts2 = 0;
	int numdirev1 = 0;
	int numdirev2 = 0;

	int nummemac = 0;



	int c;

	//Process 1 and 2 PRA data structures and info
	queue *p1FIFO = NULL;
	queue *p2FIFO = NULL;
	int nump1FIFO = 0; //count of nodes in p1FIFO
	int nump2FIFO = 0; //count of nodes in p2FIFO




	while((c = getopt(argc, argv, "n:p:s:")) != -1){
		switch(c){
			case 'n':
				frames = atoi(optarg);
			break;

			case 'p':
				pagesize = atoi(optarg);
			break;

			case 's':
				memsplit = optarg;
			break;
		}
	}

		tracefile = argv[optind];

		//printf("frames: %d pagesize: %d memsplit: %s tracefile: %s\n", frames, pagesize, memsplit, tracefile);
	page physmem[frames];


	int i;
	for(i = 0; i < frames; i++){
		physmem[i].proc = -1;
		physmem[i].pgnm = 0;
		physmem[i].ref = false;
		physmem[i].valid = false;
		physmem[i].dirty = false;
	}

	char* token = strtok(memsplit, ":");

	if(token != NULL){
		p1sp = atoi(token);
		token = strtok(NULL, ":");
		p2sp = atoi(token);

	}
	int diver = p1sp + p2sp;
	int ans = frames / diver;
	p1sp *= ans;
	p2sp *= ans;

	p1FIFO = (queue *) malloc(sizeof(node));

	if(p1FIFO == NULL){
		printf("Could not allocate memory for p1FIFO. \n");
		exit(0);
	}

	p2FIFO = (queue *) malloc(sizeof(node));
	
	if(p2FIFO == NULL){
		printf("Could not allocate memory for p2FIFO. \n");
		exit(0);
	}

	//init both FIFO queues for process 1 and 2.

	p1FIFO -> head = NULL;
	p2FIFO -> head = NULL;

	p1FIFO -> count = 0;
	p2FIFO -> count = 0;

	//printf("p1sp: %d p2sp: %d \n", p1sp, p2sp);

	int n = 1024 * pagesize;
	
	while(n != 1){
		n = n >> 1;
		offset++;
	}

	//printf("offset: %d \n", offset);

	FILE *fp = fopen(tracefile, "r");
	bool isempt = false;
	FILE *cpt = fp;

	//printf("seeking to the end. \n");
	fseek(cpt, 0, SEEK_END);

	if(ftell(cpt) == 0){
		//printf("file is empty \n");
		isempt = true;
	}
	
	fseek(cpt, 0, SEEK_SET);


if(fp != NULL && isempt == false){
		
	while(true){
		if(feof(fp)){
			break;
		}
		else{
			//printf("getting file output: \n");
			fscanf(fp, "%c %x %d \n", &mode, &address, &proc);
			nummemac++;
			

			//printf("mode: %c address: %x proc: %d \n", mode, address, proc);

			//add memsim logic here
			/*
				look for page in memory belonging to this address and process.
				if found, then check if mode is dirty and set to true if that is the case.
				if the page isn't found,
					if proc is 0
						if the total split has been alotted, then need to remove_head of FIFO, get the pgnm and remove it from physical memory.
						if the total split hasn't been alotted, then just find a nonvalid page in memory and write to that, add to FIFO, and increase count.
					if the proc is 1
						same as above.
			*/

			long pg = address >> offset;
			pg = pg << offset;
			bool fdpg = false;
			int chind = -1;
			int i;
			for(i = 0; i < frames; i++){
				if(physmem[i].valid == true && physmem[i].proc == proc && physmem[i].pgnm == pg){
					fdpg = true;
					chind = i;
					break;
				}
			}
		
			if(fdpg == true){ //page hit
//				printf("found page: %x at index: %d for proc: %d \n", pg, chind, proc);
				

				
				
				if(mode == 's'){
					physmem[chind].dirty = true;
				}
				else{
					physmem[chind].dirty = false;
				}

				physmem[chind].ref = true;
				node *cur = NULL;
				if(proc == 0){
					//printf("proc is 0 \n");
					cur = p1FIFO -> head;
				}
				else if(proc == 1){
					//printf("proc is 1 \n");
					cur = p2FIFO -> head;
				}

				if(cur == NULL){
					printf("proc is not 0 or 1 for some reason. proc is: %d\n", proc);
				}
				
				while(cur->next != NULL){
					//printf("looking for pgnm: %x \n", pg );
					if(cur->pgnm == pg){
						//printf("found pgnm: %x \n", pg);
						cur->ref = true;
						break;
					}
					cur = cur -> next;
				}


			}
			else{ //page fault
//				printf("page fault at page: %x for proc: %d \n", pg, proc);
				if(proc == 0){
					numpgflts1++;
					if(nump1FIFO == p1sp){
						int yeet = -1;
//						printf("page limit for proc 0 has been reached. removing head. \n");
						long rmpg = remove_head(&(p1FIFO->head));
//						printf("removed page num was: %x for proc: %d \n", rmpg, proc);

						int i;
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == true && physmem[i].pgnm == rmpg && physmem[i].proc == 0){
//								printf("found the rmpg %x at index: %d in physical memory for proc: %d \n", rmpg, i, proc);
								yeet = i;
								break;
							}
						}

						if(yeet == -1)
							printf("for some reason yeet is -1 in proc 0 \n");

						if(physmem[yeet].dirty == true){
							numdirev1++;
						}

						physmem[yeet].pgnm = pg;
						physmem[yeet].valid = true;
						physmem[yeet].ref = false;
						physmem[yeet].proc = 0;

						if(mode == 's'){
							physmem[yeet].dirty = true;
						}
						else{
							physmem[yeet].dirty = false;
						}
						//printf("inserting new page to p1FIFO \n");
						insert_tail(&(p1FIFO->head), pg, false);


					}
					else{
						//printf("limit has not been reached for proc 0. Adding to physical memory and p1FIFO. \n");
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == false){
//								printf("storing pg %x at index %d in physical memory for proc: %d \n", pg, i, proc);


								physmem[i].pgnm = pg;
								physmem[i].valid = true;
								physmem[i].ref = false;
								physmem[i].proc = 0;

								if(mode == 's'){
									physmem[i].dirty = true;
								}
								else{
									physmem[i].dirty = false;
								}


								insert_tail(&(p1FIFO -> head), pg, false);
								
								nump1FIFO++;
								break;
							}
						}
					}
				}
				else if(proc == 1){
					numpgflts2++;
					int yeet = -1;
					if(nump2FIFO == p2sp){
//						printf("page limit for proc 1 has been reached. removing head of p2FIFO. \n");
						long rmpg = remove_head(&(p2FIFO->head));
//						printf("removed page num was: %x \n", rmpg);


						int i;
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == true && physmem[i].pgnm == rmpg && physmem[i].proc == 1){
//								printf("found the rmpg %x at index: %d in physical memory for proc: %d\n", rmpg, i, proc);
								yeet = i;
								break;
							}
						}
						if(yeet == -1)
							printf("for some reason yeet is -1 in proc 1\n");

						if(physmem[yeet].dirty == true){
							numdirev2++;
						}

						physmem[yeet].pgnm = pg;
						physmem[yeet].valid = true;
						physmem[yeet].ref = false;
						physmem[yeet].proc = 1;

						if(mode == 's'){
							physmem[yeet].dirty = true;
						}
						else{
							physmem[yeet].dirty = false;
						}
						//printf("inserting new page to p2FIFO \n");
						insert_tail(&(p2FIFO -> head), pg, false);
						

					}
					else{
						//printf("limit has not been reached for proc 1. Adding to physical memory and p2FIFO. \n");
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == false){
//								printf("storing pg %x at index %d in physical memory for proc: %d\n", pg, i, proc);
								physmem[i].valid = true;
								physmem[i].pgnm = pg;
								physmem[i].ref = false;
								physmem[i].proc = 1;

								if(mode == 's'){
									physmem[i].dirty = true;
								}
								else{
									physmem[i].dirty = false;
								}

								insert_tail(&(p2FIFO -> head), pg, false);
								nump2FIFO++;
								break;
							}
						}
					}
				}
			}
		}
	}	
}

	//printf("freeing FIFO queues \n");
	
	node *cur = p1FIFO -> head;
	while(cur != NULL){
		node *temp = cur;
		cur = cur -> next;
		free(temp);
	}

	cur = p2FIFO -> head;
	while(cur != NULL){
		node *temp = cur;
		cur = cur -> next;
		free(temp);
	}


	fclose(fp);


	printf("Algorithm: Second Chance\n");
	printf("Number of frames: %d \n", frames);
	printf("Page size: %d KB \n", pagesize);
	printf("Process 0: \n");
	
	printf("Total page faults: %d \n", numpgflts1);
	printf("Total writes to disk: %d \n", numdirev1);

	printf("Process 1: \n");
	printf("Total page faults: %d \n", numpgflts2);
	printf("Total writes to disk: %d \n", numdirev2);

	printf("Total memory accesses: %d \n", nummemac);
	return 0;
}


//						printf("current p2FIFO after inserting %x at index %d for proc: %d\n:", pg, chind, proc);

						// node *suck =  p2FIFO->head;

						// while(suck != NULL){
						// 	if(suck->next != NULL)
						// 		printf("<%x %d> -> ", suck->pgnm, suck->ref);
						// 	else
						// 		printf("<%x %d> ", suck->pgnm, suck->ref);

						// 	suck = suck -> next;
						// }
						// printf("\n");





						//printf("current p1FIFO after inserting %x at index %d for proc: %d \n:", pg, chind, proc);

						// node *suck =  p1FIFO->head;

						// while(suck != NULL){
						// 	if(suck->next != NULL)
						// 		printf("<%x %d> ->", suck->pgnm, suck->ref);
						// 	else
						// 		printf("<%x %d> ", suck->pgnm, suck->ref);

						// 	suck = suck -> next;
						// }
						// printf("\n");




				// if(proc == 0){
				// 	printf("state of p1FIFO after page %x hit: for proc: %d\n", pg, proc);

				// 	cur = p1FIFO -> head;

				// 	while(cur != NULL){
				// 		printf("<%x %d>", cur -> pgnm, cur -> ref);
				// 		cur = cur -> next;
				// 	}
				// 	printf("\n");
				// }
				// else if(proc == 1){
				// 	printf("state of p2FIFO after page %x hit for proc: %d \n", pg, proc);

				// 	cur = p2FIFO -> head;

				// 	while(cur != NULL){
				// 		printf("<%x %d> ", cur -> pgnm, cur -> ref);
				// 		cur = cur -> next;
				// 	}
				// 	printf("\n");
				// }