.include "game_settings.asm"
.include "convenience.asm"
.globl draw_bullet
.globl bullet_update
.globl bullet_hit
.text


#Description: Draws the bullet which is four pixels long
#Inputs:
#a0 = x where to print
#a1 = y where to print
#a2 = address of bullet
draw_bullet:
	enter s0, s1, s2, s3, s4
	
	#Save arguments
	move	s0, a0
	move	s1, a1
	move	s2, a2
	
	li	t6, 0
bullet_loop:
	move	a0, s0
	move	a1, s1
	lb	a2, (s2)
	jal	display_set_pixel
	add	s0, s0, 1 #increase the x to draw the next pixel
	add	s2, s2, 1 #go to the next color in the bullet byte array
	add	t6, t6, 1
	blt	t6, 3, bullet_loop
leave_draw_bullet:	
	leave s0, s1, s2, s3, s4



#Description: Checks direction, then updates bullet in that direction which moves until it either hits the boundaries or an enemy.
#Inputs:
#a0 = address of bullet_sprite 
#a1 = address of bullet_struct
#a3 = addres of bullet_sprite_left
bullet_update:
	enter s0, s1, s2, s3, s4
	
	#Save arguments
	move	s0, a0 #s0 = bullet_sprite
	move	s1, a1 #s1 = bullet_struct
	
	lw	s2, in_motion
	beq	s2, 0, leave_bullet_update #don't update the bullet if it is 0
	
	add	s2, s1, x_pos
	add	s3, s1, y_pos
	add	s4, s1, dir
	lw	s2, (s2) #x
	lw	s3, (s3) #y
	lw	s4, (s4) #dir
	beq	s4, 1, move_right
	#move left if dir is 0
	sub	s2, s2, 1
	add	t1, s1, x_pos
	sw	s2, (t1)
	ble	s2, 2, reset_bullet
	move	a0, s2
	move	a1, s3
	move	a2, s0
	jal	draw_bullet
	j	leave_bullet_update
	
move_right:
	add	s2, s2, 1
	bge	s2, 60, reset_bullet
	add	t1, s1, x_pos
	sw	s2, (t1)
	move	a0, s2
	move	a1, s3
	la	a2, bullet_sprite_rev  
	jal	draw_bullet
	j	leave_bullet_update
	#Set in_motion to 0 if it hits a wall or enemy
reset_bullet:
	add	s2, s1, x_pos
	add	s3, s1, y_pos
	li	t0, -1
	sw	t0, (s2)
	sw	t0, (s3)
	li	t0, 0
	sw	t0, in_motion
leave_bullet_update:
	leave s0, s1, s2, s3, s4
	

#Description: Checks to see if the bullet has hit an enemy
#Inputs:
#a0 = address of enemy_struct
#a2 = address of enemies_left
#a1 = address of bullet_struct
bullet_hit:
	enter s0, s1, s2, s3, s4
	
	move	s0, a0 #s0 = enemy_struct
	move	s1, a1 #s1 = bullet_struct
	
	add	s2, s0, x_pos #enemy x
	lw	t0, (s2)
	
	add	s3, s0, y_pos #enemy y
	lw	t1, (s3)
	add	s4, s1, x_pos #bullet x
	lw	t2, (s4)
	add	t2, t2, 4
	add	t4, s1, y_pos #bullet y
	lw	t3, (t4)
	
	
	
	#test to see if bullet(x,y) = enemy(x,y)
	sub	t5, t0, t2 #t5 = enemy_x - bullet_x
	bgt	t5, -5, check_shot_right
	blt	t5, -5,leave_bullet_hit
	j	rest_of_code
check_shot_right:
	#sub	t5, t0, t2 #t5 = enemy_x - bullet_x
	bgt	t5, -4, leave_bullet_hit
	
rest_of_code:
	bne	t1, t3, leave_bullet_hit
	
	li	t6, 400 #get it off the screen
	sw	t6, (s2)
	li	t6, 600
	sw	t6, (s3)
	sw	t6, (s4)
	sw	t6, (t4)
	
	li	t6, 0
	sw	t6, in_motion
	li	a0, 12
	li	v0, 1
	syscall
	
	lb	t6, (a2)
	sub	t6, t6, 1
	sb	t6, (a2)
	
	
	j	leave_bullet_hit
	
leave_bullet_hit:
	leave s0, s1, s2, s3, s4
