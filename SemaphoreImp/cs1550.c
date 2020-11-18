#include<linux/cs1550.h>
#include<linux/string.h>  
// #include <linux/sched.h>
// #include <linux/kernel.h>

//global semaphore ID generator
long semGUID = 0;

//initialize the global semaphore list.
gsemlist* head = NULL;


//printk(KERN_INFO "defining global spinlock. \n");

DEFINE_SPINLOCK(gsemlock);//initialize the global semaphore list spinlock.




asmlinkage long sys_cs1550_create(int value, char name[32], char key[32]) {

/* Precautions */
if(value < 0){
	printk(KERN_WARNING "sys_cs1550_create: value parameter cannot be less than 0. Exiting.\n");
	return -1;
}

if(strncmp(name, "", 32) == 0 || strncmp(key, "", 32) == 0){
	printk(KERN_WARNING "sys_cs1550_create: neither name nor key can be empty. Exiting.\n");
	return -1;
}


 
//printk(KERN_EMERG "sys_cs1550_create: calling spinlock on global spinlock.\n");

 spin_lock(&gsemlock);  //get the global lock for this list.


 gsemlist *inlist = (gsemlist *) kmalloc(sizeof(gsemlist), GFP_ATOMIC); //allocate memory for the global semaphore container.
 if(inlist == NULL){

 //printk(KERN_WARNING "sys_cs1550_create: failed to allocate memory for semaphore holder.\n");

 	
 	return -1; //if kmalloc failed, abort this function.
 }
 else{

 	//printk(KERN_WARNING "sys_cs1550_create: allocating memory for semaphore. Setting semaphore next to null. \n");
 	
 	inlist->next = NULL;//initialize next pointer in global semaphore container.
 	inlist->entry = (semaphore *) kmalloc(sizeof(semaphore), GFP_ATOMIC); //allocate memory for the semaphore struct

 	if(inlist->entry == NULL){ //if malloc for semaphore failed.
 		
 		//printk(KERN_WARNING "sys_cs1550_create: failed to allocate memory for semaphore.\n");
 	
 		if(inlist != NULL){ //if the semaphore container for the global list was allocated, free it.
 			kfree(inlist);
 		}

 		return -1; //abort and return.
 	}

 	//initialize the spinlock for this semaphore.
 	//printk(KERN_EMERG "sys_cs1550_create: initializing new semaphore spinlock. Calling spin_lock_init().\n");
 	spin_lock_init(&(inlist->entry->lock));

 	
 	//printk(KERN_EMERG "sys_cs1550_create: setting new semaphore's semGUID. the GUID is: %ld \n", semGUID);
 	
 	inlist->entry->sem_id = semGUID;//set the GUID for this semaphore.
 	
 	
 	semGUID++;//create new GUID for semaphores.

 //	printk(KERN_EMERG "sys_cs1550_create: incrementing semGUID. semGUID is: %ld \n", semGUID);

 	
 //	printk(KERN_EMERG "sys_cs1550_create: initializing semaphore value to 1.\n");
 	
 	inlist->entry->value = value;//initialize semaphore value to argument
 	
 	//add semaphore's key and name as passed to this function.
 	int i=0;
 	for( ; i < 32; i++){
 		inlist->entry->key[i] = key[i];
 	    inlist->entry->name[i] = name[i];
 	}

// printk(KERN_EMERG "sys_cs1550_create: initializing semaphore name and key. Name is: %s \n", inlist->entry->name);
 //	printk(KERN_EMERG "sys_cs1550_create: initializing semaphore name and key. Key is: %s \n", inlist->entry->key);

	
 	inlist->entry->q = NULL;//initialize process queue to null. 

 	//add to the global semaphore list.
 	if(head == NULL) {
 		//printk(KERN_EMERG "sys_cs1550_create: global semaphore list is empty. Assigning head to new semaphore.\n");
 		head = inlist;
 		head->next = NULL;
 	}
 	else{
 		//printk(KERN_EMERG "sys_cs1550_create: adding to global semaphore list.\n");
 		gsemlist *cur = head;
 		
 		
 		while(cur->next != NULL){
 		//	printk(KERN_EMERG "sys_cs1550_create: looking...\n");
 			cur = cur->next;
 		}
 		cur->next = inlist;
 		//printk(KERN_EMERG "sys_cs1550_create: Added semaphore to global list. \n");
 	}
 }

 
// printk(KERN_EMERG "sys_cs1550_create: calling spin_unlock on global spinlock.\n");
 
 spin_unlock(&gsemlock); //allow access to global semaphore list.

 
 //printk(KERN_EMERG "sys_cs1550_create: returning sem_id: id is: %ld \n", inlist->entry->sem_id);
 
 return inlist->entry->sem_id;//return this semaphore's ID.

}


asmlinkage long sys_cs1550_open(char name[32], char key[32]) {
 
/* Precautions */
if(strncmp(name, "", 32) == 0 || strncmp(key, "", 32) == 0){
	printk(KERN_WARNING "sys_cs1550_open: neither name nor key can be empty. Exiting.\n");
	return -1;
}



// printk(KERN_EMERG "sys_cs1550_open: calling spin_lock on global spinlock.\n");
 
 spin_lock(&gsemlock); //acquire global spinlock.

 	gsemlist *target = head;
 	long targetID = -1;

// 	printk(KERN_EMERG "sys_cs1550_open: looking for semaphore and checking with name and key \n");
 	
 	//search the global list for the specified semaphore.
 	while(target->next != NULL){

 		if(strncmp(target->entry->name, name, 32) == 0 && strncmp(target->entry->key, key, 32) == 0){
 			targetID = target->entry->sem_id;
 			break;
 		}
 		else{
 			target = target->next;
 		}
 	}

// 	printk(KERN_EMERG "sys_cs1550_open: calling spin_unlock on global spinlock. \n");
 	
 	spin_unlock(&gsemlock); //release the global spinlock.

 //	printk(KERN_EMERG "sys_cs1550_open: returning semID: %ld \n", targetID);
	return targetID;
}


asmlinkage long sys_cs1550_down(long sem_id){

/* Precautions */
if(sem_id < 0 || sem_id >= semGUID) {
	printk(KERN_EMERG "sys_cs1550_down: cannot find semaphore.  \n");
	return -1;
}


	long answer = -1;
	gsemlist* cur = head;

	//printk(KERN_EMERG "sys_cs1550_down: looking for semaphore to perform down operation on. \n");

	//search for the semaphore with sem_id.
	while(cur != NULL && cur->entry->sem_id != sem_id){

		cur = cur->next;
	}
	
	if(cur == NULL || cur->entry->sem_id != sem_id) { //if the semaphore is not found.

	//	printk(KERN_WARNING "sys_cs1550_down: failed to find semaphore to perform down operation on. \n");
		answer = -1;
	}
	else{
	//	printk(KERN_EMERG "sys_cs1550_down: calling spin_lock() on global spinlock.\n");
		
		spin_lock(&gsemlock); //acquire global spinlock.

	//	printk(KERN_EMERG "sys_cs1550_down: calling spin_lock() on semaphore spinlock. semID: %ld \n", cur->entry->sem_id);
		
		spin_lock(&(cur->entry->lock));//acquire target semaphore. spinlock.


		cur->entry->value -= 1; //decrement semaphore 
	
	//	printk(KERN_EMERG "sys_cs1550_down: decremented semaphore value. value = %ld \n", cur->entry->value);


		if(cur->entry->value < 0){//block process and add to this semaphore's queue.
			
		//	printk(KERN_EMERG "sys_cs1550_down: value is less than 0. semaphore id: %ld \n", cur->entry->sem_id);
			
			struct task_struct *p = current;
			
		//	printk(KERN_EMERG "sys_cs1550_down: enqueueing current process. semaphore id: %ld \n", cur->entry->sem_id);
			
			long response = enQueue(cur->entry->sem_id, p); //add to semaphore queue.
			
			if(response == 0){ //enQueue was successful.

		//	 printk(KERN_EMERG "sys_cs1550_down: enqueue successful. setting process state to interruptible. semaphore id: %ld \n", cur->entry->sem_id);
			
			 set_current_state(TASK_INTERRUPTIBLE);

		//	 printk(KERN_EMERG "sys_cs1550_down: releasing semaphore and global spinlock. Semaphore id: %ld \n", cur->entry->sem_id);
			
			 spin_unlock(&(cur->entry->lock)); //release both global and semaphore locks.
			 spin_unlock(&gsemlock);

		//	 printk(KERN_EMERG "sys_cs1550_down: calling schedule().\n");

			 schedule(); //schedule another process while this one waits.
			}
			else{ //if adding to queue failed for some reason.

		//		printk(KERN_WARNING "sys_cs1550_down: enQueue failed to add process to semaphore queue.\n");
		//		printk(KERN_EMERG "sys_cs1550_down: releasing semaphore and global spinlocks. \n");
			
				spin_unlock(&(cur->entry->lock));
				spin_unlock(&gsemlock);
			}
			
		}
		else{ //if semaphore is not negative. don't add to queue, just move on.
		
		//	printk(KERN_EMERG "sys_cs1550_down: sem value >= 0. Releasing semaphore and global spinlocks. Semaphore id: %ld \n", cur->entry->sem_id);
		
			//release the global and semaphore locks.
			spin_unlock(&(cur->entry->lock));
			spin_unlock(&gsemlock);
		}
		answer = 0; //successful response.
		
	}
//	printk(KERN_INFO "sys_cs1550_down: returning answer: %ld \n", answer);
	return answer;
}

asmlinkage long sys_cs1550_up(long sem_id){

/* Precautions */
if(sem_id < 0 || sem_id >= semGUID) {
	//printk(KERN_EMERG "sys_cs1550_up: cannot find semaphore.  \n");
	return -1;
}



 long answer = -1;
 gsemlist* cur = head;

 //	printk(KERN_EMERG "sys_cs1550_up: looking for semaphore. semID: %ld \n", sem_id);
	
	while(cur != NULL && cur->entry->sem_id != sem_id){ //search for semaphore in global list.

		cur = cur->next;
	}

	if(cur == NULL || cur->entry->sem_id != sem_id) { //didn't find semaphore. return bad response.

	//	printk(KERN_WARNING "sys_cs1550_up: failed to find semaphore to perform up operation on. \n");
		answer = -1;
	}
	else{

	//	printk(KERN_EMERG "sys_cs1550_up: calling spin_lock on global spinlock.\n");

		spin_lock(&gsemlock); //get global list spinlock.

	//	printk(KERN_EMERG "sys_cs1550_up: calling spin_lock on semaphore spinlock.\n");
		
		spin_lock(&(cur->entry->lock)); //get target semaphore spinlock.


		cur->entry->value += 1;//increment the semaphore.
	
	//	printk(KERN_EMERG "sys_cs1550_up: incremented semaphore value. Current value = %ld \n", cur->entry->value);


		if(cur->entry->value <= 0){ //if a process was blocked on this semaphore, release it from the queue.


			//wake up once process from this semaphore's queue and then free the memory that entry occupied.
		//	printk(KERN_EMERG "sys_cs1550_up: semaphore value <= 0. calling dequeue.\n");
			sementry* target = deQueue(cur->entry->sem_id);
			struct task_struct *ans = NULL;
			
			if(target != NULL){ //deQueue successful.
				 
				 ans = target->pcb;
		
		//		printk(KERN_EMERG "sys_cs1550_up: dequeue successful, calling kfree on returned queue node.\n");
			
				kfree(target); //free memory held by queue item.
			}
			else{
				//printk(KERN_EMERG "sys_cs1550_up: dequeue unsuccessful.\n");
			}
		//	printk(KERN_EMERG "sys_cs1550_up: calling spin_unlock() on semaphore and global spinlocks.\n");
			

			//release global and semaphore spinlocks.
			spin_unlock(&(cur->entry->lock)); 
			spin_unlock(&gsemlock);

		//	printk(KERN_EMERG "sys_cs1550_up: calling wake_up_process() on dequeued process. \n");
			
			if(ans != NULL){ //deQueue was successful, wake up the process.

				wake_up_process(ans); 
			}
		}
		else{

		//	printk(KERN_EMERG "sys_cs1550_up: semaphore value > 0. calling spin_unlock() on semaphore and global spinlocks.\n");
			
			//semaphore didn't have any process blocking on it. Release global and semaphore locks and move on.
			spin_unlock(&(cur->entry->lock));
			spin_unlock(&gsemlock);
		}
		answer = 0; //successful response.
	}
//	printk(KERN_EMERG "sys_cs1550_up: returning answer: %ld \n", answer);
	return answer;
}

asmlinkage long sys_cs1550_close(long sem_id){

/* Precautions */
if(sem_id < 0 || sem_id >= semGUID) {
	printk(KERN_EMERG "sys_cs1550_close: cannot find semaphore.  \n");
	return -1;
}



	long answer = -1;
	gsemlist* cur = head;
	gsemlist* prev = NULL;

//	printk(KERN_EMERG "sys_cs1550_close: calling spin_lock() on global spinlock.\n");
	
	spin_lock(&gsemlock);//acquire global handle on semaphore list.

//	printk(KERN_EMERG "sys_cs1550_close: looking for semaphore. SemID: %ld \n", sem_id);

	//search for the target semaphore in global list.
	while(cur != NULL && cur->entry->sem_id != sem_id){
		prev = cur;
		cur = cur->next;
	}


	if(cur == NULL || cur->entry->sem_id != sem_id) { //didn't find the target semaphore in the list.

	//	printk(KERN_WARNING "sys_cs1550_close: failed to find semaphore to perform close operation on. \n");

		answer = -1;// bad response.
	}
	else{
	//	printk(KERN_EMERG "sys_cs1550_close: calling spin_lock() on semaphore. SemID: %ld \n", cur->entry->sem_id);
	
		spin_lock(&(cur->entry->lock));//lock the target semaphore to close.
		
		if(cur->entry->q == NULL){//No semaphore waiting on this. Remove semaphore from list and free the memory.
			
	//		printk(KERN_EMERG "sys_cs1550_close: semaphore queue list is empty. SemID: %ld \n", cur->entry->sem_id);
		
			gsemlist* after = cur->next;
			
			if(prev != NULL){
				prev->next = after;
			}
			else{
				//if it is the head of the list.
				head = cur->next;
			}

			cur->next = NULL;
	//		printk(KERN_EMERG "sys_cs1550_close: freeing semaphore memory from global list. SemID: %ld \n", cur->entry->sem_id);
			
			kfree(cur); //free kernel memory.
			answer = 0; //response successful.
		}

		spin_unlock(&(cur->entry->lock)); //let go of target semaphore.

	}

//	printk(KERN_EMERG "sys_cs1550_close: calling spin_unlock() on global spinlock.\n");
	
	spin_unlock(&gsemlock);// release global spinlock.

//	printk(KERN_EMERG "sys_cs1550_close: returning answer: %ld \n", answer);
	return answer;
}

 sementry *deQueue(long semID){
//remove an entry from the target semaphore queue list.
	gsemlist* cur = head;
//	printk(KERN_EMERG "deQueue: looking for semaphore. semID: %ld \n", semID);
	

	while(cur != NULL && cur->entry->sem_id != semID){ //search for the semaphore in global list.

		cur = cur->next;
	}

	if(cur == NULL){ //could not find semaphore.

//		printk(KERN_EMERG "deQueue: semID: %ld not found. \n", semID);
		return NULL;
	}
	else {

		semqueue *qhead = cur->entry->q; //get the semaphore queue pointer from this semaphore.
		
		if(qhead->head == NULL){ //if there are no entries in this queue.

//			printk(KERN_EMERG "deQueue: semaphore queue empty. semID: %ld \n ", semID);
			
			kfree(qhead); //free memory for the semaphore queue
			cur->entry->q = NULL; //set the q pointer for this semaphore to NULL.
			return NULL;//bad response.
		}else{

//			printk(KERN_EMERG "deQueue: removing entry from semaphore queue. semID: %ld \n ", semID);

			sementry* first = qhead->head; 
			qhead->head = qhead->head->next; //remove target from the queue.
			first->next = NULL;//isolate the target queue item.
			
			if(qhead->head == NULL){ //if the removed item was the last in this queue, remove the queue pointer.

//				printk(KERN_EMERG "deQueue: Queue is empty after dequeue. Freeing semqueue. semID: %ld \n ", semID);

				kfree(qhead);
				cur->entry->q = NULL;
			}
			if(cur->entry->q != NULL){
				cur->entry->q->size -= 1; //decrement size of queue.
			}
			return first; //successful response.
		}
	}
}

 long enQueue(long semID, struct task_struct* pcb){
//make sure to set next to NULL for the last item in queue.
	long answer = -1;
	gsemlist* cur = head;

//	printk(KERN_EMERG "enQueue: looking for semaphore. semID: %ld \n", semID);
	
	while(cur != NULL && cur->entry->sem_id != semID){//search for the semaphore in the list.

		cur = cur->next;
	}
	
	if(cur == NULL || cur->entry->sem_id != semID){ //did not find the semaphore.

//		printk(KERN_EMERG "enQueue: semID: %ld not found. \n", semID);
		return -1;//bad response
	}

	else{
	
//		printk(KERN_EMERG "enQueue: adding process to the semaphore queue. SemID: %ld \n", semID);
	
		semqueue* qhead = cur->entry->q;  //get this semaphore's queue 
		
		if(qhead == NULL){ //allocated memory for queue if it is null.
			qhead = (semqueue *) kmalloc(sizeof(semqueue), GFP_ATOMIC);

			if(qhead == NULL){
//				printk(KERN_EMERG "enQueue: failed to allocate memory for semaphore queue. SemID: %ld \n", semID);
			
				return -1;//failed to allocate memory for this queue
			}
			else{
				//initialize and assign queue.
				qhead->head = NULL;
				qhead->tail = NULL;
//				printk(KERN_EMERG "enQueue: add semqueue to this semaphore queue head. SemID: %ld \n", semID);
				cur->entry->q = qhead;
			}
		}


		if(qhead->head == NULL){ //if queue is empty, allocate memory for it.
			qhead->head = (sementry *) kmalloc(sizeof(sementry), GFP_ATOMIC);
			
			
			if(qhead->head == NULL){
//				printk(KERN_WARNING "enQueue: kmalloc returned null.\n");
				
				if(qhead != NULL){
					kfree(qhead);
				}

				cur->entry->q = NULL;
				answer = -1;//bad response. Failed to allocate memory for this queue item.
			}
			else{//get the queue tail and add the queue item there.

				qhead->tail = qhead->head; //only one item in the queue, therefore head == tail.
				qhead->head->next = NULL;
				qhead->head->pcb = pcb;//set the task_struct for this process.
				answer = 0;//successful response.
			}
		}
		else { //if queue is not null. allocate memory at the end of the queue tail and set it.

			qhead->tail->next = (sementry *) kmalloc(sizeof(sementry), GFP_ATOMIC);
			
			if(qhead->tail->next == NULL){
//				printk(KERN_WARNING "enQueue: kmalloc returned null.\n");

				answer = -1;//allocation failure, abort.
			}
			else{

//				printk(KERN_WARNING "enQueue: adding process to the end of the queue. SemID: %ld \n", semID);

				//initialize item and assign to end of queue. Increment the size of the queue.
				qhead->tail = qhead->tail->next;
				qhead->tail->pcb = pcb; //set the task_struct for this process.
				qhead->tail->next = NULL;
				qhead->size += 1;
				answer = 0;
			}
		}
	}

	return answer;
}