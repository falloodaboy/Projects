#File used to control the player
.include "game_settings.asm"
.include "convenience.asm"
.data
	up_flag: .word 0 #DO NOT CHANGE
	down_flag: .word 0 #DO NOT CHANGE
	braker: .asciiz "\n"
	inviflag: .byte 0
	timeinvi: .byte 0
.globl player_update
.globl find_block_row
.globl find_block_col
.globl draw_player
.text


#Description: Update the player's position based on which keys are pressed. Also add gravity to player and wall check mechanisms
#Inputs:
#a0 = address of player struct
player_update:
	enter	s0, s1, s2, s3
	li	t1, 0
	move	s0, a0
	li	s1, 0
jump_handler:
	move 	t5, a0
	la	a0, arena
	jal	let_player_jump
	la	a0, arena
	jal	bring_player_down
	move	a0, t5
player_update_loop:
	lw	t1, up_pressed
	bgt	t1, 0, pressed_up
	lw	t1, down_pressed
	bgt	t1, 0, pressed_down
	lw	t1, right_pressed
	bgt	t1, 0, pressed_right
	lw	t1, left_pressed
	bgt	t1, 0, pressed_left
	lw	t1, action_pressed
	bgt	t1, 0, pressed_action
	j	exit_player_update

pressed_action:
	lb	t6, in_motion
	beq	t6, 1, exit_player_update
	#set in_motion to 1 and shift player (x,y) into bullet (x,y)
	la	t2, player_struct
	la	t3, bullet_struct
	
	#Set in_motion to 1
	li	t5, 1
	sw	t5, in_motion
	
	#set bullet(x,y) = player(x,y)
	la	t2, player_struct
	la	t3, bullet_struct
	
	#Set player_x = bullet_x
	add	t5, t2, x_pos
	add	t6, t3, x_pos
	lw	t5,(t5)
	sw	t5, (t6)
	#Set player_y = bullet_y
	add	t5, t2, y_pos
	add	t6, t3, y_pos
	lw	t5, (t5)
	sw	t5, (t6)
	#Set player_dir = bullet_dir
	add	t5, t2, dir
	add	t6, t3, dir
	lw	t5, (t5)
	sw	t5, (t6)
	
	j	exit_player_update

pressed_up: 
	lw	t4, up_flag
	bgt	t4, 0, exit_player_update
	lw	t4, down_flag
	bgt	t4, 0, exit_player_update
	li	t4, 1
	sw	t4, up_flag
	
	
	j	exit_player_update
pressed_down:
	#add	s1, s0, y_pos #in the player struct, access the address of the y_pos variable
	#lw	s2, (s1) #load the y position into s2
	#jal	gravity_exists
	#add	s2, s2, 1
	#sw	s2, (s1)
	j	exit_player_update
pressed_right:#direction will be 2 when pressing left
	#check if the right of the player pos is available, move if so, do not move if there is a wall	
	add	s1, s0, x_pos
	lw	s2, (s1)
	bgt	s2, 56, exit_player_update #do not move right if at the edge of the arena

#Loop to check the entire right side of player
	li	t6, 0
check_right_side:
	la	a0, player_struct
	add	a0, a0, y_pos
	lw	a0, (a0)
	add	a0, a0, t6
	jal	find_block_col
	move	t3, v0 #J

	lw	a0, player_struct
	add	a0, a0, 3 #make it go to the end of the block
	jal	find_block_row
	move	t4, v0 #I
	
	la	t2, arena
	mul	t5, t3, ARENA_BLOCK_WIDTH
	add	t5, t5, t4
	add	t5, t5, t2
	lb	a0, (t5)
	beq	a0, 1, exit_player_update
	add	t6, t6, 1
	blt	t6, 5, check_right_side
#End of Loop
	move	a0, s0
	#Set the direction variable in the player_struct
	la	t0, player_struct
	add	t0, t0, dir
	li	t1, 1
	sw	t1, (t0)
	
	add	s1, s0, x_pos
	lw	s2, (s1)
	add	s2, s2, 1
	sw	s2, (s1)
	j	exit_player_update
pressed_left: #direction will be 0 when pressing left
	add	s1, s0, x_pos
	lw	s2, (s1)
	ble	s2, 2, exit_player_update #don't move left if at the edge of the board
	
#Start of Loop
	li	t6, 0
check_left_side:
	la	a0, player_struct
	add	a0, a0, y_pos
	lw	a0, (a0)
	add	a0, a0, t6
	jal	find_block_col
	move	t3, v0 #J
	
	lw	a0, player_struct
	sub	a0, a0,3 #make it go to the end of the block. but be careful if x_pos is 3 as this will produce value 0
	jal	find_block_row
	move	t4, v0 #I
	
	la	t2, arena
	mul	t5, t3, ARENA_BLOCK_WIDTH
	add	t5, t5, t4
	add	t5, t5, t2
	lb	a0, (t5)
	beq	a0, 1, exit_player_update
	add	t6, t6, 1
	blt	t6, 5, check_left_side
#End of Loop
	move	a0, s0
	la	t0, player_struct
	add	t0, t0, dir
	li	t1, 0
	sw	t1, (t0)
	
	add	s1, s0, x_pos
	lw	s2, (s1)
	sub	s2, s2, 1
	sw	s2, (s1)
	j	exit_player_update
jump_handler_exit: 
	
exit_player_update:
	leave	s0, s1, s2, s3

#Description: check if the player y_pos + 5 is equal to 1 in the matrix, if not, then keep going down one	
#a0 = base address of matrix
bring_player_down:
	enter s0, s1, s2, s3, s4
	lw	t5, down_flag
	beq	t5, 0, exit_bring_player_down #if down_flag is not set, then leave this function

	li	t6, 0
	move	s4, a0
#	li	t7, 0
#stall_loop:
#	add	t7, t7, 1
#	ble	t7, 10000, stall_loop
#start of loop
check_floor_loop:

	
	la	s0, player_struct
	la	s2, player_struct
	add	s2, s2, x_pos
	add	s0, s0, y_pos
	lw	s1, (s0) #s1 = *y_pos
	add	s1, s1, 5
	lw	s2, (s2) #s2 = *x_pos
	
	move	a0, s1
	jal	find_block_row
	move	s1, v0
	move	a0, s2
	sub	a0, a0, 2#
	add	a0, a0, t6
	jal	find_block_col
	move	s2, v0
	mul	s3, s1, ARENA_BLOCK_WIDTH
	add	s3, s3, s2
	add	s3, s3, s4 #s4 = I*WIDTH + J + Base
	lb	s0, (s3)
	beq	s0, 1, gravity_special_exit
	add	t6, t6, 1
	blt	t6, 5, check_floor_loop
move_player_and_exit:
	la	s1, player_struct
	add	s1, s1, y_pos
	lw	s2, (s1)
	add	s2, s2, 1
	sw	s2, (s1)
	j	exit_bring_player_down
#End of Loop
gravity_special_exit:
	li	t5, 0
	sw	t5, down_flag
exit_bring_player_down:
	leave s0, s1, s2, s3, s4

#Description: as long as nothing is above the player, the function will allow the player to move up
#a0 = base address of matrix
let_player_jump:
	enter s0, s1, s2, s3, s4, s5
	#Steps: 1. check if above the player is not a 1 in the matrix (set up_flag to 0 and down_flag to 1 otherwise) 
	#2. Check to make sure up_flag is less than 10, set up_flag to 0 and down_flag to 1 otherwise
	#if all else checks out, decrement the player y_pos and increase up_flag by 1
	lw	t4, up_flag
	beq	t4, 0, jump_conditions_not_met #if up_flag is 0, leave the function
	beq	t4, 19,jump_conditions_not_met
	
	move	s4, a0 #save the matrix base address
	la	s0, player_struct
	move	s1, s0
	add	s1, s1, y_pos
	lw	s2, (s1)
	beq	s2, 0, ceiling_reached
#Start of Loop
	li	t6, 0
check_row_above:
	move	s4, a0 #save the matrix base address
	la	s0, player_struct
	move	s1, s0
	add	s1, s1, y_pos
	lw	s2, (s1)
	beq	s2, 0, ceiling_reached
	sub	s2, s2, 1
	move	a0, s2
	jal	find_block_row
	move	s3, v0
	
	move	s1, s0
	add	s1, s1, x_pos
	lw	s2, (s1)
	move	a0, s2
	sub	a0, a0, 2
	add	a0, a0, t6
	jal	find_block_col
	move	s5, v0
	
	move	a0, s4
	mul	s2, s3, ARENA_BLOCK_WIDTH
	add	s2, s2, s5
	add	s2, s2, a0
	lb	s4, (s2)
	beq	s4, 1, jump_conditions_not_met
	add	t6, t6, 1
	blt	t6, 5, check_row_above
#End of Loop	
	
	la	s5, player_struct
	add	s5, s5, y_pos
	lw	s2, (s5)
	sub	s2, s2, 1
	sw	s2, (s5)
	lw	s2, up_flag
	add	s2, s2, 1
	sw	s2, up_flag
	j	exit_let_player_jump
jump_conditions_not_met:
	li	t5, 0
	sw	t5, up_flag
	li	t5, 1
	sw	t5, down_flag
	j	exit_let_player_jump
ceiling_reached:
	li	t5, 0
	sw	t5, up_flag
	li	t5, 1
	sw	t5, down_flag
	
exit_let_player_jump:
	leave s0, s1, s2, s3, s4 s5


#Description: Check to see if player is invincible, if true, then draw the player blinking, otherwise just regularly draw the player
#Inputs:
#a0 = base address of player
#a1 = address of player_sprite
#a2 = address of blank sprite
draw_player:
	enter s0, s1, s2
	
	move	s0, a0
	move	s1, a1
	move	s2, a2
	
	move	t0, s0
	add	t0, t0, inv
	lw	t1, (t0)
	beq	t1, 1, draw_player_invincible
	
	move	t0, s0
	add	t0, t0, x_pos
	lw	t1, (t0)
	move	a0, t1
	add	t0, t0, y_pos
	lw	t1, (t0)
	move	a1, t1
	move	a2, s1
	jal	display_blit_5x5_trans

	b	draw_player_exit
draw_player_invincible:
	lb	t6, timeinvi
	add	t6, t6, 1
	sb	t6, timeinvi
	lb	t6, inviflag
	beq	t6, 0, draw_player_regular
	lb	t6, timeinvi
	bge	t6, 30, draw_player_regular
	move	t0, s0
	add	t0, t0, x_pos
	lw	t1, (t0)
	move	a0, t1
	add	t0, t0, y_pos
	lw	t1, (t0)
	move	a1, t1
	move	a2, s2 #<-- loads the blank player_sprite to make it look like it is invincible
	jal	display_blit_5x5_trans
	li	t6, 0
	sb	t6, inviflag
	j	draw_player_exit
draw_player_regular:
	move	t0, s0
	add	t1, t0, x_pos
	lw	t1, (t1)
	move	a0, t1
	add	t1, t0, y_pos
	lw	t1, (t1)
	move	a1, t1
	move	a2, s1
	jal	display_blit_5x5_trans
	li	t6, 1
	sb	t6, inviflag
draw_player_exit:
	lb	t6, timeinvi
	blt	t6, 30, just_leave_already
	la	t6, player_struct
	add	t6, t6, inv
	li	s0, 0
	sw	s0, (t6)

	li	t6, 0
	sb	t6, inviflag
	sb	t6, timeinvi
just_leave_already:
	leave s0, s1, s2












#Description: Takes in an input and Finds the correct display_x value and puts into v0 the row of the block where that coordinate belongs.
#Inputs:
#a0= value between 2 and 61. **MUST ALWAYS BE BETWEEN THESE VALUES**
#Returns: v0 = row index in the matrix for the block
find_block_row:
	enter s0, s1, s2
	bgt	a0,61, exit_find_block_row
	blt	a0,2, exit_find_block_row
	blt	a0, 10 special_row_case_exit_0
	bgt	a0, 59, special_row_reset_case #DO NOT CHANGE the 59
	li	s2, 0
	move	s0, a0
	div 	s1, s0, 5 #divide the counter number by 5. Since this is Integer division, it will give the block in the specific section
#check if s1 > s2, if true --> add 1 to s2, do this until they are equal. then divide the answer by 5 and put into v0
check_loop:
	beq	s2, s1, exit_find_block_row
	add	s2, s2, 1 #add one pixel to s2
	j	check_loop
special_row_case_exit_0: #Integer division doesn't seem to work whenever number is 3-9 is divided by 5
	bge	a0, 5, special_row_case_exit_1
	li	s2, 0 
	j	exit_find_block_row
special_row_case_exit_1:
	li	s2, 1
	j	exit_find_block_row
special_row_reset_case:
	li	s2, 11
exit_find_block_row:
	move	v0, s2
	leave s0, s1, s2

#Description: Takes in an input and Finds the correct display_y value and puts into v0 the col of the block where that coordinate belongs.
#Inputs:
#a0= value between 0 and 54. **MUST ALWAYS BE BETWEEN THESE VALUES**
#Returns: v0 = row index in the matrix for the block
find_block_col:
	enter s0, s1, s2
	bgt	a0, 59, exit_find_block_col
	blt	a0, 0, exit_find_block_col
	blt	a0, 10 special_col_case_exit_0
	bgt	a0, 55, special_col_reset_case #DO NOT CHANGE the 59
	li	s2, 0
	move	s0, a0
	div 	s1, s0, 5 #divide the counter number by 5. Since this is Integer division, it will give the block in the specific section
check_col_loop:
	beq	s2, s1, exit_find_block_col
	add	s2, s2, 1 #add one pixel to s2
	j	check_col_loop
special_col_case_exit_0: #Integer division doesn't seem to work whenever number is 3-9 is divided by 5
	bge	a0, 5, special_col_case_exit_1
	li	s2, 0 
	j	exit_find_block_col
	
special_col_case_exit_1:
	li	s2, 1
	j	exit_find_block_col
	
special_col_reset_case:
	li	s2, 11
exit_find_block_col:
	move	v0, s2
	leave s0, s1, s2

	
	
	
	#shoot_right:
	#la	t4, player_struct
	#add	t5, t4, x_pos
	#lw	t5, (t5) #player_x
	#add	t5, t5, 4
	#add	t6, t4, y_pos
	#lw	t6, (t6)#player_y
	
#	move	a0, t5
	#move	a1, t6
	#la	a2, bullet_sprite
	#jal	fire_bullet
			
	
#	j	exit_player_update
