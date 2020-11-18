.include "convenience.asm"
.include "game_settings.asm"
.text
.globl enemy_movement
.globl collision_with_player
.globl draw_enemy




#Description: Makes enemy move left, if it hits a wall, then it moves right, if it goes off of the platform, then fall and continue moving left
#Inputs:
#a0 = base address of enemy
#a1 = address of arena
#TODO: create draw function, enable left/right checking. TEST. enable falling checking. TEST.
enemy_movement:
	enter s0, s1, s2, s3, s4
	move	s0, a1 #s0 = base address of arena
	move	s1, a0
	
	#move	a0, s1
	
	
	
	add	s2, s1, x_pos
	lw	s2, (s2)
	add	s3, s1, y_pos
	lw	s3, (s3)
	add	s4, s1, dir
	lw	s4, (s4)
	beq	s4, 0, check_left
	#if s4 is 1 then check_right
	beq	s2, 56, turn_around_right
	add	s2, s2, 2##########
	add	s3, s3, 5##########
	
	move	a0, s2
	jal	find_block_col
	move	s2, v0
	
	move	a0, s3
	jal	find_block_row
	move	s3, v0
	
	mul	s4, s3, ARENA_BLOCK_WIDTH
	add	s4, s4, s2
	add	s4, s4, s0
	lb	t2, (s4)
	beq	t2, 0, turn_around_right
	#check for the block
	add	s2, s1, x_pos
	add	s3, s1, y_pos
	lw	t2, (s2)
	add	t2, t2, 3#######
	lw	t3, (s3)
	
	move	a0, t2
	jal	find_block_col
	move	t2, v0
	
	move	a0, t3
	jal	find_block_row
	move	t3, v0
	
	mul	s4, t3, ARENA_BLOCK_WIDTH
	add	s4, s4, t2
	add	s4, s4, s0
	lb	s4, (s4)
	beq	s4, 1, turn_around_right
	
	#move right otherwise
	add	t2, s1, x_pos
	lw	t3, (t2)
	add	t3, t3, 1
	sw	t3, (t2)
	j	leave_enemy_movement
check_left:
	beq	s2, 2, turn_around_left
	sub	s2, s2, 2
	add	s3, s3, 5##########
	move	a0, s2
	jal	find_block_col
	move	s2, v0
	
	move	a0, s3	
	jal	find_block_row
	move	s3, v0
	
	mul	s4, s3, ARENA_BLOCK_WIDTH
	add	s4, s4, s2
	add	s4, s4, s0
	lb	t2, (s4)
	beq	t2, 0, turn_around_left
	#check for the block
	add	s2, s1, x_pos
	add	s3, s1, y_pos
	lw	t2, (s2)
	sub	t2, t2, 3#######
	lw	t3, (s3)
	
	move	a0, t2
	jal	find_block_col
	move	t2, v0
	
	move	a0, t3
	jal	find_block_row
	move	t3, v0
	
	mul	s4, t3, ARENA_BLOCK_WIDTH
	add	s4, s4, t2
	add	s4, s4, s0
	lb	s4, (s4)
	beq	s4, 1, turn_around_left
	#move	otherwise
	add	t2, s1, x_pos
	lw	t3, (t2)
	sub	t3, t3, 1
	sw	t3, (t2)
	j	leave_enemy_movement
turn_around_left:
	add	s2, s1, dir
	li	s3, 1
	sw	s3, (s2)
	j	leave_enemy_movement
turn_around_right:
	add	s2, s1, dir
	li	s3, 0
	sw	s3, (s2)
leave_enemy_movement:
	leave s0, s1, s2, s3, s4
	
	
	
	
	
#Description: Check if invincible is true in which case exit function,Check if the x,y of enemy are on the player's x,y 
#then clear the enemy and decrease a life from the player, also make player invincible.
#Inputs:
#a0 = base address of player
#a1 = base address of enemy
collision_with_player:
	enter s0, s1, s2, s3
	
	move	s0, a0
	move	s1, a1
	
	#Check to see if player invincible, exit if this is the case
	add	t0, s0, inv
	lw	t1, (t0)
	beq	t1, 1, leave_collision_with_player

	#if t1 is equal to 0, then check to see if (x,y) of enemy are the same as the player
	add	t0, s0, x_pos
	add	t1, s1, x_pos
	add	t2, s0, y_pos
	add	t3, s1, y_pos
	lw	t0, (t0)#t0 = player x
	lw	t1, (t1)#t1 = enemy x
	lw	t2, (t2)#t2 = player y
	lw	t3, (t3)#t3 = enemy y
	
	bne	t0, t1, not_collided
	bne	t3, t2, not_collided
	
	lb	t0, lives
	beq	t0, 0, its_over
	sub	t0, t0, 1
	sb	t0, lives
	add	t0, s0, inv
	li	t1, 1
	sw	t1, (t0)
	j	leave_collision_with_player
not_collided:

	j	leave_collision_with_player
its_over:
	li	t1, 1
	sb	t1, game_over
leave_collision_with_player:
	leave s0, s1, s2, s3
#Description: Draw the enemy at location (x.y) on the display
#Inputs:
#a0 = base address of enemy
#a1 = address of enemy sprite
draw_enemy:
	enter s0, s1
	
	 move	s0, a0
	 move	s1, a1
	
	 move	t0, s0
	 add	t0, t0, x_pos
	 lw	a0, (t0)
	 add	t0, t0, y_pos
	 lw	a1, (t0)
	 move	a2, s1
	 jal	display_blit_5x5_trans 
	 
	leave s0, s1

#Description: 
check_bullet_collision:
	
