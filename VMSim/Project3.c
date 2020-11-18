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
int insert_tail(node **q, long pgnm){
	//simply add to the tail of the node q.
	if((*q) == NULL){
		(*q) = (node *) malloc(sizeof(node));
		if((*q) == NULL){
			printf("failed to allocate memory for empty queue. \n");
			return -1;
		}
		
		(*q) -> next = NULL;
		(*q) -> pgnm = pgnm;
		(*q) -> ref = true;

		//printf("insert_tail: q is %x \n", (*q));
		return 0;
	}
	else{
		// node *cur2 = (*q);

		// while(cur2 != NULL){
		// 	printf("insert_tail: cur2 pgnm is: %x \n", cur2 -> pgnm);
		// 	cur2 = cur2 -> next;
		// }


		node *cur = (*q);
		node *nnode = (node *) malloc(sizeof(node));

		if(nnode == NULL)
			return -1;

		nnode -> next = NULL;
		nnode -> pgnm = pgnm;
		nnode -> ref = true;

		while(cur->next != NULL){
			cur = cur -> next;
		}

		cur->next = nnode;
	}

	// node* cur = (*q);

	// while(cur != NULL){
	// 	printf("insert_tail: cur pgnm is: %x \n", cur -> pgnm);
	// 	cur = cur -> next;
	// }
	
	return 0;

}

/*
For FIFO operations for second chance algorithm.
Returns the pgnm of the removed page.
*/
long remove_head(node **q, int count){
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
			//printf("iter pgnm is: %x \n", iter->pgnm);
			iter = iter -> next;
		}

	int n = 0;
	while(n < count && cur->ref == true){
		node *temp = cur;
		cur = cur->next;
		(*q) = cur;
		temp->next = NULL;
		temp->ref = false;
		iter->next = temp; //previous end node now points to this block.
		iter = temp; //iter now points to the new end of the list.
		n++;
	}

	// node* cur2 = (*q);

	// while(cur2 != NULL){
	// 	printf("cur pgnm is: %x \n", cur2->pgnm);
	// 	cur2 = cur2->next;
	// }

	//reference for the current q is now false.
	
	node *temp = (*q);
	(*q) = (*q) -> next;
	gpg = temp->pgnm;
	temp -> next = NULL;
	free(temp);
	//printf("q pgnm is: %x q next pgnm is: %x \n", (*q)->pgnm, (*q) -> next);
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
	unsigned int address;
	int proc;
	char mode;
	int frames;
	int pagesize;
	char* memsplit;
	char* tracefile;

	//Page Sim Variables
	int p1sp;
	int p2sp;
	int offset = 0;
	
	//statistics variables
	int numpgflts = 0;
	int numdirev = 0;
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

if(fp != NULL){
	while(true){
		if(feof(fp)){
			break;
		}
		else{
			fscanf(fp, "%c %x %d \n", &mode, &address, &proc);
			nummemac++;
			//printf("address: %x \n", address);
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
			bool fdpg = false;
			int chind = -1;
			int i;
			for(i = 0; i < frames; i++){
				if(physmem[i].valid == true && physmem[i].proc == proc && physmem[i].pgnm == pg){
					fdpg = true;
					chind = i;
				}
			}
		
			if(fdpg == true){ //page hit
				//printf("found page: %x at index: %d \n", pg, chind);
				
				physmem[chind].ref = true;
				if(mode == 's'){
					physmem[chind].dirty = true;
				}

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
					//printf("proc is not 0 or 1 for some reason. proc is: %d\n", proc);
				}
				
				while(cur->next != NULL){
					//printf("looking for pgnm: %x \n", pg );
					if(cur->pgnm == pg){
						cur->ref = true;
						break;
					}
					cur = cur -> next;
				}

			}
			else{ //page fault
				//printf("page fault at page: %x for proc: %d \n", pg, proc);
				numpgflts++;
				if(proc == 0){
					if(nump1FIFO == p1sp){
						//printf("page limit for proc 0 has been reached. removing head. \n");
						long rmpg = remove_head(&(p1FIFO->head), nump1FIFO);
						//printf("removed page num was: %x \n", rmpg);
						// node *suck = p1FIFO->head;

						// while(suck != NULL){
						// 	printf("suck pgnm is: %x \n", suck -> pgnm);
						// 	suck = suck -> next;
						// }

						int i;
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == true && physmem[i].pgnm == rmpg && physmem[i].proc == 0){
								//printf("found the rmpg %x at index: %d in physical memory\n", rmpg, i);
								chind = i;
								break;
							}
						}

						if(physmem[chind].dirty == true){
							numdirev++;
						}

						physmem[chind].pgnm = pg;
						physmem[chind].valid = true;
						physmem[chind].ref = true;
						physmem[chind].proc = 0;

						if(mode == 's'){
							physmem[chind].dirty = true;
						}
						//printf("inserting new page to p1FIFO \n");
						insert_tail(&(p1FIFO->head), pg);
					}
					else{
					//	printf("limit has not been reached for proc 0. Adding to physical memory and p1FIFO. \n");
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == false){
								//printf("storing pg %x at index %d in physical memory\n", pg, i);


								physmem[i].valid = true;
								physmem[i].pgnm = pg;
								physmem[i].ref = true;
								physmem[i].proc = 0;

								if(mode == 's'){
									physmem[i].dirty = true;
								}


								insert_tail(&(p1FIFO -> head), pg);
								//printf("p1FIFO head is: %x \n", p1FIFO->head);
								nump1FIFO++;
								break;
							}
						}
					}
				}
				else if(proc == 1){
					if(nump2FIFO == p2sp){
						//printf("page limit for proc 1 has been reached. removing head of p2FIFO. \n");
						long rmpg = remove_head(&(p2FIFO->head), nump2FIFO);
					//	printf("removed page num was: %x \n", rmpg);
						// node *suck = p2FIFO->head;

						// while(suck != NULL){
						// 	printf("suck pgnm is: %x \n", suck -> pgnm);
						// 	suck = suck -> next;
						// }


						int i;
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == true && physmem[i].pgnm == rmpg && physmem[i].proc == 1){
								//printf("found the rmpg %x at index: %d in physical memory\n", rmpg, i);
								chind = i;
								break;
							}
						}

						if(physmem[chind].dirty == true){
							numdirev++;
						}

						physmem[chind].pgnm = pg;
						physmem[chind].valid = true;
						physmem[chind].ref = true;
						physmem[chind].proc = 1;

						if(mode == 's'){
							physmem[chind].dirty = true;
						}
						//printf("inserting new page to p2FIFO \n");
						insert_tail(&(p2FIFO -> head), pg);
					}
					else{
						//printf("limit has not been reached for proc 1. Adding to physical memory and p2FIFO. \n");
						for(i = 0; i < frames; i++){
							if(physmem[i].valid == false){
								//printf("storing pg %x at index %d in physical memory\n", pg, i);
								physmem[i].valid = true;
								physmem[i].pgnm = pg;
								physmem[i].ref = true;
								physmem[i].proc = 1;

								if(mode == 's'){
									physmem[i].dirty = true;
								}

								insert_tail(&(p2FIFO -> head), pg);
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


	node *cur = p1FIFO -> head -> next;
	while(cur != NULL){
		node *temp = cur;
		cur = cur -> next;
		free(temp);
	}

	cur = p2FIFO -> head -> next;
	while(cur != NULL){
		node *temp = cur;
		cur = cur -> next;
		free(temp);
	}

	free(p1FIFO);
	free(p2FIFO);
	fclose(fp);


	printf("Algorithm: Second Chance\n");
	printf("Number of frames: %d \n", frames);
	printf("Page size: %d KB \n", pagesize);
	printf("Total memory accesses: %d \n", nummemac);
	printf("Total page faults: %d \n", numpgflts);
	printf("Total writes to disk: %d \n", numdirev);



	return 0;
}