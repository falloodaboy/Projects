#include<linux/smp_lock.h>

typedef struct queue_item
{
	
	struct task_struct *pcb;
	struct queue_item *next;
} sementry;

typedef struct FIFO_queue
{
	int size;
	sementry *head;
	sementry *tail;
} semqueue;


typedef struct cs1550_sem
{
 int value;
 long sem_id;
 spinlock_t lock;
 char key[32];
 char name[32];
 //Some FIFO queue of your devising
 semqueue *q;
} semaphore;

typedef struct semlist
{
	semaphore *entry;
	struct semlist *next;
} gsemlist;


/*Declare global semaphore list variable*/

spinlock_t gsemlock;

/* This syscall creates a new semaphore and stores the provided key to protect
access to the semaphore. The integer value is used to initialize the
semaphore's value. The function returns the identifier of the created
semaphore, which can be used to down and up the semaphore. */
asmlinkage long sys_cs1550_create(int value, char name[32], char key[32]);

/* This syscall opens an already created semaphore by providing the semaphore
name and the correct key. The function returns the identifier of the opened
semaphore if the key matches the stored key or -1 otherwise. */
asmlinkage long sys_cs1550_open(char name[32], char key[32]);

/* This syscall implements the down operation on an already opened semaphore
using the semaphore identifier obtained from a previous call to
sys_cs1550_create or sys_cs1550_open. The function returns 0 when successful
or -1 otherwise (e.g., if the semaphore id is invalid or if the queue is
full). Please check the lecture slides for the pseudo-code of the down
operation. */
asmlinkage long sys_cs1550_down(long sem_id);

/* This syscall implements the up operation on an already opened semaphore
using the semaphore identifier obtained from a previous call to
sys_cs1550_create or sys_cs1550_open. The function returns 0 when successful
or -1 otherwise (e.g., if the semaphore id is invalid). Please check the
lecture slides for pseudo-code of the up operation. */
asmlinkage long sys_cs1550_up(long sem_id);

/* This syscall removes an already created semaphore from the system-wide
semaphore list using the semaphore identifier obtained from a previous call to
sys_cs1550_create or sys_cs1550_open. The function returns 0 when successful
or -1 otherwise (e.g., if the semaphore id is invalid or the semaphore's
process queue is not empty). */
asmlinkage long sys_cs1550_close(long sem_id);

/*initialize the queue*/
// asmlinkage void init();

/*Remove the first entry in the semaphore with id=semID and return it.
Return NULL otherwise.*/
  sementry *deQueue(long semID);

/*add new entry to the semaphore with id = semID queue. Return
0 if successful. Return -1 otherwise.*/
 long enQueue(long semID, struct task_struct *pcb);